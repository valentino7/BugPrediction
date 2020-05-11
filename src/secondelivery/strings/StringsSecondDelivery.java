package secondelivery.strings;

public class StringsSecondDelivery {

	private StringsSecondDelivery() {}

	//Parametri iniziali
//	public static final String PROPORTION_METHOD = "increment";
	public static final String PROPORTION_METHOD = "movingWindow";

	public static final String OUTPUTFILE = "dataset.csv";
	
	//JIRA	
	public static final String PROJECT_NAME = "BOOKKEEPER";
	public static final String ROOT_URL_RELEASE = "https://issues.apache.org/jira/rest/api/2/project/";
	public static final String ROOT_URL = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22";

	public static final String URL_RELEASES = ROOT_URL_RELEASE + PROJECT_NAME;


	public static final String JQL_BUGS_URL="%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=";
	public static final String URL_BUGS =ROOT_URL+ PROJECT_NAME + JQL_BUGS_URL;

	//GITHUB
	public static final String URL_SOURCE_GITHUB = "https://api.github.com/repos/apache";
	public static final String GITHUB_REPO_BOOKKEEPER = "/bookkeeper";

	public static final String URL_SINGLE_COMMIT = "https://api.github.com/repos/apache/bookkeeper/commits/";
	
	//REGEX
	public static final String REGEX_BOOKKEEPER = "BOOKKEEPER-\\d{1,4}";

}
