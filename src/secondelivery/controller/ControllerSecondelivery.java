package secondelivery.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import secondelivery.calculator.CalculatorLifeCycle;
import secondelivery.calculator.CalculatorMetrics;
import secondelivery.entity.OutputMl;
import secondelivery.io.WriteDataset;
import secondelivery.io.WriteResultMl;
import secondelivery.ml.ModelActivity;
import secondelivery.strings.StringsSecondDelivery;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
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

		Map<String,List<JavaFile>> hRelFile = CalculatorMetrics.calculateMetrics(repos, project.getReleases(),project.getCollectCommits());

		WriteDataset.writeCSVOnFile(project.getName(), hRelFile);

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


	public static void startModelActivity() throws Exception {
		//		// save ARFF
		//		ArffSaver saver = new ArffSaver();
		//		saver.setInstances(data);//set the dataset we want to convert
		//		//and save as ARFF
		//		saver.setFile(new File("prova.arff"));
		//		saver.writeBatch();

		Instances dataset = ModelActivity.loadData();

		int numAttr = dataset.numAttributes();
		dataset.setClassIndex(numAttr - 1);
		//		testSet.setClassIndex(numAttr - 1);

		List<OutputMl> listOutput = new ArrayList<>();

		ModelActivity.walkForward(dataset, listOutput);
		WriteResultMl.writeResultMlOnFile(listOutput);
	}


}
