package edu.idc.dtdc.utils;

import org.junit.Test;

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
        String fileName = "testData.tsin";
        int numberOfPoints = 10000;
        DefaultDataGenerator dataGenerator = new DefaultDataGenerator();
        dataGenerator.generateData(numberOfPoints);
        dataGenerator.saveData(fileName);
    }
}
