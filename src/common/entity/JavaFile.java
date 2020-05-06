package common.entity;

public class JavaFile {

	private String status;
	private String filename;
	//in caso di copy
	private String oldPath;
	private Metrics metrics;
	private Integer numCreatedLines;
	private Integer numDeletedLines;
	private Boolean isDeleted;

	
	public JavaFile(String filename, Metrics metrics, Integer createdLines, Integer deletedLines, String status) {
		this.isDeleted = Boolean.FALSE;
		this.filename = filename;
		this.status = status;
		this.metrics = metrics;
		this.numCreatedLines = createdLines;
		this.numDeletedLines = deletedLines;
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
