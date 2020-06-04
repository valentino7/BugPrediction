package secondelivery.ml;

import weka.core.Instances;
import weka.filters.Filter;


public interface TypeFilter {


	public Filter getFilter(Instances dataset);
	
}
