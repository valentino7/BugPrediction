package firstdelivery.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.entity.Bug;
import common.entity.CollectCommits;
import common.entity.Project;
import common.parser.ParserGithubRest;
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
		Project project = new Project(StringsFirstDelivery.PROJ_NAME,nameRepos);

		//Get bugsX
		List<Bug> bugs = ParserJira.getProjectBug(project.getName());

		//all branch
		//		CollectCommits collectCommits = ParserGithub.getCommitsAllBranches(project.getBugs(),bugs,project.getRepo());
		CollectCommits collectCommits = ParserGithubRest.getCommitsDefaultBranch(project.getCollectBugs(),bugs,project.getRepo());
		project.setCollectCommits(collectCommits);
		List<Bug> bugsWithoutCommits = getBugsWithoutCommit(project.getCollectBugs().getBugsWithCommits(),bugs);
		project.getCollectBugs().setBugsWithoutCommits(bugsWithoutCommits);

		System.out.println("Size PARQUET commits: "+Integer.toString(project.getCollectCommits().getMyTicketCommits().size()));
		System.out.println("Size WITHOUT TICKET commits: "+Integer.toString(project.getCollectCommits().getNoTicketCommits().size()));
		System.out.println("Size OTHER TICKET commits: "+Integer.toString(project.getCollectCommits().getOtherIdCommits().size()));


		System.out.println("Size bugs with commit : "+Integer.toString( project.getCollectBugs().getBugsWithCommits().size()));
		System.out.println("Size bugs without commit: "+Integer.toString( project.getCollectBugs().getBugsWithoutCommits().size()));


		//verifico quali bug non sono nel branch di default
		//		searchCommitInDifferentBranch(project, nameRepos);

		//Create ArrayList of Date,NumTicket in 1 Month
		//intervallo finestra di studio, inizio data primo bug, fine data ultima commit
		LocalDateTime beginDate = project.getCollectBugs().getBugsWithCommits().get(0).getOpenDate();
		LocalDateTime endDate = project.getCollectCommits().getMyTicketCommits().get(project.getCollectCommits().getMyTicketCommits().size()-1).getDate();
		ArrayList<OutputFields> listDateNum = (ArrayList<OutputFields>)sumTicket(project.getCollectBugs().getBugsWithCommits(),beginDate,endDate);
		System.out.println("Numero mesi " + listDateNum.size());



		//numero ticket totali
		Integer sum=listDateNum.stream().mapToInt(i -> i.getCount()).sum();
		System.out.println("somma dei ticket presenti in ogni mese: "+sum);

		//Write on csv
		ManageFile.writeCSVOnFile(listDateNum);  

	}



	private static void searchCommitInDifferentBranch(Project projectDefaultBranch, String[] repos) throws IOException {
		Project projectAllBranches = new Project(StringsFirstDelivery.PROJ_NAME,repos);
		List<Bug> b = ParserJira.getProjectBug(projectAllBranches.getName());
		CollectCommits c = ParserGithubRest.getCommitsByAllBranches(projectAllBranches.getCollectBugs(),b,projectAllBranches.getRepo());
		projectAllBranches.setCollectCommits(c);
		List<Bug> bugsWithoutCommits = getBugsWithoutCommit(projectAllBranches.getCollectBugs().getBugsWithCommits(),b);
		projectAllBranches.getCollectBugs().setBugsWithoutCommits(bugsWithoutCommits);


		System.out.println("Size PARQUET commits: "+Integer.toString(projectAllBranches.getCollectCommits().getMyTicketCommits().size()));
		System.out.println("Size WITHOUT TICKET commits: "+Integer.toString(projectAllBranches.getCollectCommits().getNoTicketCommits().size()));
		System.out.println("Size OTHER TICKET commits: "+Integer.toString(projectAllBranches.getCollectCommits().getOtherIdCommits().size()));


		System.out.println("Size bugs with commit : "+Integer.toString( projectAllBranches.getCollectBugs().getBugsWithCommits().size()));
		System.out.println("Size bugs without commit: "+Integer.toString( projectAllBranches.getCollectBugs().getBugsWithoutCommits().size()));


		//bug che sono negli altri branch e non in quello di default
		boolean present;
		for (Bug bugAll : projectAllBranches.getCollectBugs().getBugsWithCommits()) {
			present=false;
			for (Bug bugDefault : projectDefaultBranch.getCollectBugs().getBugsWithCommits()) {
				if(bugDefault.getId().equals(bugAll.getId())) {
					present=true;
					break;
				}
			}
			if(!present)
				System.out.println(bugAll.getId());
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
		//Integer beginMonth=StringsFirstDelivery.BEGIN_MONTH;
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









	//	//rimosso
	//	public static void removeDuplicateElement(List<String> lBugs) {
	//		lBugs.stream().distinct().collect(Collectors.toList());
	//	}
	//	//rimosso
	//	public static void filterFile(List<String> lCommits) {
	//		lCommits.removeIf((String n) -> (n==null || !n.contains(StringsFirstDelivery.KEY)   )   ); 
	//	}

	//	 public static void configureLogger() {
	//		 MyLog4J.setProperties();
	//	 }
}
