/**
 *
 * @author tasneem
 */
import org.apache.jena.rdf.model.*;
import java.io.*;
import org.apache.jena.ontology.*;
import org.apache.jena.vocabulary.RDFS;

public class WriteTurtleFile 
{
    private final String path;
    private final String [][] excelSheet;
    private final String outputFilePath;
    String standard;
    String rdfType;
    String [] temp1;    
    String getURI;
    OntModel base = ModelFactory.createOntologyModel();
    Individual ind;
    
    
    /**
     * @param inputFile is the path of the excel file
     * Sets turtle file path for reading and writing
     */
    WriteTurtleFile(String p, String [][] excelSheet, String op)
    {
        this.path = p;        
        this.excelSheet = excelSheet;
        this.outputFilePath = op;
    }
    
    /**
     * This methods reads the turtle file.
     */
    public void read()
    {
        this.base.read(path, "");
    }
    
    /**
     * This method 
     *  1. reads the turtle file
     *  2. takes the converted matrix of excel file and adds the new instances of standards.
     *  3. adds properties of the instances
     *  4. writes the modified turtle file to another turtle file.
     * @throws java.io.IOException
     */
    public void createInstance() throws IOException
    {
        read();
        for(int mCol = 1; mCol < excelSheet[0].length; mCol++)
        //for(int mCol = 1; mCol < 2; mCol++)
        {
            standard = excelSheet[0][mCol];// gets the name of the standard
            temp1 = excelSheet[1][mCol].split(","); //congtains multiple ranges of rdf:type of the instance
            ind = createIndividualInstance(temp1); 
            String property;
            String rangeOfProp;
            for(int mRow = 2; mRow < excelSheet.length; mRow++)
            {
                property = excelSheet[mRow][0];
                rangeOfProp = excelSheet[mRow][mCol];
                ind = addProperties(ind, property, rangeOfProp);
            }
            write(outputFilePath);
        }
        
    }
    
    /**
     * Creates instance with multiple ranges (rdf:type / a)
     * @param temp1 String array containing multiple ranges
     * @return Individual instance
     * P.S. Make sure all the ranges are created in the ontology 
     *      Otherwise, it will generate error of nullPointException
     */
    public Individual createIndividualInstance(String [] temp1)
    {
        OntClass oClass;
        Individual indiv = null;
        String [] temp2;
        String stoURI = base.getNsPrefixURI("sto"); //extracts the default URI (sto) 

        for (int multRange = 0; multRange < temp1.length; multRange++) {
            temp2 = temp1[multRange].split(":"); //splits 'sto:Standard' to get URI(sto) and name(Standard) 
            getURI = base.getNsPrefixURI(temp2[0]);//temp2[0] is the URI (sto)
            oClass = base.getOntClass(getURI + temp2[1]);//temp2[1] is the name of the standard
            indiv = oClass.createIndividual(stoURI + standard);//creates the individual
        }
        return indiv;
    }
    
    /**
     * This method adds properties to the instance of the standard
     * @param indiv Individual created from createIndividualInstance method
     * @param property name of the property 
     * @param rangeOfProp String containing multiple range
     * @return returns the individual of the instances with after the properties
     */
    public Individual addProperties(Individual indiv, String property, String rangeOfProp)
    {
        if(!rangeOfProp.isEmpty())
        {
            String[] temp2 = property.split(":");
            switch (temp2[1]) {
                case "comment":
                    indiv.addProperty(RDFS.comment, rangeOfProp);
                    break;
                case "label":
                    indiv.addProperty(RDFS.label, rangeOfProp);
                    break;
                default:
                    String[] temp3 = rangeOfProp.split(",");
                    String URI;
                    for (int i = 0; i < temp3.length; i++) {
                        URI = base.getNsPrefixURI(temp2[0]);
                        indiv.addProperty(base.getProperty(URI + temp2[1]), temp3[i]);
                    }                    
                    break;
            }
        }
        return indiv;
    }
    
    /**
     * This method creates the a file, where the output turtle file is written
     * @param outputFilePath is the directory of the file path
     * @return File
     * @throws IOException 
     */
    public File createFile(String outputFilePath) throws IOException
    {
        File file = new File (outputFilePath);
        file.createNewFile(); // if file already exists will do nothing 
        return file;
    }
    
    /**
     * This method converts the file path to a FIleOutputStream for writing the turtle file.
     * @param outputFilePath converts the directory of path
     * @throws FileNotFoundException 
     */
    public void write(String outputFilePath) throws FileNotFoundException, IOException
    {
        FileOutputStream oFile = new FileOutputStream(createFile(outputFilePath), false);
        this.base.write(oFile, "TTL");
    }
}
