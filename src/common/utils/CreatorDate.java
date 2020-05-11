package common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CreatorDate {
	
	private CreatorDate() {}

	public static LocalDateTime defaultParseWithoutHour(String strDate) {
		return LocalDate.parse(strDate).atStartOfDay();
	}
	
	public static LocalDateTime parseSpecificFormat(String strDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return LocalDateTime.parse(strDate,formatter);
	}
	
	public static LocalDateTime defaultParseDate(String strDate) {
		//2012-10-19T10:08:27
		return LocalDateTime.parse(strDate.substring(0,19));
	}

	public static LocalDateTime getLocalDateTimeByDate(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Z"));
	}

	public static LocalDateTime getLocalDateYearMonth(Integer year, Integer month) {
		return LocalDate.of(year, month, 1).atStartOfDay();	
	}

	public static Integer getNumeWeek(LocalDateTime startDate, LocalDateTime endDate) {
		return (int) ChronoUnit.WEEKS.between(startDate, endDate);
	}

}
