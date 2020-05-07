package common.entity;

import java.time.LocalDateTime;

public class CommitEntity {
	
	private LocalDateTime date;
	private String message;
	private String sha;

	public CommitEntity(String sha, String message, LocalDateTime date) {
		this.sha = sha;
		this.message = message;
		this.date = date;
	}
	
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

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
		this.date = date;
	}
	
	

}
