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
package org.apache.isis.commons.functional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.springframework.lang.Nullable;

import org.apache.isis.commons.internal.exceptions._Exceptions;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * The {@link Try} type represents a value of one of two possible types (a disjoint union)
 * of {@link Success} or {@link Failure}.
 * <p>
 * Factory methods {@link Try#success(Object)} and {@link Try#failure(Throwable)}
 * correspond to the two possible values.
 *
 * @since 2.0 {@index}
 */
public interface Try<T> {

    // -- FACTORIES

    public static <T> Try<T> call(final @NonNull Callable<T> callable) {
        try {
            return success(callable.call());
        } catch (Throwable e) {
            return failure(e);
        }
    }

    public static Try<Void> run(final @NonNull ThrowingRunnable runnable) {
        try {
            runnable.run();
            return success(null);
        } catch (Throwable e) {
            return failure(e);
        }
    }

    public static <T> Success<T> success(final @Nullable T value) {
        return new Success<>(value);
    }

    public static <T> Failure<T> failure(final @NonNull Throwable throwable) {
        return new Failure<T>(throwable);
    }

    // -- PREDICATES

    boolean isSuccess();
    boolean isFailure();

    // -- ACCESSORS

    /**
     * Optionally returns the contained {@code value} based on presence,
     * this is, if its a {@link Success} and the value is not {@code null}.
     */
    Optional<T> getValue();
    /**
     * Optionally returns the contained {@code failure} based on presence,
     * this is, if its a {@link Failure}.
     */
    Optional<Throwable> getFailure();

    // -- PEEKING

    /**
     * Peeks into the {@code value} if its a {@link Success}.
     */
    Try<T> ifSuccess(final @NonNull Consumer<Optional<T>> valueConsumer);
    /**
     * Peeks into the {@code failure} if its a {@link Failure}.
     */
    Try<T> ifFailure(final @NonNull Consumer<Throwable> exceptionConsumer);

    // -- FAIL EARLY

    /** Throws the contained failure if any. */
    Try<T> ifFailureFail();
    /** Throws {@link NoSuchElementException} if {@code value} is {@code null}. */
    Try<T> ifAbsentFail();

    // -- MAPPING

    /**
     * Maps this {@link Try} to another if its a {@link Success}.
     * Otherwise if its a {@link Failure} acts as identity operator.
     */
    <R> Try<R> mapSuccess(final @NonNull Function<T, R> successMapper);
    /**
     * Maps this {@link Try} to another if its a {@link Failure}.
     * Otherwise if its a {@link Success} acts as identity operator.
     */
    Try<T> mapFailure(final @NonNull UnaryOperator<Throwable> failureMapper);
    /**
     * Maps this {@link Try} to {@link Failure} if its a {@link Success} with an empty {@code value}.
     * Otherwise acts as identity operator.
     */
    Try<T> mapEmptyToFailure();

    // -- FOLDING

    /**
     * Folds the contained {@code value} or {@code failure} to a new value of type {@code R}
     * using according mapping function {@code successMapper} or {@code failureMapper}.
     */
    <R> R fold(
            final @NonNull Function<Optional<T>, R> successMapper,
            final @NonNull Function<Throwable, R> failureMapper);

    // -- CONCATENATION

    /**
     * If its a {@link Success}, maps it to another based on given {@link Callable}.
     * Otherwise if its a {@link Failure} acts as identity operator.
     */
    <R> Try<R> thenCall(final @NonNull Callable<R> callable);
    /**
     * If its a {@link Success}, maps it to another based on given {@link ThrowingRunnable}.
     * Otherwise if its a {@link Failure} acts as identity operator.
     */
    Try<Void> thenRun(final @NonNull ThrowingRunnable runnable);

    // -- SUCCESS

    @lombok.Value
    @RequiredArgsConstructor
    final class Success<T> implements Try<T> {

        private final @Nullable T value;

        @Override public boolean isSuccess() { return true; }
        @Override public boolean isFailure() { return false; }

        @Override public Optional<T> getValue() { return Optional.ofNullable(value); }
        @Override public Optional<Throwable> getFailure() { return Optional.empty(); }

        @Override
        public Success<T> ifSuccess(final @NonNull Consumer<Optional<T>> valueConsumer) {
            valueConsumer.accept(getValue());
            return this;
        }

        @Override
        public Success<T> ifFailure(final @NonNull Consumer<Throwable> exceptionConsumer) {
            return this;
        }

        @Override
        public Success<T> ifFailureFail() {
            return this;
        }

        @Override
        public Success<T> ifAbsentFail() {
            if(value==null) throw _Exceptions.noSuchElement();
            return this;
        }

        @Override
        public <R> Try<R> mapSuccess(final @NonNull Function<T, R> successMapper){
            return Try.call(()->successMapper.apply(value));
        }

        @Override
        public Success<T> mapFailure(final @NonNull UnaryOperator<Throwable> failureMapper){
            return this;
        }

        @Override
        public Try<T> mapEmptyToFailure() {
            return value!=null
                    ? this
                    : Try.failure(_Exceptions.noSuchElement());
        }

        @Override
        public <R> Try<R> thenCall(final @NonNull Callable<R> callable) {
            return Try.call(callable);
        }

        @Override
        public Try<Void> thenRun(final @NonNull ThrowingRunnable runnable) {
            return Try.run(runnable);
        }

        @Override
        public <R> R fold(
                final @NonNull Function<Optional<T>, R> successMapper,
                final @NonNull Function<Throwable, R> failureMapper) {
            return successMapper.apply(getValue());
        }

    }

    // -- FAILURE

    @lombok.Value
    @RequiredArgsConstructor
    final class Failure<T> implements Try<T> {

        private final @NonNull Throwable throwable;

        @Override public boolean isSuccess() { return false; }
        @Override public boolean isFailure() { return true; }

        @Override public Optional<T> getValue() { return Optional.empty(); }
        @Override public Optional<Throwable> getFailure() { return Optional.of(throwable); }

        @Override
        public Failure<T> ifSuccess(final @NonNull Consumer<Optional<T>> valueConsumer) {
            return this;
        }

        @Override
        public Failure<T> ifFailure(final @NonNull Consumer<Throwable> exceptionConsumer) {
            exceptionConsumer.accept(throwable);
            return this;
        }

        @Override @SneakyThrows
        public Failure<T> ifFailureFail() {
            throw throwable;
        }

        @Override @SneakyThrows
        public Failure<T> ifAbsentFail() {
            throw _Exceptions.noSuchElement();
        }

        @Override
        public <R> Failure<R> mapSuccess(final @NonNull Function<T, R> successMapper){
            return new Failure<>(throwable);
        }

        @Override
        public Failure<T> mapFailure(final @NonNull UnaryOperator<Throwable> failureMapper){
            try {
                return new Failure<>(failureMapper.apply(throwable));
            } catch (Throwable e) {
                return failure(e);
            }
        }

        @Override
        public Try<T> mapEmptyToFailure() {
            return this;
        }

        @Override
        public <R> Failure<R> thenCall(final @NonNull Callable<R> callable) {
            return new Failure<>(throwable);
        }

        @Override
        public Try<Void> thenRun(final @NonNull ThrowingRunnable runnable) {
            return new Failure<>(throwable);
        }

        @Override
        public <R> R fold(
                final @NonNull Function<Optional<T>, R> successMapper,
                final @NonNull Function<Throwable, R> failureMapper) {
            return failureMapper.apply(throwable);
        }

    }

}
