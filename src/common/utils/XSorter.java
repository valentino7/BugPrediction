package common.utils;

import java.util.Collections;
import java.util.List;

import common.entity.Bug;
import common.entity.CollectBugs;
import common.entity.CollectCommits;

public class XSorter {

	private XSorter() {}
	
	public static void orderCommitsAndBugsLists(CollectCommits collectCommits, CollectBugs collectBugs) {
		for (Bug bug : collectBugs.getBugsWithCommits()) {
			Collections.sort(bug.getCommits(), (commit1,commit2) -> commit1.getDate().compareTo(commit2.getDate()));
		}
		Collections.sort(collectBugs.getBugsWithCommits(), (bug1,bug2) -> bug1.getOpenDate().compareTo(bug2.getOpenDate()));
		Collections.sort(collectBugs.getBugsWithoutCommits(), (bug1,bug2) -> bug1.getOpenDate().compareTo(bug2.getOpenDate()));
		Collections.sort(collectCommits.getMyTicketCommits(), (commit1,commit2) -> commit1.getDate().compareTo(commit2.getDate()));
		Collections.sort(collectCommits.getNoTicketCommits(), (commit1,commit2) -> commit1.getDate().compareTo(commit2.getDate()));
		Collections.sort(collectCommits.getOtherIdCommits(), (commit1,commit2) -> commit1.getDate().compareTo(commit2.getDate()));
		Collections.sort(collectCommits.getTotalCommits(), (commit1,commit2) -> commit1.getDate().compareTo(commit2.getDate()));
	}

	public static void sortBugsByFixedRelease(List<Bug> bugsWithCommits) {
		//ordina i bug tramite la data della fixed release
		Collections.sort(bugsWithCommits, (bug1,bug2) -> bug1.getFixedRelease().getDate().compareTo(bug2.getFixedRelease().getDate()));
	}

}
