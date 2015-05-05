/*
 * Copyright (c) 2015 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.dalesbred.internal.instantiation;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class ConversionMapTest {

    private final ConversionMap registry = new ConversionMap();

    @Test
    public void searchingForNotExistingItemReturnsNull() {
        assertThat(registry.findConversion(Integer.class, String.class), is(Optional.empty()));
    }

    @Test
    public void searchBasedOnExactMatch() {
        TypeConversion conversion = dummyConversion(Integer.class, String.class);
        registry.register(conversion);

        assertSame(conversion, registry.findConversion(Integer.class, String.class).orElse(null));
    }

    @Test
    public void searchBasedOnResultCovariance() {
        TypeConversion conversion = dummyConversion(Integer.class, String.class);
        registry.register(conversion);

        assertSame(conversion, registry.findConversion(Integer.class, Object.class).orElse(null));
    }

    @Test
    public void searchBasedOnParamContravariance() {
        TypeConversion conversion = dummyConversion(Number.class, String.class);
        registry.register(conversion);

        assertSame(conversion, registry.findConversion(Integer.class, String.class).orElse(null));
    }

    @Test
    public void primitivesAndWrappersAreConsideredSame() {
        TypeConversion conversion = dummyConversion(Integer.class, Long.class);
        registry.register(conversion);

        assertSame(conversion, registry.findConversion(int.class, long.class).orElse(null));
    }

    @Test
    public void sourceContravarianceOnInterfaces() {
        TypeConversion conversion = dummyConversion(CharSequence.class, Long.class);
        registry.register(conversion);

        assertSame(conversion, registry.findConversion(String.class, Long.class).orElse(null));
    }

    @Test
    public void laterAdditionsOverrideEarlierOnes() {
        TypeConversion conversion1 = dummyConversion(String.class, Long.class);
        TypeConversion conversion2 = dummyConversion(String.class, Long.class);
        registry.register(conversion1);
        registry.register(conversion2);

        assertSame(conversion2, registry.findConversion(String.class, Long.class).orElse(null));
    }

    @NotNull
    private static TypeConversion dummyConversion(@NotNull Class<?> source, @NotNull Class<?> target) {
        return TypeConversion.fromNonNullFunction(source, target, x -> { throw new UnsupportedOperationException(); });
    }
}
