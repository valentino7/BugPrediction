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


	public static void writeResultMlOnFile(List<OutputMl> listOuput)
	{
		try(FileWriter csvWriter = new FileWriter(StringsSecondDelivery.PROJECT_NAME+StringsSecondDelivery.OUTPUTFILE_ML)){
			csvWriter.append("Dataset");
			csvWriter.append(",");
			csvWriter.append("#Training Release");
			csvWriter.append(",");
			csvWriter.append("Classifier");
			csvWriter.append(",");
			csvWriter.append("Feature Selection");
			csvWriter.append(",");
			csvWriter.append("Sampling");
			csvWriter.append(",");
			csvWriter.append("Precision");
			csvWriter.append(",");
			csvWriter.append("Recall");	
			csvWriter.append(",");
			csvWriter.append("AUC");
			csvWriter.append(",");
			csvWriter.append("Kappa");
			csvWriter.append("\n");

			for(int i = 0 ; i!= listOuput.size();i++) {

				csvWriter.append(StringsSecondDelivery.PROJECT_NAME);
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getnRelease()));
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getModel());
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getNameFs());
				csvWriter.append(",");
				csvWriter.append(listOuput.get(i).getNameSampling());
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getPrecision()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getRecall()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getAuc()));
				csvWriter.append(",");
				csvWriter.append(String.valueOf(listOuput.get(i).getKappa()));
				csvWriter.append("\n");
			}

		} catch (IOException e) {
			Logger.getLogger(WriteResultMl.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
	}


}
