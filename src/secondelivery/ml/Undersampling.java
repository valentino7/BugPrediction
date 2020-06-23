package secondelivery.ml;

import weka.core.Instances;
	import weka.filters.supervised.instance.SpreadSubsample;

public class Undersampling {
	
	private Undersampling() {}
	
	public static SpreadSubsample getSpreadSubsample(Instances data) throws Exception {
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] opts = new String[]{"-M", "1.0"};
		spreadSubsample.setOptions(opts);
		spreadSubsample.setInputFormat(data);
		return spreadSubsample;
	}

}
