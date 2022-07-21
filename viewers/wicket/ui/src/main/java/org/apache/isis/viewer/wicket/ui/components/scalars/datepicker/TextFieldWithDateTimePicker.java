/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.viewer.wicket.ui.components.scalars.datepicker;

import java.util.Locale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.convert.IConverter;

import org.apache.isis.core.runtime.context.IsisAppCommonContext;
import org.apache.isis.viewer.wicket.model.converter.ConverterBasedOnValueSemantics;
import org.apache.isis.viewer.wicket.ui.components.scalars.datepicker.DateTimeConfig.TodayButton;

import static de.agilecoders.wicket.jquery.JQuery.$;

import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.DatetimePickerCssReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.DatetimePickerJsReference;
import lombok.val;

/**
 * A text input field that is used as a date or date/time picker.
 * It uses <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Bootstrap Datetime picker</a>
 * JavaScript widget
 *
 * @param <T> The type of the date/time
 */
public class TextFieldWithDateTimePicker<T>
extends TextField<T>
//extends AbstractDateTimePickerWithIcon<T>
implements IConverter<T> {

    private static final long serialVersionUID = 1L;

    protected final IConverter<T> converter;

    private final DateTimeConfig config;

    public TextFieldWithDateTimePicker(
            final IsisAppCommonContext commonContext,
            final String id,
            final IModel<T> model,
            final Class<T> type,
            final IConverter<T> converter) {

        super(id, model, type);
        setOutputMarkupId(true);

        this.converter = converter;

        /* debug
                new IConverter<T>() {

            @Override
            public T convertToObject(final String value, final Locale locale) throws ConversionException {
                System.err.printf("convertToObject %s%n", value);
                try {
                    val obj = converter.convertToObject(value, locale);
                    System.err.printf("convertedToObject %s%n", obj);
                    return obj;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String convertToString(final T value, final Locale locale) {
                val s =  converter.convertToString(value, locale);
                System.err.printf("convertedToString %s%n", s);
                return s;
            }
        };
        */

        val cfv5 = new DatetimePickerConfig();

        val config = new DateTimeConfig();

        // FIXME[ISIS-3085] some of the config options were already broken in TDv5, needs fixes for TDv6

        // if this text field is for a LocalDate, then the pattern obtained will just be a simple date format
        // (with no hour/minute components).
        final String dateTimePattern = ((ConverterBasedOnValueSemantics<T>)converter).getEditingPattern();
        config.withFormat(_TimeFormatUtil.convertToMomentJsFormat(dateTimePattern));
        config.calendarWeeks(true);
        config.useCurrent(false);
        config.minDate(commonContext.getConfiguration().getViewer().getWicket().getDatePicker().getMinDate());
        config.maxDate(commonContext.getConfiguration().getViewer().getWicket().getDatePicker().getMaxDate());
        config.readonly(!this.isEnabled());
        config.highlightToday(true);
        config.showTodayButton(TodayButton.TRUE);
        config.clearButton(isInputNullable());
        config.closeButton(true);
        config.allowKeyboardNavigation(true);

        this.config = config;

        //XXX ISIS-2834
        //Adding OnChangeAjaxBehavior registers a JavaScript event listener on change event.
        //Since OnChangeAjaxBehavior extends AjaxFormComponentUpdatingBehavior the Ajax request
        // also updates the Wicket model for this form component on the server side.
        // onUpdate() is a callback method that you could use to do something more or don't do anything
        add(new OnChangeAjaxBehavior() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                // nothing to do
            }
        });
    }

    @Override
    public T convertToObject(final String value, final Locale locale) {
        return converter.convertToObject(value, locale);
    }

    @Override
    public String convertToString(final T value, final Locale locale) {
        return converter.convertToString(value, locale);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(final Class<C> type) {
        // we use isAssignableFrom rather than a simple == to handle
        // the persistence of JDO/DataNucleus:
        // if persisting a java.sql.Date, the object we are given is actually a
        // org.datanucleus.store.types.simple.SqlDate (a subclass of java.sql.Date)
        if (super.getType().isAssignableFrom(type)) {
            return (IConverter<C>) this;
        }
        return super.getConverter(type);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        if(!isEnabled()) {
            return;
        }

        checkComponentTag(tag, "input");
        Attributes.set(tag, "type", "text");

        Attributes.addClass(tag, "datetimepicker-input");
        Attributes.set(tag, "data-toggle", "datetimepicker");
        Attributes.set(tag, "data-target", getMarkupId());
        Attributes.set(tag, "autocomplete", "off");
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);

        if(!isEnabled()) {
            return;
        }

        config.readonly(false);

        //TODO legacy calendar icon customization .. should now be possible via TD config
        response.render(CssHeaderItem.forReference(
                new CssResourceReference(TextFieldWithDateTimePicker.class, "css/tempusdominus-fa-patch.css")));

        response.render(DatetimePickerCssReference.asHeaderItem());
        response.render(DatetimePickerJsReference.asHeaderItem());

        response.render(OnDomReadyHeaderItem.forScript(createScript(config)));

        //TODO using new config in DatetimePickerBehavior (wicket-stuff)
        //response.render($(component).chain("datetimepicker", config).asDomReadyScript());
    }


    /**
     * creates the initializer script.
     *
     * @return initializer script
     */
    private CharSequence createScript(final DateTimeConfig config) {
        return $(this).chain("datetimepicker", config).get();
    }

}
