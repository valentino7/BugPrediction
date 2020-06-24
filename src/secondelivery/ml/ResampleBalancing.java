package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.filters.supervised.instance.Resample;

public class ResampleBalancing {

	private ResampleBalancing() {}
	
	public static Resample getResample(Instances data, int numDefect)    {
		Resample resample = new Resample();
		resample.setBiasToUniformClass(1.0);
		double percent = getMinorityClass(data.size(), numDefect);
		resample.setSampleSizePercent(100.0+(50.0-percent));
		try {
			resample.setInputFormat(data);
		} catch (Exception e) {
			Logger.getLogger(ResampleBalancing.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return resample;
	}
	
	private static double getMinorityClass(int size, int totalNumDefect) {
		int minority=totalNumDefect;
		if (size-totalNumDefect < totalNumDefect)
			minority = size-totalNumDefect;
		
		return (double)minority/size*100;
	}
}
