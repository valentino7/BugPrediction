package io;

import utils.FieldsQuery;
import utils.Strings;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ManageFile {

	private ManageFile() {
		
	}
	
	public static List<String> readFile() throws IOException {
		ArrayList<String> lCommits = new ArrayList<>();
		FileReader fr= new FileReader(Strings.LOG_FILENAME);
		try(BufferedReader reader = new BufferedReader(fr)){
			
			String line = reader.readLine();
			while(line!=null) {
		        line = reader.readLine();
		        lCommits.add(line);
			}
		} 
		return lCommits;
	}
	
	
	public static void writeCSVOnFile(List<FieldsQuery> list) throws IOException {
		   try(FileWriter csvWriter = new FileWriter("fixedBug.csv")){
			   csvWriter.append("Date");
			   csvWriter.append(";");
			   csvWriter.append("Number Fixed Bugs");
			   csvWriter.append("\n");
			   
			   for(int i = 0 ; i!= list.size();i++) {
				   String year= Integer.toString( list.get(i).getDate().get(Calendar.YEAR) );
				   String month= Integer.toString( list.get(i).getDate().get(Calendar.MONTH)+1);
				   csvWriter.append(year+"-"+month);
				   csvWriter.append(";");
				   csvWriter.append(Integer.toString(list.get(i).getCount()));
			       csvWriter.append("\n");
			   }
				   
		   }
	   }
	
}
