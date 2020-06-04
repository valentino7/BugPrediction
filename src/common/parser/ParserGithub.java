package common.parser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import common.entity.Bug;
import common.entity.CollectBugs;
import common.entity.CollectCommits;
import common.entity.CommitEntity;
import common.rest.JsonRest;
import common.strings.Strings;
import common.utils.CreatorDate;
import common.utils.CreatorUrls;
import common.utils.XSorter;

public class ParserGithub {

	private ParserGithub() {}

	
	public static CollectCommits getCommitsByAllBranches(CollectBugs collectBugs, List<Bug> bugs, String[] repos) {

		CollectCommits collectCommits = new CollectCommits();
		Integer page = 1;
		for (String repo : repos) {
			List<String> branchesList = getBranches(repo);
			for (Integer j=0; j< branchesList.size();j++) {

			
				JSONArray json = null;
				try {
					json = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(j), page, repo));
				} catch (IOException e1) {
					Logger.getLogger(ParserGithub.class.getName()).log( Level.SEVERE, e1.toString(), e1);
					return collectCommits;
				}
				while(json.length()!=0 ) {
					
					fillCommits(collectCommits,collectBugs,bugs,json);
					page++;
					try {	
						json  = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(branchesList.get(j), page,repo));
					} catch (IOException e) {	
						break;
					}		
				}
				page=1;
			}
		}
		//serve riscorrere i bug infine per riempire i bug senza commit 
		fillBugWithoutCommit(bugs,collectBugs);
		
		//ordino tutte le liste
		XSorter.orderCommitsAndBugsLists(collectCommits,collectBugs);
		return collectCommits;
	}

	
	
	
	private static List<String> getBranches(String repo) {
		Integer page = 1;

		List<String> branches = new ArrayList<>();
		JSONArray json = null;
		try {
			json = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlBranches(page, repo));
		} catch (IOException e1) {
			Logger.getLogger(ParserGithub.class.getName()).log( Level.SEVERE, e1.toString(), e1);
			return branches;
		}


		while(json.length()!=0 ) {
			for (int i = 0; i < json.length(); i++) {
				JSONObject commit = (JSONObject) json.getJSONObject(i).get(Strings.FIELD_COMMIT);
				branches.add(commit.get(Strings.FIELD_SHA).toString());
			}
			page++;
			try {
				json  = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlBranches(page, repo));
			} catch (IOException e) {
				break;
			}		
		}
		return branches;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static CollectCommits getCommitsDefaultBranch(CollectBugs collectBugs, List<Bug> bugs, String[] repos) {

		CollectCommits collectCommits = new CollectCommits();
		Integer page = 1;
		for (String repo : repos) {
			JSONArray json = null;
			try {
				json = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(Strings.DEFAULT, page, repo));
			} catch (IOException e1) {
				Logger.getLogger(ParserGithub.class.getName()).log( Level.SEVERE, e1.toString(), e1);
				return collectCommits;
			}
			while(json.length()!=0 ) {
				
				fillCommits(collectCommits,collectBugs,bugs,json);
				page++;
				try {	
					json  = JsonRest.readJsonArrayFromUrl(CreatorUrls.createUrlCommitsPerPage(Strings.DEFAULT, page,repo));
				} catch (IOException e) {	
					break;
				}		
			}
			page=1;
		}
		//serve riscorrere i bug infine per riempire i bug senza commit 
		fillBugWithoutCommit(bugs,collectBugs);
		
		//ordino tutte le liste
		XSorter.orderCommitsAndBugsLists(collectCommits,collectBugs);
		return collectCommits;
	}


	private static void fillBugWithoutCommit(List<Bug> bugs, CollectBugs collectBugs) {
		for (Bug bug : bugs) {
			if(!collectBugs.getBugsWithCommits().contains(bug))
				collectBugs.getBugsWithoutCommits().add(bug);			
		}
	}


	private static void fillCommits(CollectCommits collectCommits,CollectBugs collectBugs,List<Bug> bugs, JSONArray json) {
		boolean isBugInCommit;
		for (int i = 0; i < json.length(); i++) {
			isBugInCommit = false;
			
			String sha =  json.getJSONObject(i).get(Strings.FIELD_SHA).toString();				
			JSONObject jsonCommit =  (JSONObject) json.getJSONObject(i).get(Strings.FIELD_COMMIT);
			String message = jsonCommit.get(Strings.FIELD_MESSAGE).toString();
			JSONObject committer = (JSONObject) jsonCommit.get(Strings.FIELD_COMMITTER);
			//2020-05-02T22:26:38Z
			String strDate = committer.get(Strings.FIELD_DATE).toString();
			LocalDateTime date = CreatorDate.parseSpecificFormat(strDate);
			CommitEntity commit = new CommitEntity(sha,message,date);
			for (Bug bug : bugs) {
				if(Pattern.compile(bug.getId()+"\\D"+"|"+bug.getId()+"\\b").matcher(message).find()) {
					isBugInCommit = true;
					addBugIfNotExists(collectBugs.getBugsWithCommits(),bug);
					addCommitIfNotExists(collectCommits.getMyTicketCommits(),commit);
					bug.getCommits().add(commit);
				}	
			}
			//se la commit non è stata aggiunta a nessun bug e matcha con un altro Ticket di un altro progetto, aggiungo nella seconda lista
			if(!isBugInCommit && Pattern.compile(Strings.REGEX_OTHER_ID).matcher(message).find())
				//negli altri branch potrei avere commit uguali
				addCommitIfNotExists(collectCommits.getOtherIdCommits(),commit);
			else if (!isBugInCommit)
				addCommitIfNotExists(collectCommits.getNoTicketCommits(),commit);
			
			addCommitIfNotExists(collectCommits.getTotalCommits(),commit);

		}
	
	}

	
	private static void addCommitIfNotExists(List<CommitEntity> myTicketCommits, CommitEntity commit) {
		if(myTicketCommits.contains(commit))
			return;
		myTicketCommits.add(commit);
	}


	private static void addBugIfNotExists(List<Bug> bugs, Bug addBug) {
		if(bugs.contains(addBug))
			return;
		bugs.add(addBug);
	}



}
