package common.entity;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String projName;
	private String[] urlsRepos;
	private CollectBugs collectBugs;
	private CollectCommits collectCommits;
	private List<Release> releases;
	
	public Project(String projName, String[] urlsRepos) {
		this.projName = projName;
		this.releases = new ArrayList<>();
		this.urlsRepos = urlsRepos;
	}

	public List<Release> getReleases() {
		return this.releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}

	public CollectBugs getCollectBugs() {
		return this.collectBugs;
	}

	public void setCollectBugs(CollectBugs collectBugs) {
		this.collectBugs = collectBugs;
	}

	public CollectCommits getCollectCommits() {
		return this.collectCommits;
	}

	public void setCollectCommits(CollectCommits collectCommits) {
		this.collectCommits = collectCommits;
	}

	public String getName() {
		return this.projName;
	}

	public void setName(String projName) {
		this.projName = projName;
	}

	public String[] getUrlsRepo() {
		return this.urlsRepos;
	}

	public void setUrlsRepo(String[] urlsRepos) {
		this.urlsRepos = urlsRepos;
	}

	
	
	
}
