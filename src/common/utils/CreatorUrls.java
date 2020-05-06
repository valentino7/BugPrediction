package common.utils;

import common.strings.Strings;

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

	public static String createUrlReleases(String projName) {
		//https://issues.apache.org/jira/rest/api/2/project/BOOKKEEPER
		return  Strings.URL_SOURCE_RELEASE + projName;	
	}
	
	public static String createUrlBugs(String projName,Integer i,Integer j) {
		/*
		 * https://issues.apache.org/jira/rest/api/2/search?jql=project=%22
		 * BOOKKEEPER
		 * %22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&
		 * fields=key,resolutiondate,versions,created&startAt=0&maxResults=1000
		 */
		return  Strings.URL_SOURCE_BUG+ projName + Strings.URL_JQL + i.toString() + "&maxResults=" + j.toString();
	}
}
