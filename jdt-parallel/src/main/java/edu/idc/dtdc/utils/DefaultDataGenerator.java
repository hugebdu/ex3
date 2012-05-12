package edu.idc.dtdc.utils;

import edu.idc.dtdc.data.Point;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: alexanderva
 * Date: 5/12/12
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDataGenerator implements DataGenerator {

    protected Set<Point>  points;

    private final Random random;
    private final int generationCoefficient;

    public DefaultDataGenerator(){
        this(10000);
    }

    public DefaultDataGenerator(int randomGenerationCoefficient){
        random = new Random();
        generationCoefficient = randomGenerationCoefficient;
    }

    @Override
    public void generateData(int numberOfPoint) {
        points = new HashSet<Point>(numberOfPoint);

        for(int i = 0;i< numberOfPoint;i++)
            points.add(getNextPoint());
    }

    @Override
    public void saveData(String fileName) throws IOException{
        if(points==null)
            throw new InvalidStateException("Points have not been created yet");

        FileWriter fileStream = new FileWriter(fileName);
        fileStream.write(String.format("%s\n", points.size()));

        for(Point p:points)
        {
            fileStream.append(String.format("%s %s %s\n", p.x, p.y, p.z));
        }

        fileStream.close();
    }

    protected Point getNextPoint()
    {
        Double x = generationCoefficient * random.nextDouble();
        Double y = generationCoefficient * random.nextDouble();
        Double z = 0.0;

        return new Point(x, y, z);
    }
}
