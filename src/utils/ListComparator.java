package utils;

import java.util.Calendar;
import java.util.Comparator;

public class ListComparator implements Comparator<FieldsQuery> {

	public int compare(FieldsQuery p1, FieldsQuery p2) {
		
		if(p1.getDate().get(Calendar.YEAR) > p2.getDate().get(Calendar.YEAR))
			return 1;
		else if(p1.getDate().get(Calendar.YEAR) < p2.getDate().get(Calendar.YEAR))
			return -1;
		else if(p1.getDate().get(Calendar.YEAR) == p2.getDate().get(Calendar.YEAR) &&
				p1.getDate().get(Calendar.MONTH) > p2.getDate().get(Calendar.MONTH))
			return 1;
		else if(p1.getDate().get(Calendar.YEAR) == p2.getDate().get(Calendar.YEAR) &&
				p1.getDate().get(Calendar.MONTH) < p2.getDate().get(Calendar.MONTH))
			return -1;
		else
			return 0;
	}

}