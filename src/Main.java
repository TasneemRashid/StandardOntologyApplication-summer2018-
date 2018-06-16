
import java.io.File;
import java.io.IOException;

/** *
 * @author tasneem
 * Java application for standardOntology Project for automation.
 * Aim of this application is to automate the process of creating 
 * ontology for Standards of Industry4.0
 * It includes:
 *      1. Read excel file with all standards and their properties
 *      2. Automatically add the the instances of the standards 
 *         in the turtle file.
 */
public class Main 
{    
    public static void main (String [] args)
    {
        //for the warning, without this line, there will be no errors
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        
        //create file path for all files
        CreateFilePath cfp = new CreateFilePath();
        String excelFilePath = cfp.createFilePath("input.xls");
        String turtleFilePath = cfp.createFilePath("checkSTO.ttl");
        String outputTurtleFilePath = cfp.createFilePath("outputTTLFile.ttl");
        ReadExcel  r = new ReadExcel(excelFilePath);
        try
        {
           String [][] excelSheet = r.read();
           WriteTurtleFile w = new WriteTurtleFile(turtleFilePath, excelSheet, outputTurtleFilePath);
           w.createInstance();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
