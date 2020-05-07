package secondelivery.controller;

import java.util.List;
import secondelivery.calculator.CalculatorLifeCycle;
import secondelivery.calculator.CalculatorMetrics;
import secondelivery.strings.StringsSecondDelivery;
import common.entity.Bug;
import common.entity.CollectCommits;
import common.entity.Project;
import common.entity.Release;
import common.exceptions.MyException;
import common.parser.ParserJgit;
import org.eclipse.jgit.api.Git;
import common.parser.ParserJira;




public class ControllerSecondelivery {


	private ControllerSecondelivery() {
	}


	public static void startDelivery(String[] directories, String[] urlsRepos) throws MyException  {
		Project project = new Project(StringsSecondDelivery.PROJECT_NAME,urlsRepos);

		//prendo le versioni da jira
		List<Release> releases = ParserJira.getRelease(project.getName());	
		project.setReleases(releases);

		//prendo i bug con le relative versioni
		List<Bug> bugs = ParserJira.getProjectBug(project.getName());

		//get repositories potrei avere piu repo per un progetto
		List<Git> repos = ParserJgit.getRepo(project.getUrlsRepo(),directories);
		
		//prendo le commit e le assegno ai bug
		CollectCommits collectCommits = ParserJgit.getCommitsDefaultBranch(project.getCollectBugs(),bugs,repos);
		project.setCollectCommits(collectCommits);

		//calcolo le classi buggy
		CalculatorLifeCycle.calculateLifeCycle(project,repos);
		
		CalculatorMetrics.calculateMetrics(repos, project.getReleases(),project.getCollectCommits().getTotalCommits());

	}

}
