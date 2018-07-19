/**
 *
 * @author tasneem
 */
import org.apache.jena.rdf.model.*;
import java.io.*;
import org.apache.jena.ontology.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.datatypes.xsd.*;

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
        this.base.read(path);
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
        System.out.println("number of collumns in excel sheet: " + excelSheet[0].length );
        //for(int mCol = 1; mCol < excelSheet[0].length; mCol++)
        for(int mCol = 1; mCol < 2; mCol++)
        {
            System.out.println(mCol);
            standard = excelSheet[0][mCol];// gets the name of the standard
           // System.out.println("Collumn: " + mCol);
           // System.out.println("Name: " + standard );
            temp1 = excelSheet[1][mCol].split(","); //congtains multiple ranges of rdf:type of the instance
           // System.out.println(temp1[0] + " " + temp1[1]);
            ind = createIndividualInstance(temp1); 
            //System.out.println("Create individual success");
            String property;
            String rangeOfProp;
            for(int mRow = 2; mRow < excelSheet.length; mRow++)
            {
                //System.out.println("row accessed: " + mRow);
                property = excelSheet[mRow][0];
                rangeOfProp = excelSheet[mRow][mCol];
                /**
                 * property = sto:Publisher 
                 * rangeOfProperty = sto:ISO, sto:IEC
                 */
                //System.out.println("property: " + property );
                //System.out.println(property + ": rangeOfProp: " + rangeOfProp );
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
            //System.out.println("come"+temp1[multRange]);
            temp2 = temp1[multRange].split(":"); //splits 'sto:Standard' to get URI(sto) and name(Standard) 
            getURI = base.getNsPrefixURI(temp2[0]);//temp2[0] is the URI (sto)
            //System.out.println("temp2-0"+temp2[0]);
            //System.out.println("temp2-1"+temp2[1]);
            oClass = base.getOntClass(getURI + temp2[1]);//temp2[1] is the name of the standard
            indiv = oClass.createIndividual(stoURI + standard);//creates the individual
            //System.out.println(indiv);
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
            /**
             * 
             * property = sto:Publisher 
             * rangeOfProperty = sto:ISO, sto:IEC (also some String e.g "this is a comment")             
             * temp2[0] = sto (also canbe rdfs, owl etc)
             * temp2[1] = publisher (also comment, sameAs etc)
             */
            String[] temp2 = property.split(":");
            switch (temp2[1]) {                
                case "comment": //built-in in the library
                    indiv.addProperty(RDFS.comment, rangeOfProp, "en");
                    break;
                case "label": //built-in in the library
                    indiv.addProperty(RDFS.label, rangeOfProp, "en");
                    break;
                case "sameAs": //built-in in the library
                    boolean flag = checkIfString(rangeOfProp);
                    if (flag == true) {
                        indiv.addProperty(OWL.sameAs, rangeOfProp);
                    } else {
                        //System.out.println("In gorbor");
                        Property p = createProperty(rangeOfProp);
                        indiv.addProperty(OWL.sameAs, p);
                    }   
                    break;
                case "hasEdition":  
                    Property p = createProperty(property);
                    indiv.addLiteral(p, Float.parseFloat(rangeOfProp));
                    break;
                case "hasPublicationDate":  
                    p = createProperty(property);
                    indiv.addLiteral(p, ResourceFactory.createTypedLiteral(rangeOfProp,XSDDatatype.XSDdate));
                    break;
                case "hasStabilityDate":  
                    p = createProperty(property);
                    indiv.addLiteral(p, ResourceFactory.createTypedLiteral(rangeOfProp,XSDDatatype.XSDgYear));
                    break; 
                case "hasTitle": //built-in in the library
                    p = createProperty(property);                    
                    indiv.addProperty(p, rangeOfProp, "en");
                    break;
                case "hasNumericalValue":  
                    p = createProperty(property);
                    indiv.addLiteral(p, Float.parseFloat(rangeOfProp));
                    break;  
                case "hasPages":  
                    p = createProperty(property);
                    indiv.addLiteral(p, ResourceFactory.createTypedLiteral(Integer.parseInt(rangeOfProp)));
                    break;
                case "hasTag":  
                    p = createProperty(property);                    
                    indiv.addProperty(p, rangeOfProp, "en");
                    break;
                default:
                    /**
                     * For other properties created in the ontology,
                     * these properties may have multiple ranges.
                     * e.g. property sto:publisher may have ranges sto:IEC, sto:ISO.
                     * so temp3[0] = sto:IEC and temp3[1] = sto:ISO.
                     */
                    p = createProperty(property);
                    String[] temp3 = rangeOfProp.split(",");
                    for (int i = 0; i < temp3.length; i++) 
                    {
                        //indiv.addProperty(p, range);
                        flag = checkIfString(rangeOfProp);
                        if (flag == true) {
                            indiv.addProperty(p, temp3[i], "en");
                        } else {
                            Property range = createProperty(temp3[i]);
                            indiv.addProperty(p, range);
                        }                        
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
    
    /**
     * Checks if the parameter holds a String or a property
     * @param check the value to be evaluated
     * @return true is the parameter is a String
     */
    public boolean checkIfString(String check)
    {
        String [] temp = check.split(":");        
        if (temp.length == 1)
        {
            return true;
        }
        else if (temp.length == 2)
        {
            try
            {
                base.getNsPrefixURI(temp[0]);
                return false;
            }
            catch(Exception e)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates the property from the String value.
     * @param value String. e.g. sto:IEC
     * @return the property
     */
    public Property createProperty(String value)
    {
       //System.out.println(value);
        String [] temp = value.split(":");        
        String URI = base.getNsPrefixURI(temp[0]);        
        return base.getProperty(URI + temp[1]);
    }
}
