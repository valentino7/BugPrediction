package secondelivery.entity;

public class OutputMl {
	
	private String nameFs;
	private String nameSampling;
	private double nRelease;
	private String model;
	private double precision;
	private double recall;
	private double auc;
	private double kappa;
	
	public OutputMl(String nameFs, String nameSampling, double nRelease, String model) {
		this.nameFs = nameFs;
		this.nameSampling = nameSampling;
		this.nRelease = nRelease;
		this.model = model;
	}

	public String getNameFs() {
		return nameFs;
	}

	public void setNameFs(String nameFs) {
		this.nameFs = nameFs;
	}

	public String getNameSampling() {
		return nameSampling;
	}

	public void setNameSampling(String nameSampling) {
		this.nameSampling = nameSampling;
	}

	public double getnRelease() {
		return nRelease;
	}

	public void setnRelease(double nRelease) {
		this.nRelease = nRelease;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getAuc() {
		return auc;
	}

	public void setAuc(double auc) {
		this.auc = auc;
	}

	public double getKappa() {
		return kappa;
	}

	public void setKappa(double kappa) {
		this.kappa = kappa;
	}
	

}
