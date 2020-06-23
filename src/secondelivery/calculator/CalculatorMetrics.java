package secondelivery.calculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.Git;
import common.entity.CollectBugs;
import common.entity.CollectCommits;
import common.entity.JavaFile;
import common.entity.Metrics;
import common.entity.Release;
import common.parser.ParserJgit;
import common.utils.CreatorDate;

public class CalculatorMetrics {

	private CalculatorMetrics() {}

	public static Map<String,List<JavaFile>> startCalculator(List<Git> repos, List<Release> releases, CollectCommits commits, CollectBugs collectBugs) {
		//per ogni release
		//per ogni commit fino a che non è maggiore della release successiva
		//prendi le classi nella commit e calcola metriche su size
		LinkedHashMap<String,List<JavaFile>> hRelFile = new LinkedHashMap<>();
		List<JavaFile> lClassesRelease = new ArrayList<>();
		
		calculateMetrics(lClassesRelease, commits , repos, releases, hRelFile, collectBugs);

		deleteReleasesClasses(hRelFile, releases);

		return hRelFile;
	}



	private static void deleteReleasesClasses(LinkedHashMap<String, List<JavaFile>> hRelFile, List<Release> releases ) {
		//parte intera inferiore
		Integer halfIndex = releases.size()/2;
		int size = hRelFile.size();
		for (int i=0; i!= size ; i++) {
			if(i > halfIndex)
				hRelFile.remove(String.valueOf(i));
		}

	}



	private static void calculateMetrics(List<JavaFile> lClassesRelease,CollectCommits commits, List<Git> repos,  List<Release> releases, LinkedHashMap<String,List<JavaFile>> hRelFile, CollectBugs collectBugs)  {
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
					hRelFile.put(String.valueOf(releases.indexOf(currentRelease)), lClassesRelease);
					//azzero le metriche
					lClassesRelease = resetList(lClassesRelease);	
					break;
				}
				
				List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(collectBugs, repos, commits.getTotalCommits().get(currentIndex).getSha());	
				fillLclasses(resultCommit,lClassesRelease);
			}

		}

	}

	private static List<JavaFile> resetList(List<JavaFile> lClassesRelease) {
		List<JavaFile> clonedList = new ArrayList<>();
		for (JavaFile javaFile : lClassesRelease) {
			Metrics metrics = new Metrics(0, 0);
			//metriche incrementali
			metrics.setLoc(javaFile.getMetrics().getLoc());
			metrics.setAgeDotLoc(javaFile.getMetrics().getAgeDotLoc());
			metrics.setNumAuth(0);
			metrics.setAge(javaFile.getMetrics().getAge());
			metrics.setAgeDotLoc(0);
			JavaFile file = new JavaFile(javaFile.getFilename(), metrics, javaFile.getNumCreatedLines(), javaFile.getNumDeletedLines(),
					javaFile.getStatus(), javaFile.getCommitDate(),null);
			file.setAuthors(javaFile.getAuthors());
			
			file.setCreationDate(javaFile.getCreationDate());
			clonedList.add(file);

		}
		return clonedList;
	}

	private static void fillLclasses(List<JavaFile> resultCommit, List<JavaFile> lClassesRelease) {
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
				copyFileByPath(lClassesRelease, file);
				break;
			case "MODIFY":
				JavaFile oldFile = getFileByPath(lClassesRelease, file.getFilename());
				if(oldFile!=null)
					updateJavaFile(oldFile, file);
				updateList(file, lClassesRelease);
				break;
			default:
				return;

			}
		}
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
				lClassesRelease.get(lClassesRelease.indexOf(javaFile)).setOldPath(javaFile.getFilename());
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

	private static void updateJavaFile(JavaFile oldFile, JavaFile newFile) {
	
		newFile.setOldPath(oldFile.getOldPath());
		
		newFile.getMetrics().setLoc(oldFile.getMetrics().getLoc()-newFile.getNumDeletedLines()+newFile.getNumCreatedLines());
		newFile.getMetrics().setNfix(oldFile.getMetrics().getNfix()+ newFile.getMetrics().getNfix());
		newFile.getMetrics().setNr(oldFile.getMetrics().getNr()+ 1);
		newFile.getMetrics().setLocTouched(oldFile.getMetrics().getLocTouched()+newFile.getNumCreatedLines()+newFile.getNumDeletedLines());
		//metriche loc added
		newFile.getMetrics().setLocAdded(oldFile.getMetrics().getLocAdded()+newFile.getNumCreatedLines());
		newFile.getMetrics().setMaxLocAdded(getMax(oldFile.getMetrics().getMaxLocAdded(),newFile.getMetrics().getLocAdded()));
		newFile.getMetrics().setListLocAdded(oldFile.getMetrics().getListLocAdded());
		newFile.getMetrics().getListLocAdded().add(newFile.getMetrics().getLocAdded());
		newFile.getMetrics().setAvgLocAdded(getAvg( newFile.getMetrics().getListLocAdded() ));

		//metriche churn
		newFile.getMetrics().setChurn(oldFile.getMetrics().getChurn()+newFile.getNumCreatedLines()-newFile.getNumDeletedLines());
		newFile.getMetrics().setMaxChurn(getMax(oldFile.getMetrics().getMaxLocAdded(),newFile.getMetrics().getChurn()));
		newFile.getMetrics().setListChurn(oldFile.getMetrics().getListChurn());
		newFile.getMetrics().getListChurn().add(newFile.getMetrics().getChurn());
		newFile.getMetrics().setAvgChurn(getAvg( newFile.getMetrics().getListChurn() ));

		//change set size
		newFile.getMetrics().setChgset(oldFile.getMetrics().getChgset()+ newFile.getMetrics().getInstantChgset());
		newFile.getMetrics().setMaxChgset(getMax(oldFile.getMetrics().getMaxChgset(),newFile.getMetrics().getInstantChgset()));
		newFile.getMetrics().setListChgset(oldFile.getMetrics().getListChgset());
		newFile.getMetrics().getListChgset().add(newFile.getMetrics().getInstantChgset());
		newFile.getMetrics().setAvgChgset(getAvg(oldFile.getMetrics().getListChgset()));

		//age
		newFile.setCreationDate(oldFile.getCreationDate());
		newFile.getMetrics().setAge(CreatorDate.getNumeWeek(newFile.getCreationDate(),newFile.getCommitDate()));
		newFile.getMetrics().setAgeDotLoc(oldFile.getMetrics().getAgeDotLoc() + newFile.getMetrics().getAge()*newFile.getMetrics().getLocAdded());
		newFile.getMetrics().setWeightedAge(newFile.getMetrics().getAgeDotLoc().floatValue()/getSumLocAdded(newFile.getMetrics().getListLocAdded()));

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
		Integer sum = listLocAdded.stream().collect(Collectors.summingInt(Integer::intValue));
		if (sum==0) {
			return 1;
		}
		return sum;
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
		file.getMetrics().setNr(file.getMetrics().getNr()+1);
		//age
		file.setCreationDate(file.getCommitDate());
		file.getMetrics().setAge(0);
		file.getMetrics().setAgeDotLoc(0);
		file.getMetrics().setWeightedAge(0f);

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
