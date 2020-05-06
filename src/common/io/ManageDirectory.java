package common.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class ManageDirectory {
	private ManageDirectory() {
	}
	
	
	public static void deleteDirectory(File dir) throws IOException {
		FileUtils.deleteDirectory(dir);
    }
		

}
	