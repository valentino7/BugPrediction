package logger;

import org.apache.log4j.PropertyConfigurator;

public class MyLog4J {

	private MyLog4J() {
	}


	
	public static void setProperties() {
		//load configuration File
		PropertyConfigurator.configure("src\\logger\\properties");
		//get Logger Instance
		//writing some logs at different levels
	 }
}