package secondelivery.proportion;

import java.util.List;

import common.entity.Bug;
import common.entity.Release;

public interface ProportionMethod {

	public void calculateProportionMovingWindow(List<Release> releases,List<Bug> bugAVJira,Bug bug,int percent);
	
	public void calculateProportionIncrement(List<Release> releases,List<Bug> bugAVJira,Bug bug);
	
}
