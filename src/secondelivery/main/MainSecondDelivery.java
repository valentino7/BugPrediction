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
			ControllerSecondelivery.createMetrics(projectName[i%2], new String[] {directories[i%2]}, new String[] {nameRepos[i%2]}, proportionMethod[propIndexs[i]]);
			ControllerSecondelivery.startModelActivity(proportionMethod[propIndexs[i]], directories[i%2], projectName[i%2]);
		}
	}

}
