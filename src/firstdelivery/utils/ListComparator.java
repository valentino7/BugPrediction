package firstdelivery.utils;

import java.time.LocalDateTime;
import java.util.Comparator;

public class ListComparator implements Comparator<LocalDateTime> {

	public int compare(LocalDateTime p1, LocalDateTime p2) {
		
		if(p1.getYear() > p2.getYear())
			return 1;
		else if(p1.getYear() < p2.getYear())
			return -1;
		else if(p1.getYear() == p2.getYear() &&
				p1.getMonthValue() > p2.getMonthValue())
			return 1;
		else if(p1.getYear() == p2.getYear() &&
				p1.getMonthValue() < p2.getMonthValue())
			return -1;
		else
			return 0;
	}

}