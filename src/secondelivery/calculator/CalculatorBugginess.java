package secondelivery.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import common.entity.Bug;
import common.entity.CommitEntity;
import common.entity.JavaFile;
import common.entity.Project;
import common.entity.Release;
import common.parser.ParserJgit;

public class CalculatorBugginess {
	
	private CalculatorBugginess() {}
	
	public static void startBugginess(Project project, Map<String, List<JavaFile>> hRelFile, List<Git> repos) {
		//prendo le commit con bug e prendo i file che quelle commit toccano, tutti i file fino all'injected version sono buggy
		//per ogni commit prendo il relativo bug per cercare injected version
		for (Bug bug : project.getCollectBugs().getBugsWithCommits()) {
			for (CommitEntity commit : bug.getCommits()) {			
				List<JavaFile> resultCommit = ParserJgit.getChangesListsByCommit(project.getCollectBugs(), repos, commit.getSha());	

				for (JavaFile file : resultCommit) {
					if(file.getStatus().equals("MODIFY")){
						setBugginess(hRelFile, file, project.getReleases(), bug.getInjectedRelease(), bug.getFixedRelease());
					}
				}
			}
		}
	}

	private static void setBugginess(Map<String, List<JavaFile>> hRelFile, JavaFile file, List<Release> lReleases, Release iv, Release fv) {
		int keyIV = lReleases.indexOf(iv);
		int keyFV = lReleases.indexOf(fv);
		List<String> oldFiles = new ArrayList<>();

		if(!hRelFile.containsKey(String.valueOf(keyFV)))
			keyFV=getMaxKey(hRelFile);

		if(hRelFile.containsKey(String.valueOf(keyIV))) {
			iterateOnAffectedVersion(oldFiles, hRelFile, file, keyIV, keyFV);
			
		}
	}

	private static void iterateOnAffectedVersion(List<String> oldFiles, Map<String, List<JavaFile>> hRelFile, JavaFile file, 
		int keyIV, int keyFV) {
		
		for(int i = keyIV; i!=keyFV+1; i++) {
			if(i==20 || i==21 || i==22)
				System.out.println("i "+ i +" "+ file.getFilename());
			//keyFV puo essere maggiore del 50% delle release
			iterateOnFilesInRelease(oldFiles, hRelFile, file, keyIV, keyFV, i);
		}		
	}

	private static void iterateOnFilesInRelease(List<String> oldFiles, Map<String, List<JavaFile>> hRelFile, JavaFile file, 
			int keyIV, int keyFV, int i) {

		for (JavaFile javaFile : hRelFile.get(String.valueOf(i))) {
			if(javaFile.getFilename().equals(file.getFilename()) ) {
				javaFile.getMetrics().setBuggy(Boolean.TRUE);
				//check if not in list
				if(javaFile.getOldPath()!=null ) {
					String oldPath = javaFile.getOldPath();
					oldFiles.add(oldPath);
					do {
						markOldFile(oldPath, oldFiles, keyIV, keyFV, hRelFile);
						oldFiles.remove(oldPath);
						if(!oldFiles.isEmpty())
							oldPath = oldFiles.get(0);
					}
					while(!oldFiles.isEmpty());
				}
			}			
		}		
	}

	
	//GESTISCO I CASI DI RENAME, vado a cercare i file che hanno sia il nome vecchio che il nuovo
	private static void markOldFile(String oldPath, List<String> oldFiles, int keyIV, int keyFV, Map<String, List<JavaFile>> hRelFile) {
		//ITERATIVAMENTE CERCA I VECCHI NOMI E METTILI TRUE

		for(int j = keyIV; j!=keyFV+1; j++) {
			for (JavaFile old : hRelFile.get(String.valueOf(j))) {
				if(old.getFilename().equals(oldPath) ) {
					old.getMetrics().setBuggy(Boolean.TRUE);

					if(old.getOldPath()!=null && !oldFiles.contains(old.getOldPath())) {
						oldFiles.add(old.getOldPath());
					}
				}

			}
		}
	}
	
	
	
	private static int getMaxKey(Map<String, List<JavaFile>> hRelFile) {
		int max = 0;
		for (String key : hRelFile.keySet()) {
			if(Integer.valueOf(key)>max)
				max = Integer.valueOf(key);
		}
		return max;
	}

}
