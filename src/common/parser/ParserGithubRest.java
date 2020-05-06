package common.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import common.entity.Bug;
import common.entity.CollectBugs;
import common.entity.CollectCommits;
import common.rest.JsonRest;
import common.utils.CreatorUrls;

public class ParserGithubRest {

	public static CollectCommits getCommitsDefaultBranch(CollectBugs collectBugs, List<Bug> bugs, String[] repo) {
		return null;
	}

	public static CollectCommits getCommitsByAllBranches(CollectBugs collectBugs, List<Bug> b, String[] repo) {
		return null;
	}
	
	

	public static List<String> getCommitsFromGithub() throws IOException {
		//get commit,get mess, get date

		List<String> branchesList = getBranches();
		List<String> commitList = new ArrayList<>();
		Integer page = 1;
		JSONArray json = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(0), page));


		for (Integer j=0; j< branchesList.size();j++) {

			while(json.length()!=0 ) {
				for (int i = 0; i < json.length(); i++) {
					JSONObject commit =  (JSONObject) json.getJSONObject(i).get("commit");
					JSONObject committer = (JSONObject) commit.get("committer");

					String date = committer.get("date").toString();
					String message = commit.get("message").toString();

					commitList.add(date.substring(0,10)+"\t"+message);
				}
				page++;
				try {	
					json  = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(j), page));
				} catch (IOException e) {	
					break;
				}		
			}
			page=1;
		}
		return commitList;
	}
	
	
	
	

	public static List<String> getBranches() {
		Integer page = 1;

		List<String> branches = new ArrayList<>();
		branches.add("default");
		JSONArray json = JsonReader.readJsonArrayFromUrl(CreatorUrls.createUrlBranches(page));

		while(json.length()!=0 ) {
			for (int i = 0; i < json.length(); i++) {
				JSONObject commit = (JSONObject) json.getJSONObject(i).get("commit");
				branches.add(commit.get("sha").toString());
			}
			page++;
			try {
				json  = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlBranches(page));
			} catch (IOException e) {
				break;
			}		
		}
		return branches;
	}

}
