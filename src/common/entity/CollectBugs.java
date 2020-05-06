package common.entity;

import java.util.ArrayList;
import java.util.List;

public class CollectBugs {
	
	private List<Bug> bugsWithCommits;
	private List<Bug> bugsWithoutCommits;
	private List<Bug> bugsJiraAV;
	
	public CollectBugs() {
		this.bugsWithCommits = new ArrayList<>();
		this.bugsWithoutCommits = new ArrayList<>();
		this.bugsJiraAV = new ArrayList<>();
	}

	
	public List<Bug> getBugsJiraAV() {
		return bugsJiraAV;
	}


	public void setBugsJiraAV(List<Bug> bugsJiraAV) {
		this.bugsJiraAV = bugsJiraAV;
	}


	public List<Bug> getBugsWithCommits() {
		return this.bugsWithCommits;
	}

	public void setBugsWithCommits(List<Bug> bugsWithCommits) {
		this.bugsWithCommits = bugsWithCommits;
	}

	public List<Bug> getBugsWithoutCommits() {
		return this.bugsWithoutCommits;
	}

	public void setBugsWithoutCommits(List<Bug> bugsWithoutCommits) {
		this.bugsWithoutCommits = bugsWithoutCommits;
	}
	
	

}
