package common.parser;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import common.entity.Bug;
import common.entity.Release;
import common.exceptions.MyException;
import common.rest.JsonRest;
import common.strings.Strings;
import common.utils.CreatorDate;
import common.utils.CreatorUrls;

public class ParserJira {

	private ParserJira() {}

	
	//Questo metodo restituisce una lista di release prese con funzione REST get da jira
	public static List<Release> getRelease(String projName) throws MyException {
		//Fills the arraylist with releases dates and orders them
		//Ignores releases with missing dates
		List<Release> releases = new ArrayList<>();
		JSONObject json = null;
		try {
			json = JsonRest.readJsonFromUrl(CreatorUrls.createUrlReleases(projName));
		} catch (IOException e) {
			Logger.getLogger(ParserJira.class.getName()).log( Level.SEVERE, e.toString(), e);
			return releases;
		}
		JSONArray versions = json.getJSONArray(Strings.FIELD_VERSIONS);
		fillList(versions,releases);
		// order releases by date
		Collections.sort(releases, (release1,release2)-> release1.getDate().compareTo(release2.getDate()));
		if (releases.size() < 6)
			throw new MyException("Errore, le release sono meno di 6.");

		return releases;
	}

	//Questo metodo fa parsing dei campi del json restituito
	private static void fillList(JSONArray versions, List<Release> releases) {
		for (int i = 0; i < versions.length(); i++ ) {

			String name = "";
			String id = "";
			if(versions.getJSONObject(i).has(Strings.FIELD_RELEASE_DATE)) {
				if (versions.getJSONObject(i).has(Strings.FIELD_NAME))
					name = versions.getJSONObject(i).get(Strings.FIELD_NAME).toString();
				if (versions.getJSONObject(i).has(Strings.FIELD_ID))
					id = versions.getJSONObject(i).get(Strings.FIELD_ID).toString();
				addRelease(releases, versions.getJSONObject(i).get(Strings.FIELD_RELEASE_DATE).toString(),
						name,id);
			}
		}
	}

	//Funzione add in list
	private static void addRelease(List<Release> releases, String strDate, String name, String id) {
		LocalDateTime dateTime = CreatorDate.defaultParseWithoutHour(strDate);
		Release newRelease = new Release(id,name,dateTime);
		//modifico la lista
		if(modifyReleaseList(releases,dateTime,name,id).equals(Boolean.FALSE))
			releases.add(newRelease);
	}


	//Se la data della release è gia presente nella lista, mantengo la data e sostituisco la release precedente
	private static Boolean modifyReleaseList(List<Release> releases, LocalDateTime dateTime , String name, String id) {
		//check se la lista contiene gia una release in quella data
		for (Release release : releases) {
			if(release.getDate().compareTo(dateTime)==0) {
				releases.get(releases.indexOf(release)).setName(name);
				releases.get(releases.indexOf(release)).setId(id);
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	//questo metodo restituisce una lista di bug dopo aver fatto una chiamata get a jira
	public static List<Bug> getProjectBug(String projectName) {
		/*fields 
        	versions: [
            
                "self": "https://issues.apache.org/jira/rest/api/2/version/12324601",
                "id": "12324601",
                "name": "4.2.2",
                "archived": false,
                "released": true,
                "releaseDate": "2013-10-10"
            
        ],
        "resolutiondate": "2014-05-27T10:53:42.000+0000",
        "created": "2014-05-07T07:57:41.000+0000"
		 */
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		//Get JSON API for closed bugs w/ AV in the project
		List<Bug> bugsList = new ArrayList<>();
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
			j = i + 1000;
			//get bugs
			String url = CreatorUrls.createUrlBugs(projectName,i,j);
			JSONObject json = null;
			try {
				json = JsonRest.readJsonFromUrl(url);
			} catch (IOException e) {
				Logger.getLogger(ParserJira.class.getName()).log( Level.SEVERE, e.toString(), e);
				return bugsList;
			}
			JSONArray issues = json.getJSONArray(Strings.FIELD_ISSUES);
			total = json.getInt(Strings.FIELD_TOTAL);

			for (; i < total && i < j; i++) {
				JSONObject jsonBug = issues.getJSONObject(i%1000);
				fillBugList(bugsList,jsonBug);
			}      
		} while (i < total);
		// order bug by date
		Collections.sort(bugsList, (bug1,bug2)-> bug1.getOpenDate().compareTo(bug2.getOpenDate()));
		return bugsList;
	}

	//Questo metodo riempie una lista di bug inserendo come campi del bug: nomeid,data di apertura e versioni affette
	private static void fillBugList(List<Bug> bugsList, JSONObject jsonBug) {
		//Iterate through each bug
		String key = jsonBug.get(Strings.FIELD_KEY).toString();
		//2014-05-07T07:57:41.000+0000 da parsare
		JSONObject fields = (JSONObject) jsonBug.get(Strings.FIELD_FIELDS);
		String strOpenDate = fields.get(Strings.FIELD_CREATED).toString();
		LocalDateTime openDate = CreatorDate.defaultParseDate(strOpenDate);

		JSONArray versions = (JSONArray) fields.get(Strings.FIELD_VERSIONS);
		List<Release> affectedReleases = new ArrayList<>();
		for (int i = 0; i!= versions.length(); i++) {
			String name = "";
			String id = "";
			if(versions.getJSONObject(i).has(Strings.FIELD_RELEASE_DATE)) {
				if (versions.getJSONObject(i).has(Strings.FIELD_ID))
					id = versions.getJSONObject(i).get(Strings.FIELD_ID).toString();
				if (versions.getJSONObject(i).has(Strings.FIELD_NAME))
					name = versions.getJSONObject(i).get(Strings.FIELD_NAME).toString();
				String releaseDate = versions.getJSONObject(i).get(Strings.FIELD_RELEASE_DATE).toString();
				LocalDateTime date = CreatorDate.defaultParseWithoutHour(releaseDate);
				affectedReleases.add(new Release(id,name,date));
			}
		
		}

		Bug bug = new Bug(key,openDate,affectedReleases);
		bugsList.add(bug);
	}

}
