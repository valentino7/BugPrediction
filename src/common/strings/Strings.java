package common.strings;

public class Strings {
	
	private Strings() {}
	
	public static final String PROJ_NAME ="PARQUET";
	
	//GITHUB	
	public static final String TOKEN = "89f9f8799334640ff0e621508fb5c617051bf21e";
	public static final String DEFAULT = "default";
	public static final String SHA = "sha=";
	public static final String PER_PAGE = "100";
	public static final String URI_PER_PAGE = "&per_page=100";
	public static final String URI_BRANCHES = "/branches";
	public static final String URI_COMMIT = "/commits";
	public static final String URL_SOURCE_GITHUB = "https://api.github.com/repos/apache/";
	public static final String URL_SINGLE_COMMIT = "https://api.github.com/repos/apache/parquet-mr/git/commits";

	public static final String FIELD_COMMIT = "commit";
	public static final String FIELD_COMMITTER = "committer";
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_SHA = "sha";

	
	
	public static final String REST = "rest";
	public static final String FILE = "file";

	//REGEX
	public static final String REGEX_TREE_JAVA = "\\b.java$";
	public static final String REGEX_OTHER_ID = "([A-Z]+[-])\\d{1,4}";
	
	
	//JIRA
	public static final String URL_SOURCE_RELEASE = "https://issues.apache.org/jira/rest/api/2/project/";
	public static final String URL_SOURCE_BUG = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22";
	public static final String URL_JQL = "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt=";
	public static final String URL_JQL_ONLY_KEY = "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)&fields=key";

	//JIRA FIELD
	public static final String FIELD_NAME = "name";
	public static final String FIELD_RELEASE_DATE = "releaseDate";
	public static final String FIELD_ID = "id";
	public static final String FIELD_ISSUES = "issues";
	public static final String FIELD_TOTAL = "total";
	public static final String FIELD_KEY = "key";
	public static final String FIELD_CREATED = "created";
	public static final String FIELD_FIELDS = "fields";
	public static final String FIELD_VERSIONS = "versions";

}
