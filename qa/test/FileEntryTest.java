import models.FileEntry;

import org.junit.Test;

import play.test.UnitTest;

public class FileEntryTest extends UnitTest {

	@Test
	public void FileExtensionTest() {

		assertEquals("exe", FileEntry.getFileExtension("filename.exe"));

		assertEquals("blub", FileEntry.getFileExtension("test.name.blub"));

	}

}
