/*
 * Copyright (C) 2017 Peel Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peel.appscope;

import java.lang.reflect.Type;

/**
 * A typed key
 *
 * @param <T> Intended type of the content for the key
 * @author Inderjeet Singh
 */
public class TypedKey<T> {

    public static <R> TypedKey<R> of(String name, Type type, boolean config, boolean persist) {
        return new TypedKey<R>(type, name, config, persist);
    }

    private final String name;
    private final Type type;
    private final boolean config;
    private final boolean persist;

    /**
     * @param name Ensure that this name is Unique.
     * @param clazz the type of this key
     */
    public TypedKey(String name, Class<T> clazz) {
        this(name, clazz, false, false);
    }

    /**
     * @param name Ensure that this name is unique
     * @param clazz the type of this key
     * @param config If true, this property is considered to be an app configuration property
     *   that will not be erased on app reset.
     * @param persist whether to save this property on disk as JSON and reload on app restart.
     */
    public TypedKey(String name, Class<T> clazz, boolean config, boolean persist) {
        this.name = name;
        this.type = clazz;
        this.config = config;
        this.persist = persist;
    }

    private TypedKey(Type type, String name, boolean config, boolean persist) {
        this.name = name;
        this.type = type;
        this.config = config;
        this.persist = persist;
    }

    public String getName() {
        return name;
    }

    public Type getTypeOfValue() {
        return type;
    }

    public boolean isConfigType() {
        return config;
    }

    public boolean isPersistable() {
        return persist;
    }

    public boolean hasProvider() {
        return getProvider() != null;
    }

    /**
     * Override this method to provide a custom provider
     * @return the custom {@code InstanceProvider} associated with this key
     */
    public InstanceProvider<T> getProvider() {
        return null;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        TypedKey<?> other = (TypedKey<?>) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
