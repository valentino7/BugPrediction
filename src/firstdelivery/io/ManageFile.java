package firstdelivery.io;

import java.io.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import firstdelivery.entity.OutputFields;



public class ManageFile {

	private ManageFile() {
	}
	

	public static void writeCSVOnFile(String outputFile, List<OutputFields> list, int nCommitsOnlyBug, int nCommitsWithoutBug, int nCommitsOtherBug, int nBugFixed, int nBugWithoutCommit) {
		   try(FileWriter csvWriter = new FileWriter(outputFile)){
			   csvWriter.append("Date");
			   csvWriter.append(",");
			   csvWriter.append("Number Fixed Bugs");
			   csvWriter.append(",");
			   csvWriter.append(",");			   
			   csvWriter.append("Number of commits with only bug fixed");
			   csvWriter.append(",");
			   csvWriter.append("Number of commits without bug");
			   csvWriter.append(",");
			   csvWriter.append("Number of commits with other bug");
			   csvWriter.append(",");
			   csvWriter.append("Tot commits");
			   csvWriter.append(",");
			   csvWriter.append(",");
			   csvWriter.append("Number bug with commit");
			   csvWriter.append(",");
			   csvWriter.append("Number bug without commit");
			   
			   csvWriter.append("\n");
			   
			   for(int i = 0 ; i!= list.size();i++) {
				   csvWriter.append(list.get(i).getDate().getMonth().getDisplayName(TextStyle.SHORT,Locale.ITALIAN)+"/"+list.get(i).getDate().getYear());
				   csvWriter.append(",");
				   csvWriter.append(Integer.toString(list.get(i).getCount()));
				   
				   if (i==0) {
					   csvWriter.append(",");
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nCommitsOnlyBug));
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nCommitsWithoutBug));
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nCommitsOtherBug));
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nCommitsOtherBug+nCommitsWithoutBug+nCommitsOnlyBug));
					   csvWriter.append(",");
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nBugFixed));
					   csvWriter.append(",");
					   csvWriter.append(Integer.toString(nBugWithoutCommit));
				   }
			       csvWriter.append("\n");
			   }
			   
			   
		   } catch (Exception e) {
			   Logger.getLogger(ManageFile.class.getName()).log( Level.SEVERE, e.toString(), e );
	         } 
	   }
	
}
