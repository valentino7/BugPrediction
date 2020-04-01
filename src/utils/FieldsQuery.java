package utils;

import java.util.Calendar;

public class FieldsQuery {
	private Calendar date;
	private Integer count;
	
	public FieldsQuery(Calendar date,Integer count) {
		this.count=count;
		this.date=date;
	}

	public void setDate(Calendar date) {
		this.date=date;
	}
	
	public void setCount(Integer count) {
		this.count=count;
	}

	public Calendar getDate() {
		return date;
	}
	
	public Integer getCount() {
		return count;
	}
	
}
