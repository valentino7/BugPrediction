package secondelivery.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jgit.api.Git;
import common.entity.CommitEntity;
import common.entity.JavaFile;
import common.entity.Metrics;
import common.entity.Release;
import common.parser.ParserJgit;

public class CalculatorMetrics {

	private CalculatorMetrics() {}

	public static void calculateMetrics(List<Git> repos, List<Release> releases, List<CommitEntity> commits) {
		//per ogni release
		//per ogni commit fino a che non è maggiore della release successiva
		//prendi le classi nella commit e calcola metriche su size
		HashMap<String,List<JavaFile>> hRelFile = new HashMap<>();
		List<JavaFile> lClassesNoRelease = new ArrayList<>();
		
		//TODO riempire una lista per sapere quante classi vengono eliminate
		HashMap<String,List<JavaFile>> delRenClass = new HashMap<>();

		
//		initializeListWithInitialCommits(lClassesNoRelease, commits , repos, releases);
		calculateMetrics(lClassesNoRelease, commits , repos, releases, hRelFile);
		
		// TODO CANCELLARE 50% COPPIE
		deleteReleasesClasses(hRelFile);
	}

	
	
	private static void deleteReleasesClasses(HashMap<String, List<JavaFile>> hRelFile) {
		
	}

	private static void initializeListWithInitialCommits(List<JavaFile> lClassesNoRelease,List<CommitEntity> commits, List<Git> repos,  List<Release> releases) {
		for(int currentIndex = 0; currentIndex!=commits.size();currentIndex++) {
			//if release 0 e le commit stanno prima prendi tutte le classi e inizializza una lista di classi iniziali
			if(commits.get(currentIndex).getDate().compareTo(releases.get(0).getDate())<=0) {
				List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(repos,commits.get(currentIndex).getSha());	
				fillLclasses(resultCommit,lClassesNoRelease);
			}
		}		
	}

	private static void calculateMetrics(List<JavaFile> lClassesNoRelease,List<CommitEntity> commits, List<Git> repos,  List<Release> releases, HashMap<String,List<JavaFile>> hRelFile)  {
		int startIndex = 0;
		Iterator<Release> iterator = releases.iterator();
		
		int i=1;
		Release newRelease = releases.get(i);

		while(iterator.hasNext()) {
			Release currentRelease = iterator.next();

			for(int currentIndex = startIndex; currentIndex!=commits.size();currentIndex++) {

				//non perdo la commit perche inizializzo gli indici del ciclo della commit
				if(commits.get(currentIndex).getDate().compareTo(newRelease.getDate())>0 && iterator.hasNext()) {
					startIndex = currentIndex;
					//aggiorno l hashmap
					hRelFile.put(currentRelease.getName(), lClassesNoRelease);
					//azzero le metriche
					initializeMetrics(lClassesNoRelease);	
					break;
				}
				
				
				//calcolo metriche se la commit sta dopo la current release
				if(commits.get(currentIndex).getDate().compareTo(currentRelease.getDate())>0) {
					List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(repos,commits.get(currentIndex).getSha());	
					//setto la classe buggy se viene modificata e se la release è affetta
					fillLclasses(resultCommit,lClassesNoRelease,currentRelease);
				}
				
				
				if(currentIndex==commits.size()-1)
					hRelFile.put(currentRelease.getName(), lClassesNoRelease);
				
			
			}
			i++;
			if(i<releases.size())
				newRelease = releases.get(i);
		}
		
	}

	private static void initializeMetrics(List<JavaFile> lClassesNoRelease) {
		lClassesNoRelease.forEach(file-> file.getMetrics().setBuggy(Boolean.FALSE));
	}

	private static void fillLclasses(List<JavaFile> resultCommit, List<JavaFile> lClassesNoRelease, Release release) {
		for (JavaFile file : resultCommit) {
			
			switch (file.getStatus()) {
			case "DELETE": 
				deleteFileByPath(lClassesNoRelease,file.getFilename());
				break;
			case "RENAME":
				renameFileByPath(lClassesNoRelease,file);
				break;
			case "ADD":
				file.getMetrics().setLoc(file.getNumCreatedLines());
				lClassesNoRelease.add(file);
				break;
			case "COPY":
				copyFileByPath(lClassesNoRelease,file);
				break;
			case "MODIFY":
				JavaFile oldFile = getFileByPath(lClassesNoRelease, file.getFilename());
				if(oldFile!=null)
					updateJavaFile(oldFile,file,release);
					updateList(file,lClassesNoRelease);
				break;
			default:
				return;
	
			}
		}
		lClassesNoRelease.forEach(file-> System.out.println(file.getFilename()));
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}



	private static boolean copyFileByPath(List<JavaFile> lClassesNoRelease, JavaFile file) {
		//assegno al nuovo file copiato tutte le metriche del file java precedente
		for (JavaFile javaFile : lClassesNoRelease) {
			if (javaFile.getFilename().equals(file.getOldPath())) {
				file.setMetrics(javaFile.getMetrics());
				lClassesNoRelease.add(file);
				return true;
			}
		}
		return false;		
	}

	private static boolean renameFileByPath(List<JavaFile> lClassesNoRelease, JavaFile file) {
		//cambio filename al vecchio file ed incremento le statistiche?
		for (JavaFile javaFile : lClassesNoRelease) {
			if (javaFile.getFilename().equals(file.getOldPath())) {
				lClassesNoRelease.get(lClassesNoRelease.indexOf(javaFile)).setFilename(file.getFilename());
				return true;
			}
		}
		return false;
	}

	private static boolean deleteFileByPath(List<JavaFile> lClassesNoRelease, String filename) {
		ListIterator<JavaFile> listIterator = lClassesNoRelease.listIterator();
		while(listIterator.hasNext()){
			JavaFile javaFile = listIterator.next();
			if (javaFile.getFilename().equals(filename)) {
				listIterator.remove();
				return true;
			}
		}
		return false;
	}

	private static JavaFile getFileByPath(List<JavaFile> lClassesNoRelease, String filename) {
		System.out.println("\n\n\n\n\n------------------"+ filename);
		for (JavaFile javaFile : lClassesNoRelease) {
			if (javaFile.getFilename().equals(filename))
				return javaFile;
		}
		return null;
	}

	private static boolean updateList(JavaFile updatedFile, List<JavaFile> lClassesNoRelease) {
		ListIterator<JavaFile> listIterator = lClassesNoRelease.listIterator();
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

	private static void updateJavaFile(JavaFile oldFile, JavaFile newFile, Release release) {
		newFile.getMetrics().setBuggy(release.getAffected());
		newFile.getMetrics().setLoc(oldFile.getMetrics().getLoc()-newFile.getNumDeletedLines()+newFile.getNumCreatedLines());
		newFile.getMetrics().setNfix(oldFile.getMetrics().getNfix()+ newFile.getMetrics().getNfix());
		newFile.getMetrics().setNr(oldFile.getMetrics().getNr()+ newFile.getMetrics().getNr());
	}

}
