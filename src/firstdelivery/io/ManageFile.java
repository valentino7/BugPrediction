package firstdelivery.io;

import java.io.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.entity.CommitEntity;
import common.utils.CreatorDate;
import firstdelivery.entity.OutputFields;
import firstdelivery.utils.StringsFirstDelivery;



public class ManageFile {

	private ManageFile() {
		
	}
	
	public static List<CommitEntity> readFile() throws IOException {
		ArrayList<CommitEntity> lCommits = new ArrayList<>();
		FileReader fr= new FileReader(StringsFirstDelivery.LOG_FILENAME);
		try(BufferedReader reader = new BufferedReader(fr)){
			
			String line = reader.readLine();
			while(line!=null) {
		        line = reader.readLine();
		        String[] s=line.split("\t");
		        lCommits.add(new CommitEntity(CreatorDate.parseDateTimeFormat(s[0]),s[1],"id"));
			}
		} 
		return lCommits;
	}
	
	
	public static void writeCSVOnFile(List<OutputFields> list) {
		   try(FileWriter csvWriter = new FileWriter(StringsFirstDelivery.OUTPUTFILE)){
			   csvWriter.append("Date");
			   csvWriter.append(",");
			   csvWriter.append("Number Fixed Bugs");
			   csvWriter.append("\n");
			   
			   for(int i = 0 ; i!= list.size();i++) {
				   //String year= Integer.toString( list.get(i).getDate().get(Calendar.YEAR) );
				   //String month= Integer.toString( list.get(i).getDate().get(Calendar.MONTH));
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
