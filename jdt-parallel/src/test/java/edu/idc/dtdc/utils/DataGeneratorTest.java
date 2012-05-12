package edu.idc.dtdc.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: alexanderva
 * Date: 5/12/12
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataGeneratorTest {
    @Test
    public void testDefaultDataGenerator() throws Exception
    {
        String fileName = "testData1.tsin";
        int numberOfPoints = 1000;
        DefaultDataGenerator dataGenerator = new DefaultDataGenerator();
        dataGenerator.generateData(numberOfPoints);
        dataGenerator.saveData(fileName);
    }


    @Test
    public void testDefaultDataGeneratorExpectedSize() throws Exception
    {
        int numberOfPoints = 10000;
        DefaultDataGenerator dataGenerator = new DefaultDataGenerator();
        dataGenerator.generateData(numberOfPoints);
        assertThat(dataGenerator.getData().size(), is(numberOfPoints));
    }
}
