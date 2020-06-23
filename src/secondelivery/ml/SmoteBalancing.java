package secondelivery.ml;

import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;

public class SmoteBalancing {

	private SmoteBalancing() {}
	
	public static SMOTE getSmote(Instances data) throws Exception {
		SMOTE smote = new SMOTE();
		smote.setInputFormat(data);
		return smote;
	}

}
