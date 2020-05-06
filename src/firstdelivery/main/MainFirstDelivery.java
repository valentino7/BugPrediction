package firstdelivery.main;

import firstdelivery.controller.ControllerFirstdelivery;

public class MainFirstDelivery {

	private static final String[] nameRepos = {"https://api.github.com/repos/apache/parquet-mr"};
//	private static final String[] nameRepos = {"https://api.github.com/repos/apache/parquet-mr","https://api.github.com/repos/apache/arrow","https://api.github.com/repos/apache/arrow-testing"};

	
	public static void main(String[] args) {  
		

		ControllerFirstdelivery.startDelivery(nameRepos);
	}






}




