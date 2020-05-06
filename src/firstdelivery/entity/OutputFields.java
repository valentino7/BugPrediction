package firstdelivery.entity;


import java.time.LocalDateTime;

public class OutputFields {
	private LocalDateTime date;
	private Integer count;
	
	public OutputFields(LocalDateTime localDateTime,Integer count) {
		this.count=count;
		this.date=localDateTime;
	}

	public void setDate(LocalDateTime date) {
		this.date=date;
	}
	
	public void setCount(Integer count) {
		this.count=count;
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public Integer getCount() {
		return count;
	}
	
}
