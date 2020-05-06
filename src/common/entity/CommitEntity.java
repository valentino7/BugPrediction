package common.entity;

import java.time.LocalDateTime;

public class CommitEntity {
	
	private LocalDateTime date;
	private String sha;

	public CommitEntity() {}
	
	
	
	public String getSha() {
		return this.sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = this.date;
	}
	
	

}
