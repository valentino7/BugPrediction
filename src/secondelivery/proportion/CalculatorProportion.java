package secondelivery.proportion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.entity.Bug;
import common.entity.Proportion;
import common.entity.Release;

public class CalculatorProportion implements ProportionMethod {


	
	@Override
	public void calculateProportionMovingWindow(List<Release> releases, List<Bug> bugAVJira, Bug bug, int percent) {
		//calcolo proportion su 1% dei ticket precedenti
		//Utilizzo tutti i ticket
		//scorrere tutti i bug fino a quello attuale,calcolare P

		//calcolare indice del bug 1% indietro
		int currentIndex = bugAVJira.size();
		int startIndex = currentIndex-percent;
		if (startIndex<0 )
			startIndex=0;
		float[] proportions = new float[percent];
		//P = (FV-IV) / (FV-OV) 
		int j=0;
		float sum = 0;
		for(int i=startIndex; i!= bugAVJira.size();j++, i++) {
			Proportion p = getFactors(releases,bugAVJira,i);
			float d = (p.getFv() - p.getOv());
			proportions[j] = (p.getFv() - p.getIv() ) / d;
			sum += proportions[j];
		}
		//calcolo la media
		bug.setProportion(new Proportion(sum/(j+1)));
	}

	@Override
	public void calculateProportionIncrement(List<Release> releases, List<Bug> bugAVJira, Bug bug) {
		//calcolo proportion su 1% dei ticket precedenti
		//Utilizzo tutti i ticket
		//scorrere tutti i bug fino a quello attuale,calcolare P

		
		float[] proportions = new float[bugAVJira.size()];
		//P = (OV-IV) / (FV-OV) 
		float sum = 0;
		for(int i=0; i!= bugAVJira.size(); i++) {
			Proportion p = getFactors(releases,bugAVJira,i);
			float d = (p.getFv() - p.getOv());

			proportions[i] = (p.getFv() - p.getIv() ) / d;
			sum += proportions[i];
		}
		//calcolo la media
		bug.setProportion(new Proportion(sum/bugAVJira.size()));
				
	}


	private static Proportion getFactors(List<Release> releases,List<Bug> bugAVJira,int index) {
		String injectionRelease = bugAVJira.get(index).getInjectedRelease().getId();
		String openRelease = bugAVJira.get(index).getOpenRelease().getId();
		String fixedRelease = bugAVJira.get(index).getFixedRelease().getId();

		float iv = 0;
		float ov = 0;
		float fv = 0;
		int i = 0;
		for (Release release : releases) {
			if(release.getId().equals(injectionRelease))
				iv = i;
			if(release.getId().equals(openRelease))
				ov = i;
			if(release.getId().equals(fixedRelease))
				fv = i;		
			i++;
		}
		return new Proportion(iv,fv,ov);
	}

	public static void setInjectionVersion(List<Release> releases, Bug bug) {
		//IV = FV – (FV – OV)*P
		int i=0;
		float ov = 0;
		float fv = 0;
		for (Release release :releases) {
			if(release.getId().equals(bug.getOpenRelease().getId()))
				ov = i;
			if(release.getId().equals(bug.getFixedRelease().getId()))
				fv = i;		
			i++;
		}
		int iv = Math.round( (fv - ov)*bug.getProportion().getP() );

		iv = checkSimpleMethod(iv,Math.round( ov ));

		bug.setInjectedRelease(releases.get(iv));
	}

	private static int checkSimpleMethod(int iv, int ov) {
		if (iv > ov)
			iv = ov;
		return iv;
	}

	private static void addReleaseAffected(List<Release> listAffected, List<Release> releases, Release injectionRelease, LocalDateTime fixedDate) {
		for (Release release : releases) {
			//aggiungo tutte le versioni da IV a FV esclusa, tanto per essere sicuri controllo che IV è ha anche id uguale
			if((release.getDate().compareTo(injectionRelease.getDate())>0 && release.getDate().compareTo(fixedDate)<0 ) ||
					(release.getDate().compareTo(injectionRelease.getDate())==0 && release.getId().equals(injectionRelease.getId()))  ) {
				release.setAffected(Boolean.TRUE);
				listAffected.add(release);
			}
		}
	}

	public static void setAffectedVersion(Bug bug,List<Release> releases) {
		List<Release> listAffected = new ArrayList<>();
		addReleaseAffected(listAffected,releases,bug.getInjectedRelease(),bug.getFixedRelease().getDate());
		bug.setAffectedReleases(listAffected);
	}
}
