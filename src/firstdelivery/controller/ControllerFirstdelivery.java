package firstdelivery.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import common.entity.Bug;
import common.entity.CollectCommits;
import common.entity.Project;
import common.parser.ParserGithub;
import common.parser.ParserJira;
import common.utils.CreatorDate;
import firstdelivery.entity.OutputFields;
import firstdelivery.io.ManageFile;
import firstdelivery.utils.ListComparator;
import firstdelivery.utils.StringsFirstDelivery;

public class ControllerFirstdelivery {

	private ControllerFirstdelivery() {
	}


	public static void startDelivery( String[] nameRepos)  {
		Project project = new Project(StringsFirstDelivery.PROJ_NAME, nameRepos);

		//Get bugsX
		List<Bug> bugs = ParserJira.getProjectBug(project.getName());

		StringBuilder outputFile = new StringBuilder();
		for(int i=0; i!=4; i++) {
			if(i%2==0) {
				outputFile.append("Parquet");
				project.setUrlsRepo(new String[] { nameRepos[0] });
			}else {
				outputFile.append("3Project"); 
				project.setUrlsRepo(nameRepos);
			}
				


			CollectCommits collectCommits = null;
			if(i>1) {
				//all branch
				outputFile.append("allBranch.csv");
				collectCommits = ParserGithub.getCommitsByAllBranches(project.getCollectBugs(),bugs,project.getUrlsRepo());
			}else {
				outputFile.append("oneBranch.csv");
				collectCommits = ParserGithub.getCommitsDefaultBranch(project.getCollectBugs(),bugs,project.getUrlsRepo());
			}
			
			project.setCollectCommits(collectCommits);
			List<Bug> bugsWithoutCommits = getBugsWithoutCommit(project.getCollectBugs().getBugsWithCommits(),bugs);
			project.getCollectBugs().setBugsWithoutCommits(bugsWithoutCommits);

			
			//Create ArrayList of Date,NumTicket in 1 Month
			//intervallo finestra di studio, inizio data primo bug, fine data ultima commit
			LocalDateTime beginDate = project.getCollectBugs().getBugsWithCommits().get(0).getOpenDate();
			LocalDateTime endDate = project.getCollectCommits().getMyTicketCommits().get(project.getCollectCommits().getMyTicketCommits().size()-1).getDate();
			ArrayList<OutputFields> listDateNum = (ArrayList<OutputFields>)sumTicket(project.getCollectBugs().getBugsWithCommits(),beginDate,endDate);

			//Write on csv
			ManageFile.writeCSVOnFile(StringsFirstDelivery.OUTPUTFILE+outputFile, listDateNum, project.getCollectCommits().getMyTicketCommits().size(), project.getCollectCommits().getNoTicketCommits().size(),
					project.getCollectCommits().getOtherIdCommits().size(), project.getCollectBugs().getBugsWithCommits().size(), project.getCollectBugs().getBugsWithoutCommits().size());  
		
			outputFile = new StringBuilder();
		}
	}


	private static List<Bug> getBugsWithoutCommit(List<Bug> bugWithCommit,List<Bug> totalBugs) {
		List<Bug> bugsWithoutCommit = new ArrayList<>();
		for (Bug bug : totalBugs) {
			if(!bugWithCommit.contains(bug)) {
				bugsWithoutCommit.add(bug);
			}
		}
		return bugsWithoutCommit;
	}


	private static void initializeList(List<OutputFields> listDateNum,LocalDateTime beginDate,LocalDateTime endDate) {
		//i mesi iniziano da 1
		Integer beginMonth = beginDate.getMonthValue();
		for(Integer i = beginDate.getYear(); i<= endDate.getYear() ;i++) {
			if(!i.equals(beginDate.getYear())) 
				beginMonth=1;
			for(Integer month = beginMonth ; month!= 13 ;month++) {
				listDateNum.add(new OutputFields(CreatorDate.getLocalDateYearMonth(i,month),0 ));	
				if(i.equals(endDate.getYear()) && month.equals(endDate.getMonthValue()))
					break;
			}
		}
	}


	private static void incrementCount(List<OutputFields> listDateNum,Bug bug,ListComparator comparator ) {
		for(int i = 0 ; i!= listDateNum.size();i++) {
			//se Date,NumTickets esiste allora incrementa il contatore mensile
			int commitSize = bug.getCommits().size();
			if(comparator.compare(listDateNum.get(i).getDate(),bug.getCommits().get(commitSize-1).getDate())==0) {
				listDateNum.get(i).setCount(listDateNum.get(i).getCount()+1);
				return;
			}			      
		}
	}


	private static List<OutputFields> sumTicket(List<Bug> bugs, LocalDateTime beginDate,LocalDateTime endDate){
		ArrayList<OutputFields> listDateNum = new ArrayList<>();
		ListComparator comparator =  new ListComparator();
		//inizializza valori
		initializeList(listDateNum,beginDate,endDate);
		for(Bug bug : bugs) {
			incrementCount(listDateNum,bug,comparator);
		}
		return listDateNum; 
	}


}
