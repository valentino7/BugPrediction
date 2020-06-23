package secondelivery.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import secondelivery.calculator.CalculatorBugginess;
import secondelivery.calculator.CalculatorLifeCycle;
import secondelivery.calculator.CalculatorMetrics;
import secondelivery.entity.OutputMl;
import secondelivery.io.WriteDataset;
import secondelivery.io.WriteResultMl;
import secondelivery.ml.ModelActivity;
import weka.core.Instances;
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

	public static void createMetrics(String projectName, String[] directories, String[] urlsRepos, String proportionMethod) throws MyException  {
		Project project = new Project(projectName, urlsRepos);

		//prendo le versioni da jira
		List<Release> releases = ParserJira.getRelease(project.getName());	

		//prendo i bug con le relative versioni
		List<Bug> bugs = ParserJira.getProjectBug(project.getName());

		//correggi possibili errori se le affected version dei bug non sono continue
		correctPossibleErrorsAV(bugs, releases);
		setIndex(releases);
		project.setReleases(releases);

		//get repositories potrei avere piu repo per un progetto
		List<Git> repos = ParserJgit.getRepo(project.getUrlsRepo(), directories);

		//prendo le commit e le assegno ai bug
		CollectCommits collectCommits = ParserJgit.getCommitsDefaultBranch(project.getCollectBugs(), bugs, repos);
		project.setCollectCommits(collectCommits);


		//calcolo IV FV OV AV ed elimino i bug senza FV e senza OV oppure OV>FV
		CalculatorLifeCycle.calculateLifeCycle(project, proportionMethod);
		
		for(int i=0;i!= project.getReleases().size();i++) {
			if(project.getReleases().get(i).getAffected().equals(Boolean.TRUE))
				System.out.println(project.getReleases().get(i).getIndex());
		}

		Map<String,List<JavaFile>> hRelFile = CalculatorMetrics.startCalculator(repos, project.getReleases(), project.getCollectCommits(), project.getCollectBugs());

		CalculatorBugginess.startBugginess(project, hRelFile, repos);

		WriteDataset.writeCSVOnFile(proportionMethod, project.getName(), hRelFile);
		
		WriteDataset.writeBugPercent(proportionMethod, project.getName(), hRelFile);

		WriteDataset.writeBugIVOVAV(proportionMethod, project.getName(), project.getCollectBugs());
	}


	private static void setIndex(List<Release> releases) {
		for (Release release : releases) {
			release.setIndex(releases.indexOf(release));
		}
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


	public static void startModelActivity(String method, String output, String projectName) throws Exception {
		Instances dataset = ModelActivity.loadData(method, projectName);
		int numAttr = dataset.numAttributes();
		dataset.setClassIndex(numAttr - 1);
		List<OutputMl> listOutput = new ArrayList<>();
		ModelActivity.walkForward(dataset, listOutput);
		WriteResultMl.writeResultMlOnFile(method, listOutput, output);
	}


}
