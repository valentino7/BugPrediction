package common.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bug {
	
	List<CommitEntity> commits ;
	private String id;
	private Proportion proportion;
	private LocalDateTime openDate;
	private List<Release> affectedReleases;
	private Release openRelease;
	private Release fixedRelease;
	private Release injectedRelease;

	public Bug(String id, LocalDateTime openDate, List<Release> affectedReleases ) {
		this.commits = new ArrayList<>();
		this.openDate = openDate;
		this.id = id;
		this.affectedReleases = affectedReleases;
	}
	
	public Proportion getProportion() {
		return this.proportion;
	}

	public void setProportion(Proportion proportion) {
		this.proportion = proportion;
	}

	public List<CommitEntity> getCommits() {
		return this.commits;
	}

	public void setCommits(List<CommitEntity> commits) {
		this.commits = commits;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getOpenDate() {
		return this.openDate;
	}

	public void setOpenDate(LocalDateTime openDate) {
		this.openDate = openDate;
	}

	public List<Release> getAffectedReleases() {
		return this.affectedReleases;
	}

	public void setAffectedReleases(List<Release> affectedReleases) {
		this.affectedReleases = affectedReleases;
	}

	public Release getOpenRelease() {
		return this.openRelease;
	}

	public void setOpenRelease(Release openRelease) {
		this.openRelease = openRelease;
	}

	public Release getFixedRelease() {
		return this.fixedRelease;
	}

	public void setFixedRelease(Release fixedRelease) {
		this.fixedRelease = fixedRelease;
	}

	public Release getInjectedRelease() {
		return this.injectedRelease;
	}

	public void setInjectedRelease(Release injectedRelease) {
		this.injectedRelease = injectedRelease;
	}


}
