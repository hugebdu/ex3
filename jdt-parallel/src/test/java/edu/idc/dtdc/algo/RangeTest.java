package edu.idc.dtdc.algo;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static edu.idc.dtdc.algo.Range.empty;
import static edu.idc.dtdc.algo.Range.range;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 5/12/12
 */
public class RangeTest
{
    private static final ImmutableList<String> list = ImmutableList.of("a", "b", "c", "d", "e", "f");

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalRangeCreation() throws Exception
    {
        range(2, 1);
    }

    @Test
    public void testEmptyRangeCreation() throws Exception
    {
        Range range = empty();

        assertTrue(range.isEmpty());
        assertThat(range.size(), is(0));
    }

    @Test
    public void testNonEmptyRange() throws Exception
    {
        Range range = range(0, 0);

        assertThat(range.size(), is(1));
        assertFalse(range.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSplit1() throws Exception
    {
        Range range = range(2, 4);
        range.splitTakeLeft(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSplit2() throws Exception
    {
        Range range = range(2, 4);
        range.splitTakeLeft(5);
    }

    @Test(expected = IllegalStateException.class)
    public void testSplitOnEmpty1() throws Exception
    {
        empty().splitTakeLeft(0);
    }

    @Test(expected = IllegalStateException.class)
    public void testSplitOnEmpty2() throws Exception
    {
        empty().splitTakeRight(0);
    }

    @Test
    public void testSplitRangeOf1() throws Exception
    {
        Range range = range(0, 0);
        
        assertFalse(range.isEmpty());
        assertThat(range.size(), is(1));

        assertTrue(range.splitTakeLeft(0).isEmpty());
        assertTrue(range.splitTakeRight(0).isEmpty());
    }

    @Test
    public void testSplitRangeOf2() throws Exception
    {
        Range range = range(0, 1);

        assertFalse(range.isEmpty());
        assertThat(range.size(), is(2));
        
        assertThat(range.start(), is(0));
        assertThat(range.end(), is(1));

        Range left = range.splitTakeLeft(0);
        Range right = range.splitTakeRight(0);

        assertFalse(left.isEmpty());
        assertThat(left.size(), is(1));
        
        assertThat(left.start(), is(0));
        assertThat(left.end(), is(0));

        assertFalse(right.isEmpty());
        assertThat(right.size(), is(1));
        
        assertThat(right.start(), is(1));
        assertThat(right.end(), is(1));

        left = range.splitTakeLeft(1);
        right = range.splitTakeRight(1);

        assertThat(left, sameInstance(range));
        assertThat(right, sameInstance(empty()));
    }

    @Test
    public void testRangeSubList() throws Exception
    {
        assertThat(range(0, 5).subList(list), is(list));
        assertThat(range(0, 25).subList(list), is(list));
        assertList(range(0, 0).subList(list), "a");
        assertList(range(0, 1).subList(list), "a", "b");
        assertList(range(5, 5).subList(list), "f");
        assertList(range(5, 5).subList(list), "f");
        assertList(range(5, 6).subList(list), "f");
        assertList(range(4, 6).subList(list), "e", "f");
        assertList(range(1, 1).subList(list), "b");
        assertList(range(1, 2).subList(list), "b", "c");
    }

    @Test
    public void testEmptyRangeSubList() throws Exception
    {
        assertTrue(empty().subList(list).isEmpty());
    }
    
    public void assertList(List<String> list, String ... values)
    {
        assertThat(list.size(), is(values.length));
        assertThat(list, is(asList(values)));
    }
}
