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

package org.dalesbred.dialect;

import org.dalesbred.conversion.TypeConversionRegistry;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;

/**
 * Support for PostgreSQL.
 */
public class PostgreSQLDialect extends Dialect {

    @NotNull
    @Override
    public Function<Enum<?>, ?> createNativeEnumToDatabaseConversion(@NotNull String typeName) {
        return value -> createPgObject(value.name(), typeName);
    }

    @NotNull
    @Override
    public <T extends Enum<T>> Function<Object, T> createNativeEnumFromDatabaseConversion(@NotNull Class<T> enumType) {
        return value -> Enum.valueOf(enumType, value.toString());
    }

    @NotNull
    private Object createPgObject(@NotNull String value, @NotNull String typeName) {
        try {
            PGobject object = new PGobject();
            object.setType(typeName);
            object.setValue(value);
            return object;
        } catch (SQLException e) {
            throw convertException(e);
        }
    }

    @Override
    public void registerTypeConversions(@NotNull TypeConversionRegistry typeConversionRegistry) {
        typeConversionRegistry.registerConversionToDatabase(Date.class, v -> new Timestamp(v.getTime()));
    }
}