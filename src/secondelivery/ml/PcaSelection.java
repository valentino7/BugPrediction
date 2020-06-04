package secondelivery.ml;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

public class PcaSelection implements TypeFilter {
	
	@Override
	public Filter getFilter(Instances dataset) {
		PrincipalComponents pca = new PrincipalComponents();
		//standardizza i dati
		pca.setCenterData(false);

		String[] op = new String[]{ "-R", "0.95","-A","5","-M","-1"};
		try {
			pca.setOptions(op);
		} catch (Exception e1) {
			Logger.getLogger(PcaSelection.class.getName()).log( Level.SEVERE, e1.toString(), e1 );
		}
		try {
			pca.setInputFormat(dataset);
		} catch (Exception e) {
			Logger.getLogger(PcaSelection.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return pca;
	}
	
	

}
