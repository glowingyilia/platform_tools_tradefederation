/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tradefed.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * Unit tests for {@link OptionSetter}.
 */
public class OptionParserTest extends TestCase {

    /** Option source with generic type. */
    private static class GenericTypeOptionSource {
        @SuppressWarnings("unused")
        @Option(name = "my_option", shortName = 'o')
        private Collection<?> mMyOption;
    }

    /** Option source with unparameterized type. */
    private static class CollectionTypeOptionSource {
        @SuppressWarnings( {
                "unused", "unchecked"
        })
        @Option(name = "my_option", shortName = 'o')
        private Collection mMyOption;
    }

    private static class MyGeneric<T> {
    }

    /** Option source with unparameterized type. */
    private static class NonCollectionGenericTypeOptionSource {
        @SuppressWarnings("unused")
        @Option(name = "my_option", shortName = 'o')
        private MyGeneric<String> mMyOption;
    }

    /** Option source with an option with same name as AllTypesOptionSource. */
    private static class DuplicateOptionSource {
        @SuppressWarnings("unused")
        @Option(name = "string", shortName = 's')
        private String mMyOption;
    }

    /** option source with all supported types. */
    private static class AllTypesOptionSource {
        @Option(name = "string_collection")
        private Collection<String> mStringCollection = new ArrayList<String>();

        @Option(name = "string")
        private String mString = null;

        @Option(name = "boolean")
        private boolean mBool = false;

        @Option(name = "booleanObj")
        private Boolean mBooleanObj = false;

        @Option(name = "byte")
        private byte mByte = 0;

        @Option(name = "byteObj")
        private Byte mByteObj = 0;

        @Option(name = "short")
        private short mShort = 0;

        @Option(name = "shortObj")
        private Short mShortObj = null;

        @Option(name = "int")
        private int mInt = 0;

        @Option(name = "intObj")
        private Integer mIntObj = 0;

        @Option(name = "long")
        private long mLong = 0;

        @Option(name = "longObj")
        private Long mLongObj = null;

        @Option(name = "float")
        private float mFloat = 0;

        @Option(name = "floatObj")
        private Float mFloatObj = null;

        @Option(name = "double")
        private double mDouble = 0;

        @Option(name = "doubleObj")
        private Double mDoubleObj = null;

        @Option(name = "file")
        private File mFile = null;
    }

