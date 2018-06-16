/**
 *
 * @author tasneem
 */
import java.io.File;
import java.io.IOException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcel 
{
    private final File inputWorkbook;
    String [] [] excelSheet;
    /**
     * @param inputFile is the path of the excel file
     * Creates the workbook with the file path.
     */
    ReadExcel(String inputFile) 
    {
        this.inputWorkbook = new File(inputFile);
    }
    
    /**
     * This method reads the excel file from the file path. 
     * @return the matrix of the excel file     * 
     * @throws java.io.IOException
    */
    public String [][] read() throws IOException 
    {
        Workbook w;
        try 
        {
            w = Workbook.getWorkbook(inputWorkbook);
            /**
             * This gets the first sheet of the excel
             * To get the number of excel sheets in the file
             * use w.getNumberOfSheets() 
             */
            Sheet sheet = w.getSheet(0);
            
            int sheetRow = sheet.getRows();
            int sheetCol = sheet.getColumns();
            
            //matrix initialization with rows and columns of the excel file.
            excelSheet = new String[sheetRow][sheetCol];
            
            //This part reads the excel file and saves it in the excelSheet matrix
            for (int i = 0; i < sheetRow; i++) 
            {
                for (int j = 0; j < sheetCol; j++) 
                {
                    excelSheet[i][j] = sheet.getCell(j, i).getContents();//getCell method takes col first                    
                }
            }

        } 
        catch (BiffException e) 
        {
            System.out.print(e);
        }
        return excelSheet;
    }
}
