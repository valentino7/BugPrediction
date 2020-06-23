package secondelivery.entity;

public class OutputMl {
	
	private String nameFs;
	private String nameSampling;
	private double nRelease;
	private String model;
	private double percentDefectiveTraining;
	private double percentDefectiveTesting;
	private double percentTrainingOnTotal;
	private int kParam;
	private int epvBefore;
	private int epvAfter;	
	private double precision;
	private double tp;
	private double fp;
	private double tn;
	private double fn;
	private double recall;
	private double fMeasure;
	private double auc;
	private double kappa;
	
	
	public OutputMl(double percentTrainingOnDataset, double nRelease, int epvBefore, int epvAfter, String nameFs) {
		this.nameFs = nameFs;
		this.nRelease = nRelease;
		this.epvAfter = epvAfter;
		this.epvBefore = epvBefore;
		this.percentTrainingOnTotal = percentTrainingOnDataset;
	}

	public int getEpvBefore() {
		return epvBefore;
	}

	public void setEpvBefore(int epvBefore) {
		this.epvBefore = epvBefore;
	}

	public int getEpvAfter() {
		return epvAfter;
	}

	public void setEpvAfter(int epvAfter) {
		this.epvAfter = epvAfter;
	}

	public double getPercentDefectiveTraining() {
		return percentDefectiveTraining;
	}

	public void setPercentDefectiveTraining(double percentDefectiveTraining) {
		this.percentDefectiveTraining = percentDefectiveTraining;
	}

	public double getPercentDefectiveTesting() {
		return percentDefectiveTesting;
	}

	public void setPercentDefectiveTesting(double percentDefectiveTesting) {
		this.percentDefectiveTesting = percentDefectiveTesting;
	}

	public double getPercentTrainingOnTotal() {
		return percentTrainingOnTotal;
	}

	public void setPercentTrainingOnTotal(double percentTrainingOnTotal) {
		this.percentTrainingOnTotal = percentTrainingOnTotal;
	}

	public double getTp() {
		return tp;
	}

	public void setTp(double tp) {
		this.tp = tp;
	}

	public double getFp() {
		return fp;
	}

	public void setFp(double fp) {
		this.fp = fp;
	}

	public double getTn() {
		return tn;
	}

	public void setTn(double tn) {
		this.tn = tn;
	}

	public double getFn() {
		return fn;
	}

	public void setFn(double fn) {
		this.fn = fn;
	}

	public int getkParam() {
		return kParam;
	}

	public void setkParam(int kParam) {
		this.kParam = kParam;
	}

	public double getfMeasure() {
		return fMeasure;
	}

	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
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
