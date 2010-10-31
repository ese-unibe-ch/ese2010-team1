import java.io.File;
import java.io.IOException;

import models.FileEntry;

import org.junit.Test;

import play.test.UnitTest;

public class FileEntryTest extends UnitTest {

	@Test
	public void FileExtensionTest() {

		assertEquals("exe", FileEntry.getFileExtension("filename.exe"));

		assertEquals("blub", FileEntry.getFileExtension("test.name.blub"));

	}

	@Test
	public void UploadFileTest() {

		File testFile = new File("public/files/test.txt");
		System.out.println(testFile.getAbsolutePath());
		try {
			testFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(testFile);
		/*
		 * FileEntry file = FileEntry.upload(testFile);
		 * 
		 * assertNotNull(file); FileEntry foundFile =
		 * FileEntry.findById(file.id); assertNotNull(foundFile);
		 */
	}

}