    /**
     * Test creating an {@link OptionSetter} for a source with invalid option type.
     */
    public void testOptionParser_noType() {
        try {
            new OptionSetter(new GenericTypeOptionSource());
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test creating an {@link OptionSetter} for a source with duplicate names.
     */
    public void testOptionParser_duplicateOptions() {
        try {
            new OptionSetter(new AllTypesOptionSource(), new DuplicateOptionSource());
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test creating an {@link OptionSetter} for a Collection with no type.
     */
    public void testOptionParser_unparamType() {
        try {
            new OptionSetter(new CollectionTypeOptionSource());
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test creating an {@link OptionSetter} for a non collection option with generic type
     */
    public void testOptionParser_genericType() {
        try {
            new OptionSetter(new NonCollectionGenericTypeOptionSource());
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test {@link OptionSetter#isBooleanOption(String)} when passed an unknown option name
     */
    public void testIsBooleanOption_unknown() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        try {
            parser.isBooleanOption("unknown");
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test {@link OptionSetter#isBooleanOption(String)} when passed boolean option name
     */
    public void testIsBooleanOption_true() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        assertTrue(parser.isBooleanOption("boolean"));
    }

    /**
     * Test {@link OptionSetter#isBooleanOption(String)} when passed boolean option name for a
     * Boolean object
     */
    public void testIsBooleanOption_objTrue() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        assertTrue(parser.isBooleanOption("booleanObj"));
    }

    /**
     * Test {@link OptionSetter#isBooleanOption(String)} when passed non-boolean option
     */
    public void testIsBooleanOption_false() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        assertFalse(parser.isBooleanOption("string"));
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} when passed an unknown option name
     */
    public void testSetOptionValue_unknown() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        try {
            parser.setOptionValue("unknown", "foo");
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test setting a value for a option with an unknown generic type.
     */
    public void testSetOptionValue_unknownType() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        try {
            parser.setOptionValue("my_option", "foo");
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test setting a value for a non-parameterized Collection
     */
    public void testSetOptionValue_unparameterizedType() throws ConfigurationException {
        OptionSetter parser = new OptionSetter(new AllTypesOptionSource());
        try {
            parser.setOptionValue("my_option", "foo");
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a String.
     */
    public void testSetOptionValue_string() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        final String expectedValue = "stringvalue";
        assertSetOptionValue(optionSource, "string", expectedValue);
        assertEquals(expectedValue, optionSource.mString);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Collection.
     */
    public void testSetOptionValue_collection() throws ConfigurationException, IOException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        final String expectedValue = "stringvalue";
        assertSetOptionValue(optionSource, "string_collection", expectedValue);
        assertEquals(1, optionSource.mStringCollection.size());
        assertTrue(optionSource.mStringCollection.contains(expectedValue));
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a boolean.
     */
    public void testSetOptionValue_boolean() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "boolean", "true");
        assertEquals(true, optionSource.mBool);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a boolean for a non-boolean
     * value.
     */
    public void testSetOptionValue_booleanInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "boolean", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Boolean.
     */
    public void testSetOptionValue_booleanObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "booleanObj", "true");
        assertTrue(optionSource.mBooleanObj);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a byte.
     */
    public void testSetOptionValue_byte() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "byte", "2");
        assertEquals(2, optionSource.mByte);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a byte for an invalid value.
     */
    public void testSetOptionValue_byteInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "byte", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Byte.
     */
    public void testSetOptionValue_byteObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "byteObj", "2");
        assertTrue(2 == optionSource.mByteObj);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a short.
     */
    public void testSetOptionValue_short() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "short", "2");
        assertTrue(2 == optionSource.mShort);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Short.
     */
    public void testSetOptionValue_shortObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "shortObj", "2");
        assertTrue(2 == optionSource.mShortObj);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a short for an invalid value.
     */
    public void testSetOptionValue_shortInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "short", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a int.
     */
    public void testSetOptionValue_int() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "int", "2");
        assertTrue(2 == optionSource.mInt);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Integer.
     */
    public void testSetOptionValue_intObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "intObj", "2");
        assertTrue(2 == optionSource.mIntObj);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a int for an invalid value.
     */
    public void testSetOptionValue_intInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "int", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a long.
     */
    public void testSetOptionValue_long() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "long", "2");
        assertTrue(2 == optionSource.mLong);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Long.
     */
    public void testSetOptionValue_longObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "longObj", "2");
        assertTrue(2 == optionSource.mLongObj);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a long for an invalid value.
     */
    public void testSetOptionValue_longInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "long", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a float.
     */
    public void testSetOptionValue_float() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "float", "2.1");
        assertEquals(2.1, optionSource.mFloat, 0.01);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Float.
     */
    public void testSetOptionValue_floatObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "floatObj", "2.1");
        assertEquals(2.1, optionSource.mFloatObj, 0.01);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a float for an invalid value.
     */
    public void testSetOptionValue_floatInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "float", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a float.
     */
    public void testSetOptionValue_double() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "double", "2.1");
        assertEquals(2.1, optionSource.mDouble, 0.01);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a Float.
     */
    public void testSetOptionValue_doubleObj() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValue(optionSource, "doubleObj", "2.1");
        assertEquals(2.1, optionSource.mDoubleObj, 0.01);
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a double for an invalid value.
     */
    public void testSetOptionValue_doubleInvalid() throws ConfigurationException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        assertSetOptionValueInvalid(optionSource, "double", "blah");
    }

    /**
     * Test {@link OptionSetter#setOptionValue(String, String)} for a File.
     */
    public void testSetOptionValue_file() throws ConfigurationException, IOException {
        AllTypesOptionSource optionSource = new AllTypesOptionSource();
        File tmpFile = File.createTempFile("testSetOptionValue_file", "txt");
        assertSetOptionValue(optionSource, "file", tmpFile.getAbsolutePath());
        assertEquals(tmpFile.getAbsolutePath(), optionSource.mFile.getAbsolutePath());
    }

    /**
     * Perform {@link OptionSetter#setOptionValue(String, String)} for a given option.
     */
    private void assertSetOptionValue(AllTypesOptionSource optionSource, final String optionName,
            final String expectedValue) throws ConfigurationException {
        OptionSetter parser = new OptionSetter(optionSource);
        parser.setOptionValue(optionName, expectedValue);
    }

    /**
     * Perform {@link OptionSetter#setOptionValue(String, String)} for a given option, with an
     * invalid value for the option type.
     */
    private void assertSetOptionValueInvalid(AllTypesOptionSource optionSource,
            final String optionName, final String expectedValue) {
        try {
            assertSetOptionValue(optionSource, optionName, expectedValue);
            fail("ConfigurationException not thrown");
        } catch (ConfigurationException e) {
            // expected
        }
    }
}