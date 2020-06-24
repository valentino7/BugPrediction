package common.parser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import common.entity.Bug;
import common.entity.CollectBugs;
import common.entity.CollectCommits;
import common.entity.CommitEntity;
import common.entity.JavaFile;
import common.entity.Metrics;
import common.io.ManageDirectory;
import common.strings.Strings;
import common.utils.CreatorDate;
import common.utils.XSorter;

public class ParserJgit {
	private ParserJgit() {}



	public static List<Git> getRepo(String[] urlsRepos, String[] directories) {
		int i=0;
		List<Git> gits = new ArrayList<>(); 
		for (String repo : urlsRepos) {
			try {
				ManageDirectory.deleteDirectory(new File("repos/"+directories[i]));
			} catch (IOException e1) {
				Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e1.toString(), e1);
			}

			try {
				gits.add(Git.cloneRepository()
						.setURI(repo)
						.setDirectory(new File("repos/"+directories[i]))
						.call());
			} catch (GitAPIException e) {
				Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
			}
			i++;
		}
		return gits;

	}
	public static CollectCommits getCommitsDefaultBranch(CollectBugs collectBugs, List<Bug> bugs, List<Git> gits) {
		CollectCommits collectCommits = new CollectCommits();

		for (Git git : gits) {

			RevWalk revWalk = new RevWalk( git.getRepository() );
			//tutti i figli prima del padre
			revWalk.sort( RevSort.TOPO );

			RevCommit head = null;
			try (RevWalk walk = new RevWalk(git.getRepository())) {
				try {
					head = walk.parseCommit(git.getRepository().exactRef("HEAD" ).getObjectId());
				} catch (IOException e) {
					Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
					return null;
				}
				while (head.getParentCount() != 0) {
					try {
						head = walk.parseCommit(head.getParent(0));
						fillCommitList(head,bugs,collectBugs,collectCommits);

					} catch (IOException e) {
						Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
					}
				}
			}
			revWalk.close();

		}

	
		//serve riscorrere i bug infine per riempire i bug senza commit 
		fillBugWithoutCommit(bugs,collectBugs);

		//ordino tutte le liste
		XSorter.orderCommitsAndBugsLists(collectCommits,collectBugs);
		
		return collectCommits;

	}



	private static void fillCommitList(RevCommit commit , List<Bug> bugs, CollectBugs collectBugs, CollectCommits collectCommits) {

		boolean isBugInCommit = false;
		//creo classe commit
		String message = commit.getFullMessage();
		String sha = commit.getName();
		LocalDateTime date = CreatorDate.getLocalDateTimeByDate(commit.getCommitterIdent().getWhen());
		CommitEntity commitEntity = new CommitEntity(sha, message, date);
		for (Bug bug : bugs) {
			if(Pattern.compile(bug.getId()+"\\D"+"|"+bug.getId()+"\\b").matcher(message).find()) {
				isBugInCommit = true;
				addCommitIfNotExists(collectCommits.getMyTicketCommits(), commitEntity);
				bug.getCommits().add(commitEntity);
				addBugIfNotExists(collectBugs,bug);
			}	
		}
		//se la commit non e stata aggiunta a nessun bug e matcha con un altro Ticket di un altro progetto, aggiungo nella seconda lista
		if(!isBugInCommit && Pattern.compile(Strings.REGEX_OTHER_ID).matcher(message).find())
			collectCommits.getOtherIdCommits().add(commitEntity);
		else if (!isBugInCommit)
			collectCommits.getNoTicketCommits().add(commitEntity);

		collectCommits.getTotalCommits().add(commitEntity);
		
	}


	private static void addCommitIfNotExists(List<CommitEntity> myTicketCommits, CommitEntity commit) {
		if(myTicketCommits.contains(commit))
			return;
		myTicketCommits.add(commit);
	}


	private static void addBugIfNotExists(CollectBugs bugs, Bug addBug) {
		if(bugs.getBugsWithCommits().contains(addBug)) {
			bugs.getBugsWithCommits().remove(addBug);
		}
		bugs.getBugsWithCommits().add(addBug);
	}

	private static void fillBugWithoutCommit(List<Bug> bugs, CollectBugs collectBugs) {
		for (Bug bug : bugs) {
			if(!collectBugs.getBugsWithCommits().contains(bug))
				collectBugs.getBugsWithoutCommits().add(bug);			
		}
	}







	public static List<JavaFile> getChangesListsByCommit(CollectBugs collectBugs, List<Git> repos, String sha){
		List<JavaFile> lClasses = new ArrayList<>();
		Repository repository = repos.get(0).getRepository();
		RevWalk rw = new RevWalk(repository);
		ObjectId head = null;
		try {
			head = repository.resolve(sha);
		}catch( RevisionSyntaxException | IOException e) {
			Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
		}
		//prendo la commit attuale
		RevCommit commit = null;
		try {
			commit = rw.parseCommit(head);
		}catch(IOException e){
			Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
			rw.close();
			return lClasses;
		}
		//prendo la commit del padre
		RevCommit parent = null;
		try {
			if(commit.getParentCount()!=0) {
				parent = rw.parseCommit(commit.getParent(0).getId());
			}
		}catch(IOException e){
			Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
			rw.close();
			return lClasses;
		}
		DiffFormatter df = getDiffFormatter(repository);
		List<DiffEntry> diffs = null;

		//creo diff
		try {
			if(parent == null) {
				diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(),commit.getTree()));
			}else {
				diffs = df.scan(parent.getTree(), commit.getTree());
			}
		}catch(IOException e){
			Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
		}
		//riempio la lista con le classi
		fillListWithJavaClasses(df, lClasses, diffs, commit, collectBugs);
		df.close();
		rw.close();
		return lClasses;
	}

	private static void fillListWithJavaClasses(DiffFormatter df, List<JavaFile> lClasses, List<DiffEntry> diffs, RevCommit commit, CollectBugs collectBugs) {
		Integer instantChangeSet = getInstantChgset(diffs);
		for (DiffEntry diff : diffs) {
			String path = null;
			//se il type e delete prendo il path della commit padre
			if(diff.getChangeType().name().equals("DELETE")) 
				path = diff.getOldPath();
			else
				path = diff.getNewPath();
			
			//processo solo i file java con un espressione regolare
			if(!Pattern.compile(Strings.REGEX_TREE_JAVA).matcher(path).find())
				continue;

			JavaFile javaFile = getJavaFile(path, diff, commit, df, instantChangeSet, commit.getAuthorIdent().getName(), collectBugs);
			lClasses.add(javaFile);

		}
	}

	private static Integer getInstantChgset(List<DiffEntry> diffs) {
		Integer chgset=0;
		for (DiffEntry diff : diffs) {
			if(diff.getChangeType().name().equals("ADD") || diff.getChangeType().name().equals("MODIFY")) 
				chgset++;
		}
		return chgset;
	}



	private static JavaFile getJavaFile(String path, DiffEntry diff, RevCommit commit, DiffFormatter df, Integer instantChangeSet, String author, CollectBugs collectBugs) {
		//incremento change set solo in commit add o modify
		
		//incremento nfix solo per operazioni di add e modify quando la commit e l'ultima per quel bug
		int nfix = calculateNfix(diff, commit, collectBugs);
			
		int deletedLines = 0;
		int createdLines = 0;

		try {
			for (Edit edit : df.toFileHeader(diff).toEditList()) {				
				createdLines+=edit.getLengthB();
				deletedLines+=edit.getLengthA();
			}
		} catch (IOException e) {
			Logger.getLogger(ParserJgit.class.getName()).log( Level.SEVERE, e.toString(), e);
		}
		Metrics metrics	 = new Metrics(nfix, instantChangeSet);
		JavaFile javaFile = new JavaFile(path, metrics, createdLines, deletedLines, diff.getChangeType().name(),
				CreatorDate.getLocalDateTimeByDate(commit.getCommitterIdent().getWhen()), author);
		if(diff.getChangeType().name().equals("COPY"))
			javaFile.setOldPath(diff.getOldPath());
		return javaFile;
	}
	
	private static int calculateNfix(DiffEntry diff, RevCommit commit, CollectBugs collectBugs) {
		for(int i=0; i!= collectBugs.getBugsWithCommits().size(); i++) {
			int last = collectBugs.getBugsWithCommits().get(i).getCommits().size();
			if((diff.getChangeType().name().equals("ADD") || diff.getChangeType().name().equals("MODIFY")) 
					&& commit.getName().equals(collectBugs.getBugsWithCommits().get(i).getCommits().get(last-1).getSha()))
				return 1;
		}
		return 0;
	}



	private static DiffFormatter getDiffFormatter(Repository repository) {
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repository);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setContext(0);
		df.setDetectRenames(true);
		return df;
	}
	
	

}
