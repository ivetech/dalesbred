/*
 * Copyright (c) 2017 Evident Solutions Oy
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

package org.dalesbred.query

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VariableResolversTest {

    @Test
    fun testProviderForMap() {
        val parameterMap = mutableMapOf<String, Any>()

        val foo = Any()
        val bar = Any()
        val baz = Any()

        parameterMap.put("foo", foo)
        parameterMap.put("bar", bar)
        parameterMap.put("baz", baz)

        val variableResolver = VariableResolver.forMap(parameterMap)

        assertEquals(foo, variableResolver.getValue("foo"))
        assertEquals(bar, variableResolver.getValue("bar"))
        assertEquals(baz, variableResolver.getValue("baz"))
    }

    @Test
    fun testProviderForBean() {
        val bean = TestBean()
        val variableResolver = VariableResolver.forBean(bean)

        assertEquals(bean.foo, variableResolver.getValue("foo"))
        assertEquals(bean.isBar, variableResolver.getValue("bar"))
        assertEquals(bean.baz, variableResolver.getValue("baz"))
    }

    @Test
    fun resolvingUnknownVariableThrowsException() {
        val variableResolver = VariableResolver.forBean(TestBean())

        assertFailsWith<VariableResolutionException> {
            variableResolver.getValue("unknown")
        }
    }

    @Test
    fun resolvingPrivateVariableThrowsException() {
        val variableResolver = VariableResolver.forBean(TestBean())

        assertFailsWith<VariableResolutionException> {
            variableResolver.getValue("privateVariable")
        }
    }

    @Test
    fun variableThrowingIsWrappedInVariableResolutionException() {
        val variableResolver = VariableResolver.forBean(TestBean())

        assertFailsWith<VariableResolutionException> {
            variableResolver.getValue("throwingVariable")
        }
    }

    @Test
    fun mapResolverForUnknownMapKey() {
        val variableResolver = VariableResolver.forMap(emptyMap<String, Any>())

        assertFailsWith<VariableResolutionException> {
            variableResolver.getValue("unknown")
        }
    }

    @Suppress("unused")
    private class TestBean {
        var foo = Any()
        val isBar = true
        val baz = "qwerty"
        private val privateVariable = "foo"

        val throwingVariable: String
            get() = throw RuntimeException()
    }
}
