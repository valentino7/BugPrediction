package common.entity;

import java.util.ArrayList;
import java.util.List;

public class CollectCommits {
	private List<CommitEntity> totalCommits;
	private List<CommitEntity> myTicketCommits;
	private List<CommitEntity> noTicketCommits;
	private List<CommitEntity> otherIdCommits;
	
	public CollectCommits() {
		this.totalCommits = new ArrayList<>();
		this.myTicketCommits = new ArrayList<>();
		this.noTicketCommits = new ArrayList<>();
		this.otherIdCommits = new ArrayList<>();
	}

	public List<CommitEntity> getTotalCommits() {
		return totalCommits;
	}

	public void setTotalCommits(List<CommitEntity> totalCommits) {
		this.totalCommits = totalCommits;
	}

	public List<CommitEntity> getMyTicketCommits() {
		return myTicketCommits;
	}

	public void setMyTicketCommits(List<CommitEntity> myTicketCommits) {
		this.myTicketCommits = myTicketCommits;
	}

	public List<CommitEntity> getNoTicketCommits() {
		return noTicketCommits;
	}

	public void setNoTicketCommits(List<CommitEntity> noTicketCommits) {
		this.noTicketCommits = noTicketCommits;
	}

	public List<CommitEntity> getOtherIdCommits() {
		return otherIdCommits;
	}

	public void setOtherIdCommits(List<CommitEntity> otherIdCommits) {
		this.otherIdCommits = otherIdCommits;
	}
	
	
}
