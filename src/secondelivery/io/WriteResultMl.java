package secondelivery.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import secondelivery.entity.OutputMl;
import secondelivery.strings.StringsSecondDelivery;

public class WriteResultMl {

	private WriteResultMl() {}


	public static void writeResultMlOnFile(String method, List<OutputMl> listOuput, String output)
	{
		try(FileWriter csvWriter = new FileWriter(StringsSecondDelivery.RESULTS_ROOT+method+StringsSecondDelivery.DIRECTORY_ML+output+StringsSecondDelivery.OUTPUTFILE_ML)){
			csvWriter.append("Dataset");
			csvWriter.append(",");
			csvWriter.append("#Training Release");
			csvWriter.append(",");
			csvWriter.append("%Training(data on training/total data");
			csvWriter.append(",");
			csvWriter.append("%Defective on training");
			csvWriter.append(",");
			csvWriter.append("%Defective in testing");
			csvWriter.append(",");
			csvWriter.append("Classifier");
			csvWriter.append(",");
			csvWriter.append("IBK k-hyperParam[1-3-9-19]");
			csvWriter.append(",");
			csvWriter.append("Feature Selection");
			csvWriter.append(",");
			csvWriter.append("EPV Before");
			csvWriter.append(",");
			csvWriter.append("EPV After");
			csvWriter.append(",");
			csvWriter.append("Balancing");
			csvWriter.append(",");
			csvWriter.append("TP");
			csvWriter.append(",");
			csvWriter.append("FP");
			csvWriter.append(",");
			csvWriter.append("TN");
			csvWriter.append(",");
			csvWriter.append("FN");
			csvWriter.append(",");
			csvWriter.append("Precision");
			csvWriter.append(",");
			csvWriter.append("Recall");	
			csvWriter.append(",");
			csvWriter.append("AUC");
			csvWriter.append(",");
			csvWriter.append("Kappa");
			csvWriter.append(",");
			csvWriter.append("FMeasure");
			csvWriter.append("\n");

			for(int i = 0 ; i!= listOuput.size();i++) {

				csvWriter.append(output);
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getnRelease()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getPercentTrainingOnTotal()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getPercentDefectiveTraining()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getPercentDefectiveTesting()));
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getModel());
				csvWriter.append(",");
				if(listOuput.get(i).getkParam()==0.0)
					csvWriter.append("NO K-Param");
				else
					csvWriter.append(String.valueOf(listOuput.get(i).getkParam()));
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getNameFs());
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getEpvBefore()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getEpvAfter()));
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getNameSampling());
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getTp()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getFp()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getTn()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getFn()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getPrecision()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getRecall()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getAuc()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getKappa()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getfMeasure()));
				csvWriter.append("\n");
			}

		} catch (IOException e) {
			Logger.getLogger(WriteResultMl.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
	}


}
