/**
 *
 * @author tasneem
 */
import org.apache.jena.rdf.model.*;
import java.io.*;
import org.apache.jena.ontology.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class Check {
    String [][] m;
    String inPath;
    String outPath;
    
    Check(String [][] m, String inPath, String outPath )
    {
        this.m = m;
        this.inPath = inPath;
        this.outPath = outPath;
    }
    public void runMethod() throws FileNotFoundException, IOException
    {
        OntClass oClass;
        Individual indiv;        
        String [] temp;
        String [] temp2;
        String name;
        OntModel base = ModelFactory.createOntologyModel();
        base.read(inPath);
        String stoURI = base.getNsPrefixURI("sto");
        String owlURI = base.getNsPrefixURI("owl");
        for(int mRow = 1; mRow < m.length; mRow++)
        {
            temp = m[mRow][0].split(" ");
            temp2 = temp[1].split("-"); 
            name = temp[0];
            if(temp2.length == 0)
            {
                name = name+"_"+temp[1];
            }
            else
            {
                for(int i = 0; i < temp2.length; i++)
                {
                    name = name+"_"+temp2[i];
                }
            }
            oClass = base.getOntClass(stoURI + "Standard"); 
            indiv = oClass.createIndividual(stoURI + name);  
            oClass = base.getOntClass(owlURI + "NamedIndividual");
            indiv = oClass.createIndividual(stoURI + name);
            indiv.addProperty(RDFS.comment, "blablablbla", "en");
            Property p;
            p = base.getProperty(stoURI + "hasPublicationDate");
            indiv.addProperty(p,m[mRow][1]);   
            p = base.getProperty(stoURI + "hasTitle");
            indiv.addProperty(p, m[mRow][2],"en");
            p = base.getProperty(stoURI + "hasPublisher");
            Property range = base.getProperty(stoURI + m[mRow][3]);
            indiv.addProperty(p, range);
        }
        File file = new File (outPath);        
        file.createNewFile();
        FileOutputStream oFile = new FileOutputStream(file, false);
        base.write(oFile, "TTL");        
    }
}
