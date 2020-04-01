package utils;

public final class Strings {
	
	private Strings(){
	}
	
	public static final String ROOT_URL= "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22";
	public static final String PROJ_NAME ="PARQUET";
	public static final String JQL_URL="%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)&fields=key";
	
	public static final String TOKEN = "76487d136ce4dd4f8def9fe63f0ef43000ca21d5";
	public static final String SHA = "sha=";
	public static final String PER_PAGE = "100";
	public static final String URI_PER_PAGE = "&per_page=100";
	public static final String URI_BRANCHES = "/branches";
	public static final String URI_COMMIT = "/commits";
	public static final String URL_SOURCE_GITHUB = "https://api.github.com/repos/apache/parquet-mr";
	public static final String URL_SINGLE_COMMIT = "https://api.github.com/repos/apache/parquet-mr/git/commits";

	
	public static final String URL= ROOT_URL+PROJ_NAME+JQL_URL;
	public static final String LOG_FILENAME = "data\\commitsParquet.txt";
	
	public static final String KEY = "PARQUET-";
	
	public static final String REST = "rest";
	public static final String FILE = "file";

}
