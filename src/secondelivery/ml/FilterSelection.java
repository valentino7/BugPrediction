package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FilterSelection implements TypeFilter{

	
	@Override
	public Filter getFilter(Instances dataset)  {
		AttributeSelection filter = new AttributeSelection();
		//create evaluator and search algorithm objects
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		//set the algorithm to search backward
		search.setSearchBackwards(true);
		//set the filter to use the evaluator and search algorithm
		filter.setEvaluator(eval);
		filter.setSearch(search);
		//specify the dataset
		try {
			filter.setInputFormat(dataset);
		} catch (Exception e) {
			Logger.getLogger(FilterSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		//apply
		return filter;
	}

}
