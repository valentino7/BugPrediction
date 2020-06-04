package firstdelivery.utils;


public final class StringsFirstDelivery {
	
	private StringsFirstDelivery(){}
	
//	//window commits
//	BEGIN_YEAR: 2014
//	FINAL_YEAR: 2020
//	//agosto
//	BEGIN_MONTH: 8
//	FINAL_MONTH: 4

	//REGEX
	public static final String REGEX_PARQUET = "PARQUET-\\d{1,4}";

	//JIRA
	public static final String ROOT_URL= "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22";
	public static final String PROJ_NAME ="PARQUET";
	public static final String JQL_URL="%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=";
	public static final String URL= ROOT_URL+PROJ_NAME+JQL_URL;

	//github
	static final String URL_SOURCE_GITHUB = "https://api.github.com/repos/apache";
	public static final String GITHUB_REPO_ARROW = "/arrow";
	public static final String GITHUB_REPO_ARROW_TESTING = "/arrow-testing";
	public static final String GITHUB_REPO_PARQUET = "/parquet-mr";

	
	//files string
	public static final String LOG_FILENAME = "data\\commitsParquet.txt";
	public static final String OUTPUTFILE = "fixedBug.csv";

	
	//not used
	public static final String URL_SINGLE_COMMIT = "https://api.github.com/repos/apache/parquet-mr/git/commits";
	public static final String KEY = "PARQUET-";
	public static final String REST = "rest";
	public static final String FILE = "file";

}
