package firstdelivery.io;

import java.io.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import firstdelivery.entity.OutputFields;
import firstdelivery.utils.StringsFirstDelivery;



public class ManageFile {

	private ManageFile() {
		
	}
	

	public static void writeCSVOnFile(List<OutputFields> list) {
		   try(FileWriter csvWriter = new FileWriter(StringsFirstDelivery.OUTPUTFILE)){
			   csvWriter.append("Date");
			   csvWriter.append(",");
			   csvWriter.append("Number Fixed Bugs");
			   csvWriter.append("\n");
			   
			   for(int i = 0 ; i!= list.size();i++) {
				   csvWriter.append(list.get(i).getDate().getMonth().getDisplayName(TextStyle.SHORT,Locale.ITALIAN)+"/"+list.get(i).getDate().getYear());
				   csvWriter.append(",");
				   csvWriter.append(Integer.toString(list.get(i).getCount()));
			       csvWriter.append("\n");
			   }
		   } catch (Exception e) {
			   Logger.getLogger(ManageFile.class.getName()).log( Level.SEVERE, e.toString(), e );
	         } 
	   }
	
}
