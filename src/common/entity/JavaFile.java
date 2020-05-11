package common.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JavaFile {

	private String status;
	private String filename;
	//in caso di copy
	private String oldPath;
	private Metrics metrics;
	private Integer numCreatedLines;
	private Integer numDeletedLines;
	private Boolean isDeleted;
	private List<String> authors;
	private LocalDateTime creationDate;
	private LocalDateTime commitDate;
	
	public JavaFile(String filename, Metrics metrics, Integer createdLines, Integer deletedLines, String status, LocalDateTime commitDate, String author) {
		this.isDeleted = Boolean.FALSE;
		this.filename = filename;
		this.status = status;
		this.metrics = metrics;
		this.numCreatedLines = createdLines;
		this.numDeletedLines = deletedLines;
		this.commitDate = commitDate;
		this.authors = new ArrayList<>();
		this.authors.add(author);
	}



	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public LocalDateTime getCommitDate() {
		return commitDate;
	}

	public void setCommitDate(LocalDateTime commitDate) {
		this.commitDate = commitDate;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public String getOldPath() {
		return oldPath;
	}


	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}

	public String getStatus() {
		return this.status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getFilename() {
		return this.filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public Metrics getMetrics() {
		return this.metrics;
	}


	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}


	public Integer getNumCreatedLines() {
		return this.numCreatedLines;
	}


	public void setNumCreatedLines(Integer createdLines) {
		this.numCreatedLines = createdLines;
	}


	public Integer getNumDeletedLines() {
		return this.numDeletedLines;
	}


	public void setNumDeletedLines(Integer deletedLines) {
		this.numDeletedLines = deletedLines;
	}


	public Boolean getIsDeleted() {
		return this.isDeleted;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	
}
