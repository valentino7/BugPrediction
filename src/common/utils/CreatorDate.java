package common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreatorDate {
	
	private CreatorDate() {}

	public static LocalDateTime getLocalDateYearMonth(Integer i, Integer month) {
		return null;
	}
	
	public static LocalDateTime defaultParseWithoutHour(String strDate) {
		return LocalDate.parse(strDate).atStartOfDay();
	}
	
	public static LocalDateTime defaultParseDate(String strDate) {
		//2012-10-19T10:08:27
		return LocalDateTime.parse(strDate.substring(0,19));
	}
	//default parsing
	
	
	//parsing with hour minutes
	
	
	//parsing with timezone

}
