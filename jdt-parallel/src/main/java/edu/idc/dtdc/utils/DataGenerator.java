package edu.idc.dtdc.utils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alexanderva
 * Date: 5/12/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DataGenerator {
    void generateData(int numberOfPoint);
    void saveData(String fileName)  throws IOException;
}
