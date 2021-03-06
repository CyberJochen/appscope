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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Unit tests for {@link AppScope}
 *
 * @author Inderjeet Singh
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, SharedPreferences.class })
public class AppScopeTest {

    private Context context;
    private static final Gson gson = new Gson();

    @Before
    public void setUp() {
        context = AndroidFixtures.createMockContext();
        AppScope.TestAccess.init(context, gson);
    }

    @Test
    public void booleanDefaultValueOnGet() {
        TypedKey<Boolean> testKey = new TypedKey<>("testKey", Boolean.class, false, false);
        AppScope.remove(testKey);
        assertFalse(AppScope.get(testKey));
        AppScope.bind(testKey, true);
        assertTrue(AppScope.get(testKey));
        AppScope.remove(testKey);
    }

    @Test
    public void testBind() throws Exception {
        TypedKey<String> key = new TypedKey<>("userId", String.class, false, false);
        assertNull(AppScope.get(key));
        AppScope.bind(key, "19999999999");
        assertNotNull(AppScope.get(key));
        assertEquals("19999999999", AppScope.get(key));
    }

    @Test
    public void testBindProvider() throws Exception {
        final InstanceProvider<String> instanceProvider = new InstanceProvider<String>() {
            private String value = "a";
            @Override public void update(String value) {
                this.value = value;
            }
            @Override public String get() {
                return value;
            }
        };
        TypedKey<String> key = new TypedKey<>("key", String.class, false, false);
        AppScope.bindProvider(key, instanceProvider);
        assertEquals("a", AppScope.get(key));
    }

    @Test
    public void testBindProviderCantBeUsedWithPersistableKeys() throws Exception {
        TypedKey<String> key = new TypedKey<>("key", String.class, false, true);
        try {
            AppScope.bindProvider(key, new InstanceProvider<String>() {
                private String value = "a";
                @Override public void update(String value) {
                    this.value = value;
                }
                @Override public String get() {
                    return value;
                }
            });
            fail();
        } catch (Exception expected) {}
    }

    @Test
    public void testBindProviderWithInstanceProvider() throws Exception {
        TypedKey<String> key = new TypedKey<String>("key", String.class, false, true) {
            @Override public InstanceProvider<String> getProvider() {
                return new InstanceProvider<String>() {
                    private String value = "a";
                    @Override public void update(String value) {
                        this.value = value;
                    }
                    @Override public String get() {
                        return value;
                    }
                };
            }
        };
        AppScope.bind(key, "b");
        assertEquals("b", AppScope.get(key));
    }

    @Test
    public void testBindIfNew() throws Exception {
        TypedKey<String> key = new TypedKey<>("userId", String.class, false, false);
        AppScope.bind(key, "19999999999");
        assertEquals("19999999999", AppScope.get(key));
        AppScope.bindIfAbsent(key, "16506953562");
        assertNotEquals("19999999999", "16506953562");
        assertNotEquals("16506953562", AppScope.get(key));
        assertEquals("19999999999", AppScope.get(key));
    }

    @Test
    public void testTestAccessReconfigure() throws Exception {
        TypedKey<String> key = new TypedKey<>("userId", String.class, false, false);
        AppScope.bind(key, "a");
        assertEquals("a", AppScope.get(key));
        AppScope.TestAccess.init(context, gson);
        assertFalse(AppScope.has(key));
    }

    @Test
    public void testReconfigureClearsPersistentProperties() throws Exception {
        TypedKey<String> key = new TypedKey<>("key", String.class, false, true);
        AppScope.bind(key, "a");
        assertEquals("a", AppScope.get(key));
        AppScope.TestAccess.init(context, gson);
        assertFalse(AppScope.has(key));
    }

    @Test
    public void testReconfigureClearsConfigProperties() throws Exception {
        TypedKey<String> key = new TypedKey<>("userId", String.class, true, true);
        AppScope.bind(key, "a");
        assertEquals("a", AppScope.get(key));
        AppScope.TestAccess.init(context, gson);
        assertFalse(AppScope.has(key));
    }
}
