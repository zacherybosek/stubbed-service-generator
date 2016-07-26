package com.zacherybosek.testing.stubbedservice;

import com.zacherybosek.testing.stubbedservice.request.info.SizeStoredSizeLimitedLinkedList;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Zachery on 7/26/2016.
 */
public class SizedStoredLinkListTest {

    @Test
    public void test() {
        SizeStoredSizeLimitedLinkedList<String> strings = new SizeStoredSizeLimitedLinkedList<>(1);
        strings.add("test1");
        strings.add("test2");

        assertEquals(2L, strings.size());
        assertEquals(1, strings.getListSizeLimit());

        Iterator<String> iterStrings = strings.iterator();
        assertEquals("test2", iterStrings.next());
        assertFalse(iterStrings.hasNext());

        strings.clear();
        assertEquals(2L, strings.size());
        assertFalse(strings.iterator().hasNext());

        strings.add("test3");
        strings.setListSizeLimit(0);
        assertFalse(strings.iterator().hasNext());

        strings.setListSizeLimit(5);
        strings.add("test4");
        strings.add("test5");
        strings.add("test6");
        assertTrue(strings.iterator().hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSize() {
        new SizeStoredSizeLimitedLinkedList<>(-1);
    }
}
