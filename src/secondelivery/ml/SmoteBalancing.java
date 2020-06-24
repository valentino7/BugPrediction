package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.filters.supervised.instance.SMOTE;

public class SmoteBalancing {

	private SmoteBalancing() {}
	
	public static SMOTE getSmote(Instances data) {
		SMOTE smote = new SMOTE();
		try {
			smote.setInputFormat(data);
		} catch (Exception e) {
			   Logger.getLogger(SmoteBalancing.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return smote;
	}

}
