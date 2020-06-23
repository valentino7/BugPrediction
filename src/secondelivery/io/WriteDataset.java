package secondelivery.io;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.entity.Bug;
import common.entity.CollectBugs;
import common.entity.JavaFile;
import common.entity.Metrics;
import secondelivery.strings.StringsSecondDelivery;

public class WriteDataset {

	private WriteDataset() {}


	public static void writeCSVOnFile(String method, String nameProject, Map<String,List<JavaFile>> hRelFile) {
		try(FileWriter csvWriter = new FileWriter(StringsSecondDelivery.RESULTS_ROOT+method+StringsSecondDelivery.DIRECTORY_DATASET+nameProject+StringsSecondDelivery.OUTPUTFILE)){
			csvWriter.append("Version");
			csvWriter.append(",");
			csvWriter.append("File Name");
			csvWriter.append(",");
			csvWriter.append("LOC");
			csvWriter.append(",");
			csvWriter.append("LOC_touched");
			csvWriter.append(",");
			csvWriter.append("NR");	
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
			csvWriter.append(",");
			csvWriter.append("Buggy");

			csvWriter.append("\n");

			for (String key : hRelFile.keySet()) {
				int i = Integer.parseInt(key);
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
					csvWriter.append(String.valueOf(metrics.getNr()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getNfix()));
					csvWriter.append(",");
					csvWriter.append(String.valueOf(metrics.getNumAuth()));
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
					csvWriter.append(",");

					if(metrics.getBuggy().equals(Boolean.TRUE)) {
						csvWriter.append("YES");}
					else
						csvWriter.append("NO");
					csvWriter.append("\n");
				}


			}
		} catch (Exception e) {
			Logger.getLogger(WriteDataset.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 
	}


	public static void writeBugPercent(String proportionMethod, String name, Map<String, List<JavaFile>> hRelFile) {
		try(FileWriter csvWriter = new FileWriter(StringsSecondDelivery.RESULTS_ROOT+proportionMethod+name+StringsSecondDelivery.OUTPUTFILE_PERCENT)){
			csvWriter.append("Version");
			csvWriter.append(",");
			csvWriter.append("Percent bug");
			csvWriter.append("\n");

			for (String key : hRelFile.keySet()) {
				int i = Integer.parseInt(key);
				csvWriter.append(String.valueOf(i+1));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(getBugPercent(hRelFile.get( String.valueOf(i) ) )));					
				csvWriter.append("\n");

			}
		} catch (Exception e) {
			Logger.getLogger(WriteDataset.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 		
	}


	private static double getBugPercent(List<JavaFile> list) {
		int somm=0;
		for(int i=0; i!= list.size(); i++) {
			if(list.get(i).getMetrics().getBuggy().equals(Boolean.TRUE))
				somm+=1;
		}
		return (double)somm/list.size()*100;
	}


	public static void writeBugIVOVAV(String proportionMethod, String name, CollectBugs collectBugs) {
		try(FileWriter csvWriter = new FileWriter(StringsSecondDelivery.RESULTS_ROOT+"buganalysis\\"+proportionMethod+name+".csv")){
			csvWriter.append("Bug");
			csvWriter.append(",");
			csvWriter.append("IV");
			csvWriter.append(",");
			csvWriter.append("OV");
			csvWriter.append(",");
			csvWriter.append("FV");
			csvWriter.append(",");
			csvWriter.append("JIRA AV");
			csvWriter.append("\n");

			for (Bug bug :collectBugs.getBugsWithCommits()) {
				csvWriter.append(bug.getId());
				csvWriter.append(",");
				csvWriter.append(String.valueOf(bug.getInjectedRelease().getIndex()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(bug.getOpenRelease().getIndex()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(bug.getFixedRelease().getIndex()));
				csvWriter.append(",");
				if(collectBugs.getBugsJiraAV().contains(bug))
					csvWriter.append("YES");
				else
					csvWriter.append("FALSE");
				csvWriter.append("\n");
			}
		} catch (Exception e) {
			Logger.getLogger(WriteDataset.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 				
	}

}
