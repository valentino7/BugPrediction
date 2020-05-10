package secondelivery.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.Git;
import common.entity.CollectCommits;
import common.entity.CommitEntity;
import common.entity.JavaFile;
import common.entity.Release;
import common.parser.ParserJgit;
import common.utils.CreatorDate;

public class CalculatorMetrics {

	private CalculatorMetrics() {}

	public static HashMap<String,List<JavaFile>> calculateMetrics(List<Git> repos, List<Release> releases, CollectCommits commits) {
		//per ogni release
		//per ogni commit fino a che non è maggiore della release successiva
		//prendi le classi nella commit e calcola metriche su size
		HashMap<String,List<JavaFile>> hRelFile = new HashMap<>();
		List<JavaFile> lClassesRelease = new ArrayList<>();

		//TODO riempire una lista per sapere quante classi vengono eliminate
		//		HashMap<String,List<JavaFile>> delRenClass = new HashMap<>();


		//		initializeListWithInitialCommits(lClassesNoRelease, commits , repos, releases);
		calculateMetrics(lClassesRelease, commits , repos, releases, hRelFile);

		// TODO CANCELLARE 50% COPPIE
		deleteReleasesClasses(hRelFile, releases);
		
		return hRelFile;
	}



	private static void deleteReleasesClasses(HashMap<String, List<JavaFile>> hRelFile, List<Release> releases ) {
		Integer halfIndex = releases.size()/2;

		for (Iterator<String> it = hRelFile.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			for (Release release : releases) {
				if(release.getName().equals(key) && releases.indexOf(release) > halfIndex) {
						it.remove();
				}		
			}
		}
	}

	//	private static void initializeListWithInitialCommits(List<JavaFile> lClassesRelease,List<CommitEntity> commits, List<Git> repos,  List<Release> releases) {
	//		for(int currentIndex = 0; currentIndex!=commits.size();currentIndex++) {
	//			//if release 0 e le commit stanno prima prendi tutte le classi e inizializza una lista di classi iniziali
	//			if(commits.get(currentIndex).getDate().compareTo(releases.get(0).getDate())<=0) {
	//				List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(repos,commits.get(currentIndex).getSha());	
	//				//fillLclasses(resultCommit,lClassesNoRelease);
	//			}
	//		}		
	//	}

	private static void calculateMetrics(List<JavaFile> lClassesRelease,CollectCommits commits, List<Git> repos,  List<Release> releases, HashMap<String,List<JavaFile>> hRelFile)  {
		int startIndex = 0;
		int currentIndex ;
		Iterator<Release> iterator = releases.iterator();
		while(iterator.hasNext()) {
			Release currentRelease = iterator.next();

			for( currentIndex = startIndex; currentIndex!=commits.getTotalCommits().size();currentIndex++) {

				//non perdo la commit perche inizializzo gli indici del ciclo della commit
				if(commits.getTotalCommits().get(currentIndex).getDate().compareTo(currentRelease.getDate())>0) {
					startIndex = currentIndex;

					//aggiorno l hashmap
					hRelFile.put(currentRelease.getName(), lClassesRelease);
					//azzero le metriche
					initializeMetrics(lClassesRelease);	
					break;
				}

				List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(repos,commits.getTotalCommits().get(currentIndex).getSha());	
				//setto la classe buggy se viene modificata e se la release è affetta
				fillLclasses(resultCommit,lClassesRelease,currentRelease, commitIsBuggy(commits.getTotalCommits().get(currentIndex),commits.getMyTicketCommits()));

			}

		}

	}

	private static Boolean commitIsBuggy(CommitEntity commitEntity,List<CommitEntity> commitsWithId) {
		return commitsWithId.contains(commitEntity);
	}

	private static void initializeMetrics(List<JavaFile> lClassesRelease) {
		
		lClassesRelease.forEach(file-> {
			file.getMetrics().setBuggy(Boolean.FALSE);

			file.getMetrics().setLoc(0);
			file.getMetrics().setLocTouched(0);
			//loc added
			file.getMetrics().setLocAdded(0);
			file.getMetrics().setMaxLocAdded(0);
			file.getMetrics().setAvgLocAdded(0f);
			file.getMetrics().getListLocAdded().clear();

			//churn
			file.getMetrics().setChurn(0);
			file.getMetrics().getListChurn().clear();
			file.getMetrics().setAvgLocAdded(0f);
			file.getMetrics().setMaxChurn(0);

			//change set size
			file.getMetrics().getListChgset().clear();
			file.getMetrics().setChgset(0);
			file.getMetrics().setMaxChgset(0);
			file.getMetrics().setAvgChgset(0f);

		});
	}

