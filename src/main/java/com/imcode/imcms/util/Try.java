package com.imcode.imcms.util;

import java.util.Optional;
import java.util.concurrent.Callable;

// Scala parser does not support static interface methods - fix version 2.12
// https://issues.scala-lang.org/browse/SI-8355
public abstract class Try<T> {

//    public interface F1<T, R> {
//        Try<R> execute(T value) throws Exception;
//    }
//
//    public static class ExecutionException extends RuntimeException {
//        public ExecutionException(Throwable cause) {
//            super(cause);
//        }
//    }
//
//    public abstract T get() throws ExecutionException;
//
//    public boolean isFailure() {
//        return exception().isPresent();
//    }
//
//    public boolean isSuccess() {
//        return !isFailure();
//    }
//
//    public abstract Optional<Throwable> exception();
//
//    public <R> Try<R> flatMap(F1<? super T, R> f1) {
//        return isFailure() ? this : f1.execute(get());
//    }
//
//    public static <T> Try<T> execute(Callable<? extends T> callable) {
//        try {
//            T result = callable.call();
//
//            return new Try<T>() {
//                @Override
//                public T get() {
//                    return result;
//                }
//
//                @Override
//                public Optional<Throwable> exception() {
//                    return Optional.empty();
//                }
//            };
//        } catch (Throwable t) {
//            return new Try<T>() {
//                @Override
//                public T get() {
//                    throw new ExecutionException(t);
//                }
//
//                @Override
//                public Optional<Throwable> exception() {
//                    return Optional.of(t);
//                }
//            };
//        }
//    }
}