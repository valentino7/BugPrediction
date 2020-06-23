package secondelivery.ml;

import weka.core.Instances;
import weka.filters.supervised.instance.Resample;

public class ResampleBalancing {

	private ResampleBalancing() {}
	
	public static Resample getResample(Instances data, int numDefect) throws Exception {
		Resample resample = new Resample();
		resample.setBiasToUniformClass(1.0);
		double percent = getMinorityClass(data.size(), numDefect);
		resample.setSampleSizePercent(100.0+(50.0-percent));
		resample.setInputFormat(data);
		return resample;
	}
	
	private static double getMinorityClass(int size, int totalNumDefect) {
		int minority=totalNumDefect;
		if (size-totalNumDefect < totalNumDefect)
			minority = size-totalNumDefect;
		
		return (double)minority/size*100;
	}
}
