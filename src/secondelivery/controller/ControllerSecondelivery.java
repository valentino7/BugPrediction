package secondelivery.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import secondelivery.calculator.CalculatorLifeCycle;
import secondelivery.calculator.CalculatorMetrics;
import secondelivery.io.WriteDataset;
import secondelivery.strings.StringsSecondDelivery;
import common.entity.Bug;
import common.entity.CollectCommits;
import common.entity.JavaFile;
import common.entity.Project;
import common.entity.Release;
import common.exceptions.MyException;
import common.parser.ParserJgit;
import org.eclipse.jgit.api.Git;
import common.parser.ParserJira;




public class ControllerSecondelivery {


	private ControllerSecondelivery() {
	}


	public static void startDelivery(String[] directories, String[] urlsRepos, String proportionMethod) throws MyException  {
		Project project = new Project(StringsSecondDelivery.PROJECT_NAME,urlsRepos);

		//prendo le versioni da jira
		List<Release> releases = ParserJira.getRelease(project.getName());	
		project.setReleases(releases);

		//prendo i bug con le relative versioni
		List<Bug> bugs = ParserJira.getProjectBug(project.getName());

		//correggi possibili errori se le affected version dei bug non sono continue
		correctPossibleErrorsAV(bugs,releases);
		
		//get repositories potrei avere piu repo per un progetto
		List<Git> repos = ParserJgit.getRepo(project.getUrlsRepo(),directories);
		
		//prendo le commit e le assegno ai bug
		CollectCommits collectCommits = ParserJgit.getCommitsDefaultBranch(project.getCollectBugs(),bugs,repos);
		project.setCollectCommits(collectCommits);

		//calcolo le classi buggy
		CalculatorLifeCycle.calculateLifeCycle(project,repos,proportionMethod);
		
		HashMap<String,List<JavaFile>> hRelFile = CalculatorMetrics.calculateMetrics(repos, project.getReleases(),project.getCollectCommits());
		
		WriteDataset.writeCSVOnFile(hRelFile);

	}

	
	private static void correctPossibleErrorsAV(List<Bug> bugs, List<Release> releases) {
		for (Bug bug : bugs) {
			List<Release> correctreleases = new ArrayList<>();
			if(!bug.getAffectedReleases().isEmpty()) {
				int startIndex = 0;
				int endIndex = 0;
				int i = 0;
				for (Release release : releases) {
						if(bug.getAffectedReleases().get(0).getId().equals(release.getId())) 
							startIndex = i;
						if(bug.getAffectedReleases().get(bug.getAffectedReleases().size()-1).getId().equals(release.getId())) 
							endIndex = i;
						i++;
						
				}
				correctreleases.addAll(releases.subList(startIndex, endIndex+1));
				correctreleases.stream().forEach(release->release.setAffected(Boolean.TRUE));
			}
			
			bug.setAffectedReleases(correctreleases);
		}
		
		
		
	}

}
