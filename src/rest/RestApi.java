package rest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import io.JsonReader;
import utils.CreatorUrls;
import utils.Strings;

public class RestApi {

	private RestApi() {
	}

	public static List<String> getProjectBug() throws IOException {
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		//Get JSON API for closed bugs w/ AV in the project
		List<String> bugsList = new ArrayList<>();
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
			j = i + 1000;
			//get bugs
			String url = Strings.URL+ i.toString() + "&maxResults=" + j.toString();
			JSONObject json  = JsonReader.readJsonFromUrl(url);
			JSONArray issues = json.getJSONArray("issues");
			total = json.getInt("total");

			for (; i < total && i < j; i++) {
				//Iterate through each bug
				bugsList.add(issues.getJSONObject(i%1000).get("key").toString());
			}      
		} while (i < total);
		return bugsList;
	}


	public static List<String> getBranches() throws IOException{
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
				json  = JsonReader.readJsonArrayFromUrl(CreatorUrls.createUrlBranches(page));
			} catch (IOException e) {
				break;
			}		
		}
		return branches;
	}

	public static List<String> getCommitsFromGithub() throws IOException {
		//get commit,get mess, get date

		List<String> branchesList = getBranches();
		List<String> commitList = new ArrayList<>();
		Integer page = 1;
		JSONArray json = JsonReader.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(0), page));


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
					json  = JsonReader.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(j), page));
				} catch (IOException e) {	
					break;
				}		
			}
			page=1;
		}
		return commitList;
	}


}
