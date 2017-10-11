package com.imcode.imcms.util.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 * <p>
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <I1> the type of the first argument to the function
 * @param <I2> the type of the second argument to the function
 * @param <I3> the type of the third argument to the function
 * @param <O>  the type of the result of the function
 * @author Serhii Maksymchuk
 * @since 6.0.0
 */
@FunctionalInterface
public interface TernaryFunction<I1, I2, I3, O> {

    /**
     * Applies this function to the given arguments.
     *
     * @param input1 the first function argument
     * @param input2 the second function argument
     * @param input3 the third function argument
     * @return the function result
     */
    O apply(I1 input1, I2 input2, I3 input3);

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V>   the type of output of the {@code after} function, and of the
     *              composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> TernaryFunction<I1, I2, I3, V> andThen(Function<? super O, ? extends V> after) {
        Objects.requireNonNull(after);
        return (I1 input1, I2 input2, I3 input3) -> after.apply(apply(input1, input2, input3));
    }
}
