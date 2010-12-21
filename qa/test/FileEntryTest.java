import java.io.File;
import java.io.IOException;

import models.FileEntry;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.test.Fixtures;
import play.test.UnitTest;

public class FileEntryTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void FileExtensionTest() {

		assertEquals("exe", FileEntry.getFileExtension("filename.exe"));

		assertEquals("blub", FileEntry.getFileExtension("test.name.blub"));

	}

	@Test
	public void UploadFileTest() {

		File testFile = new File(Play.applicationPath
				+ "/public/files/test.txt");
		try {
			testFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		assertNotNull(testFile);
		FileEntry file = FileEntry.upload(testFile, null, null);

		assertNotNull(file);
		FileEntry foundFile = FileEntry.findById(file.id);
		assertNotNull(foundFile);

	}

	@Test
	public void deleteUploadedFile() {

		File testFile = new File(Play.applicationPath
				+ "/public/files/test.txt");
		try {
			testFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		User user = new User("test", "test", "test").save();
		FileEntry file = FileEntry.upload(testFile, null, user);

		FileEntry entry = FileEntry.find("byContent", "test.txt").first();
		assertNotNull(entry);
		assertEquals(file.owner, user);

		assertTrue(entry.deleteFile());
	}
}
