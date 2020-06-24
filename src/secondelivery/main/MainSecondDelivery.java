package secondelivery.main;

import secondelivery.controller.ControllerSecondelivery;

public class MainSecondDelivery {

	//lista dei repository
	private static final String[] directories = {"bookkeeper","syncope"};
	private static final String[] projectName = {"BOOKKEEPER","SYNCOPE"};
	private static final String[] nameRepos = {"https://github.com/apache/bookkeeper","https://github.com/apache/syncope"};
	private static final String[] proportionMethod = {"movingWindow","increment"};
	private static final int[] propIndexs = {0,1,1,0};

	public static void main(String[] args) throws Exception   {
		for(int i = 0; i!= 4; i++) {	
			//viene creato il file di output contenente tutte le metriche per ogni file java in ogni release
			ControllerSecondelivery.createMetrics(projectName[i%2], new String[] {directories[i%2]}, new String[] {nameRepos[i%2]}, proportionMethod[propIndexs[i]]);
			
			/*viene dato in input il file creato precedentemente e ne viene creato uno di output 
			 * contenente le metriche risultato dei vari modelli di machine learning
			  
			  vengono utilizzati i seguenti modelli:
				naive bayes, RandomForest, IBK, logistic regression
			  vengono utilizati i seguenti tipi di feature selection:
			  	no fs, pca, filter best selection, wrapping con naive bayes
			  vengono utilizzati i seguenti tipi di tecniche di balancing:
			  	undersampling, oversampling, smote, no sampling
			 
			 in particolare viene eseguito un piccolo tuning sul modello ibk dove viene scelto il valore migliore di K tra i seguenti:
			 	1,3,9,19
			 *
			 */
			ControllerSecondelivery.startModelActivity(proportionMethod[propIndexs[i]], directories[i%2], projectName[i%2]);
		}
	}

}
