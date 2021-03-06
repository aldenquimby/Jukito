/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jukito;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test that methods with some parameters annotated with {@literal @}{@link org.jukito.All} behave correctly.
 */
@RunWith(JukitoRunner.class)
public class AllAnnotationTest {

    /**
     * Guice test module.
     */
    static class Module extends JukitoModule {
        @SuppressWarnings("unchecked")
        @Override
        protected void configureTest() {
            bindManyInstances(String.class, "A", "B");
            bindManyInstances(TestDataInstance.class, new TestDataInstance("A"),
                    new TestDataInstance("B"));
            bindMany(TestData.class, TestDataA.class, TestDataB.class);
            bindMany(Node.class, NodeA.class);
        }
    }

    interface TestData {
        String getData();
    }

    static class TestDataA implements TestData {
        public String getData() {
            return "A";
        }
    }

    static class TestDataB implements TestData {
        public String getData() {
            return "B";
        }
    }

    static class TestDataInstance {
        private final String data;

        public TestDataInstance(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

    interface Node {
    }

    static class NodeA implements Node {
    }

    /**
     * This class keeps track of what happens in all the tests run in this
     * class. It's used to make sure all expected tests are called.
     */
    private static class Bookkeeper {
        static List<String> stringsProcessed = new ArrayList<String>();
        static List<String> dataProcessed = new ArrayList<String>();
        static List<String> dataInstanceProcessed = new ArrayList<String>();
    }

    @Test
    public void testAllWithInstance(@All String string1, @All String string2) {
        Bookkeeper.stringsProcessed.add(string1 + string2);
    }

    @Test
    public void testAllWithClass(@All TestData data1, @All TestData data2) {
        Bookkeeper.dataProcessed.add(data1.getData() + data2.getData());
    }

    @Test
    public void testAllWithClassInstance(@All TestDataInstance data1, @All TestDataInstance data2) {
        Bookkeeper.dataInstanceProcessed.add(data1.getData() + data2.getData());
    }

    @AfterClass
    public static void checkBookkeeper() {
        assertTrue(Bookkeeper.stringsProcessed.contains("AA"));
        assertTrue(Bookkeeper.stringsProcessed.contains("AB"));
        assertTrue(Bookkeeper.stringsProcessed.contains("BA"));
        assertTrue(Bookkeeper.stringsProcessed.contains("BB"));
        assertEquals(4, Bookkeeper.stringsProcessed.size());

        assertTrue(Bookkeeper.dataProcessed.contains("AA"));
        assertTrue(Bookkeeper.dataProcessed.contains("AB"));
        assertTrue(Bookkeeper.dataProcessed.contains("BA"));
        assertTrue(Bookkeeper.dataProcessed.contains("BB"));
        assertEquals(4, Bookkeeper.dataProcessed.size());

        assertTrue(Bookkeeper.dataInstanceProcessed.contains("AA"));
        assertTrue(Bookkeeper.dataInstanceProcessed.contains("AB"));
        assertTrue(Bookkeeper.dataInstanceProcessed.contains("BA"));
        assertTrue(Bookkeeper.dataInstanceProcessed.contains("BB"));
        assertEquals(4, Bookkeeper.dataInstanceProcessed.size());
    }

    @Test
    public void testAllDoesNotIncludeMock(@All Node node, Node neighbour) {
        assertFalse(Mockito.mockingDetails(node).isMock());
    }
}
