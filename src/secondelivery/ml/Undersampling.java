package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.filters.supervised.instance.SpreadSubsample;

public class Undersampling {
	
	private Undersampling() {}
	
	public static SpreadSubsample getSpreadSubsample(Instances data)  {
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] opts = new String[]{"-M", "1.0"};
		try {
			spreadSubsample.setOptions(opts);
		} catch (Exception e) {
			   Logger.getLogger(Undersampling.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		try {
			spreadSubsample.setInputFormat(data);
		} catch (Exception e) {
			   Logger.getLogger(Undersampling.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return spreadSubsample;
	}

}
