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
package org.apache.causeway.viewer.wicket.ui.components.table.nav.pageact;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import org.apache.causeway.applib.services.i18n.TranslationContext;
import org.apache.causeway.core.metamodel.context.MetaModelContext;
import org.apache.causeway.viewer.wicket.model.tableoption.PageActionChoice;
import org.apache.causeway.viewer.wicket.ui.components.table.DataTableWithPagesAndFilter;
import org.apache.causeway.viewer.wicket.ui.components.widgets.links.AjaxLinkNoPropagate;
import org.apache.causeway.viewer.wicket.ui.util.Wkt;
import org.apache.causeway.viewer.wicket.ui.util.WktLinks;
import org.apache.causeway.viewer.wicket.ui.util.WktTooltips;

import lombok.Getter;
import lombok.NonNull;

public class PageActionChooser extends Panel {
    private static final long serialVersionUID = 1L;

    //private static final String ID_ENTRIES_PER_PAGE_LABEL = "entriesPerPageLabel";
    private static final String ID_PAGE_ACTION_CHOICE = "pageActionChoice";
    private static final String ID_PAGE_ACTION_CHOICES = "pageActionChoices";

    private static final String ID_VIEW_ITEM_TITLE = "viewItemTitle";
    private static final String ID_VIEW_ITEM_ICON = "viewItemIcon";

    @Getter final DataTableWithPagesAndFilter<?, ?> table;

    public PageActionChooser(final String id, final DataTableWithPagesAndFilter<?, ?> table) {
        super(id);
        this.table = table;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        buildGui();
    }

    // -- HELPER

    private void buildGui() {

        //Wkt.labelAdd(this, ID_ENTRIES_PER_PAGE_LABEL, table.getEntriesPerPageAsLiteral());

        Wkt.listViewAdd(this, ID_PAGE_ACTION_CHOICES, table.getPageActionChoices(), item->{
            var link = Wkt.linkAdd(item, ID_PAGE_ACTION_CHOICE, target->{
                var pageActionChoice = item.getModelObject();
                //FIXME just a mockup
                switch(pageActionChoice.getKey()) {
                case "PAGE_SEL": { 
                    break;
                }
                case "PAGE_UNSEL": { 
                    break;
                }
                default: 
                    return; // ignore
                }
                System.err.printf("pageActionChoice: %s%n", pageActionChoice.getKey());
                //table.setItemsPerPage(pagesizeChoice.getItemsPerPage());
                //table.setPageSizeHintAndBroadcast(target);
                target.add(table);
            });
            // add title and icon to the link
            addIconAndTitle(item, link);
            // add checkmark to the link
            //addCheckmark(item, link, table.getCurrentPagesizeChoice());

            Wkt.ajaxEnable(link);
        });
        
        WktTooltips.addTooltip(this, translate("Execute a page specific action."));

    }
    
    private String translate(String text) {
        return MetaModelContext.translationServiceOrFallback()
                .translate(TranslationContext.named("Table"), text);
    }

    private static void addIconAndTitle(
            final @NonNull ListItem<PageActionChoice> item,
            final @NonNull AjaxLinkNoPropagate link) {
        WktLinks.listItemAsDropdownLink(item, link,
                ID_VIEW_ITEM_TITLE, pagesizeChoice->Model.of(pagesizeChoice.getTitle()),
                ID_VIEW_ITEM_ICON, pagesizeChoice->Model.of(pagesizeChoice.getCssClass()),
                null);
    }

//    private static void addCheckmark(
//            final @NonNull ListItem<PagesizeChoice> item,
//            final @NonNull AjaxLinkNoPropagate link,
//            final @NonNull Optional<PagesizeChoice> currentPagesizeChoice) {
//        var checkmarkForChoice = Wkt.labelAdd(link, ID_VIEW_ITEM_CHECKMARK, "");
//
//        // whether the item in this loop is also the currently selected one (is to receive the checkmark)
//        var isSelected = currentPagesizeChoice
//                .map(currentChoice->
//                    currentChoice.equals(item.getModelObject()))
//                .orElse(false);
//        checkmarkForChoice.setVisible(isSelected);
//
//        // disable the link when selected
//        if(isSelected) {
//            link.setEnabled(false);
//        }
//    }

}
