package common.entity;

import java.util.ArrayList;
import java.util.List;

public class Metrics {

	private Integer nfix;
	private Integer nr;
	private Boolean buggy;
	private Integer loc;
	private Integer locAdded;
	private List<Integer> listLocAdded;
	private Integer maxLocAdded;
	private Float avgLocAdded;
	private Integer locTouched;
	private Integer nAuth;
	private Integer churn;
	private Integer maxChurn;
	private Float avgChurn;
	private List<Integer> listChurn;
	
	private Integer ageDotLoc;
	private Integer age;
	private Integer weightedAge;
	
	private List<Integer> listChgset;
	private Integer instantChgset;
	private Integer chgset;
	private Integer maxChgset;
	private Float avgChgset;
	private Integer numAuth;
	
	public Metrics(Integer nfix, Integer instantChgset) {
		this.buggy = Boolean.FALSE;
		this.nfix = nfix;
		this.instantChgset = instantChgset;
		this.nr = 1;
		this.listLocAdded = new ArrayList<>();
		this.listChurn = new ArrayList<>();
		this.listChgset = new ArrayList<>();
	}

	public Integer getNumAuth() {
		return numAuth;
	}

	public void setNumAuth(Integer numAuth) {
		this.numAuth = numAuth;
	}

	public List<Integer> getListChgset() {
		return listChgset;
	}

	public void setListChgset(List<Integer> listChgset) {
		this.listChgset = listChgset;
	}

	public Integer getInstantChgset() {
		return instantChgset;
	}

	public void setInstantChgset(Integer instantChgset) {
		this.instantChgset = instantChgset;
	}

	public Integer getChgset() {
		return chgset;
	}

	public void setChgset(Integer chgset) {
		this.chgset = chgset;
	}

	public Integer getMaxChgset() {
		return maxChgset;
	}

	public void setMaxChgset(Integer maxChgset) {
		this.maxChgset = maxChgset;
	}

	public Float getAvgChgset() {
		return avgChgset;
	}

	public void setAvgChgset(Float avgChgset) {
		this.avgChgset = avgChgset;
	}

	public Integer getAgeDotLoc() {
		return ageDotLoc;
	}

	public void setAgeDotLoc(Integer ageDotLoc) {
		this.ageDotLoc = ageDotLoc;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getWeightedAge() {
		return weightedAge;
	}

	public void setWeightedAge(Integer weightedAge) {
		this.weightedAge = weightedAge;
	}

	public Integer getChurn() {
		return churn;
	}

	public void setChurn(Integer churn) {
		this.churn = churn;
	}

	public Integer getMaxChurn() {
		return maxChurn;
	}

	public void setMaxChurn(Integer maxChurn) {
		this.maxChurn = maxChurn;
	}

	public Float getAvgChurn() {
		return avgChurn;
	}

	public void setAvgChurn(Float avgChurn) {
		this.avgChurn = avgChurn;
	}

	public List<Integer> getListChurn() {
		return listChurn;
	}

	public void setListChurn(List<Integer> listChurn) {
		this.listChurn = listChurn;
	}

	public List<Integer> getListLocAdded() {
		return listLocAdded;
	}

	public void setListLocAdded(List<Integer> listLocAdded) {
		this.listLocAdded = listLocAdded;
	}

	public Integer getLocAdded() {
		return locAdded;
	}

	public void setLocAdded(Integer locAdded) {
		this.locAdded = locAdded;
	}

	public Integer getMaxLocAdded() {
		return maxLocAdded;
	}

	public void setMaxLocAdded(Integer maxLocAdded) {
		this.maxLocAdded = maxLocAdded;
	}

	public Float getAvgLocAdded() {
		return avgLocAdded;
	}

	public void setAvgLocAdded(Float avgLocAdded) {
		this.avgLocAdded = avgLocAdded;
	}

	public Integer getLocTouched() {
		return locTouched;
	}

	public void setLocTouched(Integer locTouched) {
		this.locTouched = locTouched;
	}

	public Integer getnAuth() {
		return nAuth;
	}

	public void setnAuth(Integer nAuth) {
		this.nAuth = nAuth;
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
