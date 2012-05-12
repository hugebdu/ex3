package edu.idc.dtdc.algo;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class Range
{
    private static final Range EMPTY_RANGE = new EmptyRange();
    
    public int start()
    {
        throw new NoSuchElementException();
    }
    
    public int end()
    {
        throw new NoSuchElementException();
    }
    
    public abstract boolean isEmpty();
    public abstract int size();
    public abstract Range splitTakeLeft(int splitPoint);
    public abstract Range splitTakeRight(int splitPoint);
    public abstract <T> ImmutableList<T> subList(ImmutableList<T> source);

    public static Range empty()
    {
        return EMPTY_RANGE;
    }

    public static Range range(int start, int end)
    {
        checkArgument(start >= 0 && end >= 0 && end >= start);
        return new NonEmptyRange(start, end);
    }
    
    static class NonEmptyRange extends Range
    {
        private final int start;
        private final int end;

        NonEmptyRange(int start, int end)
        {
            checkArgument(start <= end);

            this.end = end;
            this.start = start;
        }

        @Override
        public int start()
        {
            return start;
        }

        @Override
        public int end()
        {
            return end;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public int size()
        {
            return end - start + 1;
        }

        @Override
        public Range splitTakeLeft(int splitPoint)
        {
            validateSplitPoint(splitPoint);

            if (size() == 1)
                return EMPTY_RANGE;

            if (splitPoint == end)
                return this;

            return new NonEmptyRange(start, splitPoint);
        }

        @Override
        public Range splitTakeRight(int splitPoint)
        {
            validateSplitPoint(splitPoint);

            if (size() == 1)
                return EMPTY_RANGE;

            if (splitPoint == end)
                return EMPTY_RANGE;

            return new NonEmptyRange(splitPoint + 1, end);
        }

        @Override
        public String toString()
        {
            return "Range[" + start + ".." + end + ']';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NonEmptyRange that = (NonEmptyRange) o;

            if (end != that.end) return false;
            if (start != that.start) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(start, end);
        }

        @Override
        public <T> ImmutableList<T> subList(ImmutableList<T> source)
        {
            return source.subList(start, Ints.min(source.size(), end + 1));
        }

        private void validateSplitPoint(int splitPoint)
        {
            checkArgument(splitPoint >= start && splitPoint <= end);
        }
    }
    
    static class EmptyRange extends Range
    {
        EmptyRange()
        {
            
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        public Range splitTakeLeft(int splitPoint)
        {
            throw new IllegalStateException();
        }

        @Override
        public Range splitTakeRight(int splitPoint)
        {
            throw new IllegalStateException();
        }

        @Override
        public <T> ImmutableList<T> subList(ImmutableList<T> source)
        {
            return ImmutableList.of();
        }

        @Override
        public int hashCode()
        {
            return EMPTY_RANGE.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return EMPTY_RANGE == obj;
        }

        @Override
        public String toString()
        {
            return "Range{empty}";
        }
    }
}