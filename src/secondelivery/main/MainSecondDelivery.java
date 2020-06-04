package secondelivery.main;

import secondelivery.controller.ControllerSecondelivery;
import secondelivery.strings.StringsSecondDelivery;

public class MainSecondDelivery {

	//lista dei repository
	private static final String[] directories = {"bookkeeper"};
	private static final String[] nameRepos = {"https://github.com/apache/bookkeeper"};

	public static void main(String[] args) throws Exception   {
		ControllerSecondelivery.startDelivery(directories,nameRepos, StringsSecondDelivery.PROPORTION_METHOD);
		ControllerSecondelivery.startModelActivity();
	}

}
