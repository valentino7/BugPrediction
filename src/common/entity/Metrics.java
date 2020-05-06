package common.entity;

public class Metrics {

	private Integer nfix;
	private Integer nr;
	private Boolean buggy;
	private Integer loc;
	
	public Metrics(Integer nfix) {
		this.buggy = Boolean.FALSE;
		this.nfix = nfix;
		this.nr = 1;
	}

	public Integer getLoc() {
		return this.loc;
	}

	public void setLoc(Integer loc) {
		this.loc = loc;
	}

	public Integer getNfix() {
		return this.nfix;
	}

	public void setNfix(Integer nfix) {
		this.nfix = nfix;
	}

	public Integer getNr() {
		return this.nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Boolean getBuggy() {
		return this.buggy;
	}

	public void setBuggy(Boolean bugginess) {
		this.buggy = bugginess;
	}
	
}