	private static void fillLclasses(List<JavaFile> resultCommit, List<JavaFile> lClassesRelease, Release release, Boolean commitIsBuggy) {
		for (JavaFile file : resultCommit) {

			switch (file.getStatus()) {
			case "DELETE": 
				deleteFileByPath(lClassesRelease,file.getFilename());
				break;
			case "RENAME":
				renameFileByPath(lClassesRelease,file);
				break;
			case "ADD":
				initializeMetrics(file);

				lClassesRelease.add(file);
				break;
			case "COPY":
				copyFileByPath(lClassesRelease,file);
				break;
			case "MODIFY":
				JavaFile oldFile = getFileByPath(lClassesRelease, file.getFilename());
				if(oldFile!=null)
					updateJavaFile(oldFile,file,release,commitIsBuggy);
				updateList(file,lClassesRelease);
				break;
			default:
				return;

			}
		}
		//		lClassesNoRelease.forEach(file-> System.out.println(file.getFilename()));
		//		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}



	private static boolean copyFileByPath(List<JavaFile> lClassesRelease, JavaFile file) {
		//assegno al nuovo file copiato tutte le metriche del file java precedente
		for (JavaFile javaFile : lClassesRelease) {
			if (javaFile.getFilename().equals(file.getOldPath())) {
				initializeMetrics(file);
				lClassesRelease.add(file);
				return true;
			}
		}
		return false;		
	}

	private static boolean renameFileByPath(List<JavaFile> lClassesRelease, JavaFile file) {
		//cambio filename al vecchio file ed incremento le statistiche?
		for (JavaFile javaFile : lClassesRelease) {
			if (javaFile.getFilename().equals(file.getOldPath())) {
				lClassesRelease.get(lClassesRelease.indexOf(javaFile)).setFilename(file.getFilename());
				return true;
			}
		}
		return false;
	}

	private static boolean deleteFileByPath(List<JavaFile> lClassesRelease, String filename) {
		ListIterator<JavaFile> listIterator = lClassesRelease.listIterator();
		while(listIterator.hasNext()){
			JavaFile javaFile = listIterator.next();
			if (javaFile.getFilename().equals(filename)) {
				listIterator.remove();
				return true;
			}
		}
		return false;
	}

	private static JavaFile getFileByPath(List<JavaFile> lClassesRelease, String filename) {
		//		System.out.println("\n\n\n\n\n------------------"+ filename);
		for (JavaFile javaFile : lClassesRelease) {
			if (javaFile.getFilename().equals(filename))
				return javaFile;
		}
		return null;
	}

	private static boolean updateList(JavaFile updatedFile, List<JavaFile> lClassesRelease) {
		ListIterator<JavaFile> listIterator = lClassesRelease.listIterator();
		while(listIterator.hasNext()){
			JavaFile javaFile = listIterator.next();
			if (javaFile.getFilename().equals(updatedFile.getFilename())) {
				listIterator.remove();
				listIterator.add(updatedFile);
				return true;
			}
		}
		return false;
	}

	private static void updateJavaFile(JavaFile oldFile, JavaFile newFile, Release release, Boolean commitIsBuggy) {
		newFile.getMetrics().setBuggy(release.getAffected().equals(Boolean.TRUE) && commitIsBuggy.equals(Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE );
		newFile.getMetrics().setLoc(oldFile.getMetrics().getLoc()-newFile.getNumDeletedLines()+newFile.getNumCreatedLines());
		newFile.getMetrics().setNfix(oldFile.getMetrics().getNfix()+ newFile.getMetrics().getNfix());
		newFile.getMetrics().setNr(oldFile.getMetrics().getNr()+ newFile.getMetrics().getNr());
		newFile.getMetrics().setLocTouched(oldFile.getMetrics().getLocTouched()+newFile.getNumCreatedLines()+newFile.getNumDeletedLines());
		//metriche loc added
		newFile.getMetrics().setLocAdded(oldFile.getMetrics().getLocAdded()+newFile.getNumCreatedLines());
		newFile.getMetrics().setMaxLocAdded(getMax(oldFile.getMetrics().getMaxLocAdded(),newFile.getMetrics().getLocAdded()));
		newFile.getMetrics().setListLocAdded(oldFile.getMetrics().getListLocAdded());
		newFile.getMetrics().getListLocAdded().add(newFile.getMetrics().getLocAdded());
		newFile.getMetrics().setAvgLocAdded(getAvg( newFile.getMetrics().getListLocAdded() ));

		//metriche churn
		newFile.getMetrics().setLocAdded(oldFile.getMetrics().getChurn()+newFile.getNumCreatedLines()-newFile.getNumDeletedLines());
		newFile.getMetrics().setMaxLocAdded(getMax(oldFile.getMetrics().getMaxLocAdded(),newFile.getMetrics().getChurn()));
		newFile.getMetrics().setListLocAdded(oldFile.getMetrics().getListChurn());
		newFile.getMetrics().getListLocAdded().add(newFile.getMetrics().getChurn());
		newFile.getMetrics().setAvgLocAdded(getAvg( newFile.getMetrics().getListChurn() ));

		//change set size
		newFile.getMetrics().setChgset(oldFile.getMetrics().getChgset()+ newFile.getMetrics().getInstantChgset());
		newFile.getMetrics().setChgset(getMax(oldFile.getMetrics().getMaxChgset(),newFile.getMetrics().getInstantChgset()));
		newFile.getMetrics().setListChgset(oldFile.getMetrics().getListChgset());
		newFile.getMetrics().getListChgset().add(newFile.getMetrics().getInstantChgset());
		newFile.getMetrics().setAvgChgset(getAvg(oldFile.getMetrics().getListChgset()));

		//age
		newFile.setCreationDate(oldFile.getCreationDate());
		System.out.println("inizio date: "+ newFile.getCreationDate()+ "fine date"+ newFile.getCommitDate());
		newFile.getMetrics().setAge(CreatorDate.getNumeWeek(newFile.getCreationDate(),newFile.getCommitDate()));
		newFile.getMetrics().setAgeDotLoc(oldFile.getMetrics().getAgeDotLoc() + newFile.getMetrics().getAge()*newFile.getMetrics().getLocAdded());
		newFile.getMetrics().setWeightedAge(newFile.getMetrics().getAgeDotLoc()/getSumLocAdded(newFile.getMetrics().getListLocAdded()));

		//num autori
		addIfNotExists(oldFile.getAuthors(),newFile.getAuthors());
		newFile.setAuthors(oldFile.getAuthors());
		newFile.getMetrics().setNumAuth(newFile.getAuthors().size());
	}


	private static void addIfNotExists(List<String> oldList, List<String> newList) {
		for (String element : newList) {
			if(!oldList.contains(element))
				oldList.add(element);
		}
	}

	private static Integer getSumLocAdded( List<Integer> listLocAdded) {
		return listLocAdded.stream().collect(Collectors.summingInt(Integer::intValue));

	}

	private static Integer getMax(Integer currentMax, Integer newValue) {
		if( currentMax > newValue)
			return currentMax;
		return newValue;
	}

	private static Float getAvg( List<Integer> listValues) {
		Float s = 0f;
		for (Integer value : listValues) {
			s = s + value.floatValue();
		}
		return s/listValues.size();
	}

	private static void initializeMetrics(JavaFile file) {
		//age
		file.setCreationDate(file.getCommitDate());
		file.getMetrics().setAge(0);
		file.getMetrics().setWeightedAge(0);

		file.getMetrics().setLoc(file.getNumCreatedLines());
		file.getMetrics().setLocTouched(file.getNumCreatedLines());
		//loc added
		file.getMetrics().setLocAdded(file.getNumCreatedLines());
		file.getMetrics().setMaxLocAdded(file.getNumCreatedLines());
		file.getMetrics().setAvgLocAdded(file.getNumCreatedLines().floatValue());
		file.getMetrics().getListLocAdded().add(file.getMetrics().getLocAdded());

		//churn
		file.getMetrics().setChurn(file.getNumCreatedLines());
		file.getMetrics().getListChurn().add(file.getMetrics().getChurn());
		file.getMetrics().setAvgLocAdded(file.getNumCreatedLines().floatValue());
		file.getMetrics().setMaxChurn(file.getNumCreatedLines());

		//change set size
		file.getMetrics().setChgset(file.getMetrics().getInstantChgset());
		file.getMetrics().getListChgset().add(file.getMetrics().getChgset());
		file.getMetrics().setMaxChgset(file.getMetrics().getInstantChgset());
		file.getMetrics().setAvgChgset(file.getMetrics().getInstantChgset().floatValue());

		//num aut
		file.getMetrics().setNumAuth(file.getAuthors().size());
	}
}
