/*
 * This is a simple program that reads an excel document that contains 2 columns - first for the source
 * and the second for the target URIs. Finally, this program writes an OWL file with OWLEquivalent statements 
 * between the mapping classes. 
 * @Fahim Imam (January 14, 2013)
 */

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class SetOWLEquivalence 
{
	
	//Global declarations.
	static ArrayList<String> srcURI = new ArrayList<String>(); //List of source URI.
    static ArrayList<String> targetURI = new ArrayList<String>(); //List of target URI.
	
    
    public static void main (String[] args)
	{
	    String srcClassURI = "";    // If the mapping excel file only contains Class IDs, 
	    String targetClassURI = ""; // please insert the URI portion of the Class IDs here.
		
	    //Note: Only works with xls file, not xslx.
	    String excelFileName = "NIFSTD-PRO-Mappings.xls"; // Change the file name as you wish.
														  // Make sure to keep the excel file at the same directory of this program.
		String outputOWLFileName = "NIF-PRO-Bridge.owl";  // Make sure to have .owl extension for the file name.
		
		String outputOntologyURI = "http://ontology.neuinfo.org/NIF/BiomaterialEntities/" + outputOWLFileName; 
	    //Example URI for NIF: http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-GO-CC-Bridge.owl
			
			
	    loadExcelDocument(srcClassURI, targetClassURI, excelFileName);
		writeOWLEquivFile(outputOWLFileName, outputOntologyURI); 
	} // end of main method.

	public static void loadExcelDocument(String srcClassURI, String targetClassURI, String fileName)
	{
		try 
	    {
	        InputStream input = new BufferedInputStream(
	                    new FileInputStream(fileName)); 
	        POIFSFileSystem fs = new POIFSFileSystem( input );
	        HSSFWorkbook wb = new HSSFWorkbook(fs);
	        HSSFSheet sheet = wb.getSheetAt(0);
	        
	        Iterator rows = sheet.rowIterator();
	        while( rows.hasNext() ) 
	        {  
	            HSSFRow row = (HSSFRow) rows.next();
	            Iterator cells = row.cellIterator();
	          
	            while( cells.hasNext() ) 
	            {
	               HSSFCell cell = (HSSFCell) cells.next();
	                if (cell.getColumnIndex() == 0) //if first column.
	            	  	    srcURI.add(srcClassURI + cell.getStringCellValue().trim());
	                	else 
	                		targetURI.add(targetClassURI + cell.getStringCellValue().trim());
	            } //End of while block 2.
	
	        } //End of while block 1.
	             
	    } // End of try block.
	    
	    catch ( IOException ex ) 
	    {
	        ex.printStackTrace();
	    } // End of catch.
	
	}
	
	public static void writeOWLEquivFile(String outputFileName, String outputURI)
		{
			String OWLStatement = "";
			Iterator <String>srcItr = srcURI.iterator();
			Iterator <String>targetItr = targetURI.iterator();
			
		     while(srcItr.hasNext()) // loop through elements
		          {
		    	    //System.out.println(srcItr.next()); 
			
					OWLStatement += "\n<owl:Class rdf:about=\"" + srcItr.next() + "\">";
					OWLStatement += "\n\t<owl:equivalentClass rdf:resource=\"" + targetItr.next() + "\"/>";
					OWLStatement += "\n</owl:Class>";
		            
				  }
		     System.out.println(OWLStatement);
			/*
			OWL Equivalent Statement format:
			<owl:Class rdf:about="http://purl.obolibrary.org/obo/ERO_0000019">	
			  <owl:equivalentClass rdf:resource="http://ontology.neuinfo.org/NIF/DigitalEntities/NIF-Investigation.owl#nlx_inv_20090407"/>
			</owl:Class> 
			*/
		     
		     //OWL File header information.
		     String OWLFileHeader = "";
		     
		     OWLFileHeader += "<?xml version=\"1.0\"?>" 
		     			   + "\n\t<!DOCTYPE rdf:RDF ["
						   + "\n\t<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >"
						   + "\n\t<!ENTITY obo \"http://purl.obolibrary.org/obo/\" >"
						   + "\n\t<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >"
						   + "\n\t<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >"
						   + "\n\t<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >"
						   + "\n]>\n";


			OWLFileHeader += "\n<rdf:RDF xmlns=\"" + outputURI + "#\""
						    + "\n\txml:base=\"" + outputURI + "\""
						    + "\n\txmlns:obo=\"http://purl.obolibrary.org/obo/\""
						    + "\n\txmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""
						    + "\n\txmlns:owl=\"http://www.w3.org/2002/07/owl#\""
						    + "\n\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\""
						    + "\n\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
						    + "\n";
    
			OWLFileHeader += "\n\n<owl:Ontology rdf:about=\"" + outputURI + "\">"
					        + "\n\t<rdfs:label xml:lang=\"en\">" + outputFileName + "</rdfs:label>"
					        + "\n\t<owl:versionInfo xml:lang=\"en\">" + new Date().toGMTString() + "</owl:versionInfo>"
					        + "\n\t<rdfs:comment xml:lang=\"en\">Your comment goes here.</rdfs:comment>"
					        + "\n</owl:Ontology>\n";
		     
		     String OWLFileFooter = "\n\n</rdf:RDF>";
		     
		     try
			  {
			    // Create file 
			    FileWriter fstream = new FileWriter(outputFileName);
			    BufferedWriter output = new BufferedWriter(fstream);
			    
			    output.write(OWLFileHeader + OWLStatement + OWLFileFooter);
			    //Close the output stream
			    System.out.println ( "\n\nSUCCESS! Your file named " + outputFileName + " has been created successfully.");
			    output.close();
			    }
			  catch (Exception e)
			  {//Catch exception if any
			      System.err.println("Error: " + e.getMessage());
			  }
	
		} // End of writeOWLEquivFile method block.

}