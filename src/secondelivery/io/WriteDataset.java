package secondelivery.io;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.entity.JavaFile;
import common.entity.Metrics;
import firstdelivery.io.ManageFile;
import firstdelivery.utils.StringsFirstDelivery;

public class WriteDataset {

	private WriteDataset() {}


	public static void writeCSVOnFile(HashMap<String,List<JavaFile>> hRelFile) {
		try(FileWriter csvWriter = new FileWriter(StringsFirstDelivery.OUTPUTFILE)){
			csvWriter.append("Version");
			csvWriter.append(",");
			csvWriter.append("File Name");
			csvWriter.append(",");
			csvWriter.append("LOC");
			csvWriter.append(",");
			csvWriter.append("LOC_touched");
			csvWriter.append(",");
			csvWriter.append("NFix");
			csvWriter.append(",");
			csvWriter.append("NAuth");
			csvWriter.append(",");
			csvWriter.append("LOC_added");
			csvWriter.append(",");
			csvWriter.append("MAX_LOC");
			csvWriter.append(",");
			csvWriter.append("AVG_LOC");
			csvWriter.append(",");
			csvWriter.append("Churn");
			csvWriter.append(",");
			csvWriter.append("MAX_Churn");
			csvWriter.append(",");
			csvWriter.append("AVG_Churn");
			csvWriter.append(",");
			csvWriter.append("ChgSetSize");
			csvWriter.append(",");
			csvWriter.append("MAX_ChgSets");
			csvWriter.append(",");
			csvWriter.append("AVG_ChgSet");
			csvWriter.append(",");
			csvWriter.append("Age");
			csvWriter.append(",");
			csvWriter.append("WeightedAge");
			csvWriter.append("\n");

			for(int i = 0 ; i!= hRelFile.size();i++) {
				for (JavaFile javaFile : hRelFile.get( String.valueOf(i) )) {
					Metrics metrics = javaFile.getMetrics();
					csvWriter.append(String.valueOf(i+1));
					csvWriter.append(",");
					csvWriter.append(javaFile.getFilename());
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getLoc()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getLocTouched()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getNfix()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getnAuth()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getLocAdded()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getMaxLocAdded()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getAvgLocAdded()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getChurn()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getMaxChurn()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getAvgChurn()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getChgset()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getMaxChgset()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getAvgChgset()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getAge()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getWeightedAge()));
					csvWriter.append("\n");
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ManageFile.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 
	}

}
