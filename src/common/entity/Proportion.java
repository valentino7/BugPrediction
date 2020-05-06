package common.entity;

public class Proportion {
	
	private float iv;
	private float ov;
	private float fv;
	private float p;
	
	public Proportion(float p) {
		this.p = p;
	}
	
	public Proportion(float iv, float fv, float ov) {
		this.ov = ov;
		this.fv = fv;
		this.iv = iv;
	}

	public float getIv() {
		return this.iv;
	}

	public void setIv(float iv) {
		this.iv = iv;
	}

	public float getOv() {
		return this.ov;
	}

	public void setOv(float ov) {
		this.ov = ov;
	}

	public float getFv() {
		return this.fv;
	}

	public void setFv(float fv) {
		this.fv = fv;
	}

	public float getP() {
		return this.p;
	}

	public void setP(float p) {
		this.p = p;
	}

	
	
}
