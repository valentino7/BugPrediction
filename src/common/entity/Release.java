package common.entity;

import java.time.LocalDateTime;

public class Release {
	private String id;
	private String name;
	private LocalDateTime date;
	private Boolean affected;
	
	public Release(String id, String name, LocalDateTime dateTime) {
		this.id = id;
		this.name = name;
		this.date = dateTime;
		this.affected = Boolean.FALSE;
	}
	
	public Boolean getAffected() {
		return this.affected;
	}

	public void setAffected(Boolean affected) {
		this.affected = affected;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalDateTime getDate() {
		return this.date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
}
