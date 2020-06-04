package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.WrapperSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class WrapperSelection implements TypeFilter {

	@Override
	public Filter getFilter(Instances dataset)   {
		WrapperSubsetEval wrapperEvalnbc = new WrapperSubsetEval();
		//F num fold T standard deviation
		String[] wrapperEvalOptsnbc = {"-B", "weka.classifiers.bayes.NaiveBayes","-F","5","-T","0.01","-R","1"};
		try {
			wrapperEvalnbc.setOptions(wrapperEvalOptsnbc);
		} catch (Exception e) {
			Logger.getLogger(WrapperSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		try {
			wrapperEvalnbc.buildEvaluator(dataset);
		} catch (Exception e) {
			Logger.getLogger(WrapperSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}

		BestFirst bFWrpNbc = new BestFirst();
		//N numero di nodi che non migliorano prima di terminare D 0 backword 1 forward 2 bidirezionale
		String [] optsBFwrpnbc = {"-D", "2",  "-N", "5"};
		try {
			bFWrpNbc.setOptions(optsBFwrpnbc);
		} catch (Exception e) {
			Logger.getLogger(WrapperSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		
		AttributeSelection fs = new AttributeSelection();
		fs.setEvaluator(wrapperEvalnbc);
		fs.setSearch(bFWrpNbc);

		//specify the dataset
		try {
			fs.setInputFormat(dataset);
		} catch (Exception e) {
			Logger.getLogger(WrapperSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		//apply
		return fs;

	}
	
	
}
