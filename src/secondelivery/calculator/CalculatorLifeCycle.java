package secondelivery.calculator;
import java.util.Iterator;
import java.util.List;
import common.entity.Bug;
import common.entity.CommitEntity;
import common.entity.Project;
import common.entity.Release;
import common.utils.XSorter;
import secondelivery.proportion.CalculatorProportion;
import secondelivery.proportion.ProportionMethod;

public class CalculatorLifeCycle {

	private CalculatorLifeCycle() {}

	public static void calculateLifeCycle(Project project, String proportionMethod) {

		System.out.println("size BUG prima del filtro "+project.getCollectBugs().getBugsWithCommits().size() );
		//set fixed version per tutti i bug
		
		setFixedAndOpenVersion(project.getCollectBugs().getBugsWithCommits(),project.getReleases());
		
		//ordino per fixed version
		
//		for (Bug bug : project.getCollectBugs().getBugsWithCommits()) {
//			for (Release affectedRelease : bug.getAffectedReleases()) {
//				int i=0;
//				for (Release release : project.getReleases()) {
//
//					if(release.equals(bug.getOpenRelease())) {
//						System.out.println("indice versione open " + i);
//					}
//					if(release.getId().equals(affectedRelease.getId())) {
//						System.out.println("indice versione affetta " + i);
//					}
//					i++;
//				}
//			}
//			
//		}
		
		XSorter.sortBugsByFixedRelease(project.getCollectBugs().getBugsWithCommits());
		int percent = project.getCollectBugs().getBugsWithCommits().size() *1 /100;
		if(percent==0)
			percent=1;
		System.out.println("percent "+ percent);
		System.out.println("size BUG dopo il filtro "+project.getCollectBugs().getBugsWithCommits().size() );
		//calcolare injected version e affected version
		setInjectedAndAffectedVersion(percent, project, proportionMethod);
		
		//setta le release affette
		setAffectedRelease(project.getCollectBugs().getBugsWithCommits(), project.getReleases());
	}




	private static void setAffectedRelease(List<Bug> bugs, List<Release> releases) {
		for (Bug bug : bugs) {
			for (Release affectedRelease : bug.getAffectedReleases()) {
				for (Release release : releases) {

					if(release.getId().equals(affectedRelease.getId())) {
						if(Boolean.FALSE.equals(release.getAffected()))
							release.setAffected(Boolean.TRUE);
						break;
					}
				}
			}
			
		}
		
	}

	private static void setFixedAndOpenVersion(List<Bug> bugs,List<Release> releases) {

		Iterator<Bug> iter = bugs.iterator();
		while(iter.hasNext()){
			//fixed version è la release relativa all'ultima commit del bug
			Bug bug = iter.next();
			if ( setFixedVersion(bug,releases,iter) != -1 )
				setOpeningVersion(releases,bug,iter);
		}
	}



	private static int setFixedVersion(Bug bug,List<Release> releases, Iterator<Bug> iter) {
		CommitEntity lastCommits = bug.getCommits().get(bug.getCommits().size()-1);

		boolean isFixed = false;
		for (Release release : releases) {

			if(release.getDate().compareTo(lastCommits.getDate())>0) {
				isFixed=true;
				bug.setFixedRelease(release);
				break;
			}
		}
		//CANCELLO BUG SENZA FIXEDVERSION
		if(!isFixed) {
			System.out.println("Non esiste relase fixed RIMUOVO-bug: "+ String.valueOf(bug.getId())+ " commit id: "+lastCommits.getDate().toString());
			iter.remove();
			return -1;
		}
		return 1;
	}


	private static void setOpeningVersion(List<Release> releases,Bug bug, Iterator<Bug> iter) {
		//get release tra quelle affette che ha data >= data della commit dopo l'apertura		
		boolean isOpening = false;
		for (Release release : releases) {
			if(release.getDate().compareTo(bug.getOpenDate())>0 ) {
				//caso in cui il bug ha AV jira e la versione dopo l'open è minore AV Jira
//				if(!bug.getAffectedReleases().isEmpty() && release.getDate().compareTo(bug.getAffectedReleases().get(0).getDate()) < 0 )
//					bug.setOpenRelease(bug.getAffectedReleases().get(0));
//				else
				bug.setOpenRelease(release);
				//caso in cui il bug ha AV jira e la versione dopo l'open è minore AV Jira
				//creo una lista con affette le versioni dall'opening in poi
				if(!bug.getAffectedReleases().isEmpty() && release.getDate().compareTo(bug.getAffectedReleases().get(0).getDate()) < 0 ) {
					//bug.setAffectedReleases(addOVtoAV(releases,release,bug.getAffectedReleases()));
					bug.setAffectedReleases(releases.subList(releases.indexOf(release),
							releases.indexOf(bug.getAffectedReleases().get(bug.getAffectedReleases().size()-1))+1));
					bug.getAffectedReleases().stream().forEach(r->r.setAffected(Boolean.TRUE));
				}
				
				//rimuovo il bug se open version = o > fixed version
				if(bug.getFixedRelease().getDate().compareTo(bug.getOpenRelease().getDate())<=0) {
					iter.remove();
				}
				isOpening=true;
				break;
			}
		}
		//CANCELLO VERSIONI SENZA OPEN VERSION
		if(!isOpening) {
			System.out.println("RIMUOVO VERSIONE SENZA OPENING, SONO AFFECTED? :"+ bug.getAffectedReleases().size()+" BUG ID: "+bug.getId()+" openDate: "+bug.getOpenDate().toString());
			iter.remove();
		}
	}

	
	

	private static void setInjectedAndAffectedVersion(int percent,Project project, String proportionMethod) {
		boolean seenAffectedRelease = false;
		Iterator<Bug> iter = project.getCollectBugs().getBugsWithCommits().iterator();
		ProportionMethod calculator = new CalculatorProportion();
		
		while(iter.hasNext()){
			//fixed version è la release relativa all'ultima commit del bug
			Bug bug = iter.next();
			if(bug.getAffectedReleases().isEmpty() ) {
				if(!seenAffectedRelease)
					//simple method
					bug.setInjectedRelease(bug.getOpenRelease());
				else {
					//calcola P 
					calculateProportion(proportionMethod, calculator, project.getReleases(), project.getCollectBugs().getBugsJiraAV(), bug, percent);
					//calcola injection version
					CalculatorProportion.setInjectionVersion(project.getReleases(), bug);
				}

				//calcola affected version, sono affette tutte le versioni dall opening alla fixed
				CalculatorProportion.setAffectedVersion(bug,project.getReleases());

			}else {
				if (!seenAffectedRelease)
					seenAffectedRelease=true;
				bug.setInjectedRelease(bug.getAffectedReleases().get(0));
				project.getCollectBugs().getBugsJiraAV().add(bug);
			}	
		}		
	}

	public static void calculateProportion(String proportionMethod, ProportionMethod calculator ,List<Release> releases,List<Bug> bugsAVJira, Bug bug,int percent) {
		if(proportionMethod.equals("movingWindow")) {
			System.out.println(proportionMethod);
			calculator.calculateProportionMovingWindow(releases,bugsAVJira, bug,percent);
		}else if(proportionMethod.equals("increment") ) {
			System.out.println(proportionMethod);
			calculator.calculateProportionIncrement(releases,bugsAVJira, bug);
		}
	}
	
}
