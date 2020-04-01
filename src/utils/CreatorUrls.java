package utils;

public class CreatorUrls {

	private CreatorUrls() {	
	}

	public static String createUrlCommitsPerPage(String branch, Integer page) {
		if(branch.equals("default")) {
			return Strings.URL_SOURCE_GITHUB+Strings.URI_COMMIT+"?&page="+
					page+Strings.URI_PER_PAGE;
		}
		return Strings.URL_SOURCE_GITHUB+Strings.URI_COMMIT+"?"+Strings.SHA+branch+"&page="+
			page+Strings.URI_PER_PAGE;
	}

	public static String createUrlBranches(Integer page) {
		return Strings.URL_SOURCE_GITHUB+Strings.URI_BRANCHES+"?&page="+page+Strings.URI_PER_PAGE;
	}
}
