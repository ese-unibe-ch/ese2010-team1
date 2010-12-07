package models;

import java.io.File;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.Play;
import play.db.jpa.JPASupport;

@Entity
public class FileEntry extends MajorEntry {
	/** The extension. */
	public String extension;
	/** The entry. */
	@ManyToOne
	public Entry entry;
	/** The picID. */
	public int picID;
	/** A picturecounter. */
	private static int pictureCounter = 1;
	/** The path where the files are stored. */
	private static String uploadPath = "/public/files/";

	/**
	 * Instantiates a new FileEntry.
	 * 
	 * @param filename
	 *            the filename
	 * @param entry
	 *            the entry
	 * @param owner
	 *            the owner
	 */
	private FileEntry(String filename, Entry entry, User owner) {
		super(owner, filename);
		this.entry = entry;
		this.extension = getFileExtension(filename);
		this.picID = pictureCounter++;

	}

	/**
	 * Uploads a new file and creates the right name of it.
	 * 
	 * @param input
	 *            the file
	 * @param entry
	 *            the entry
	 * @param user
	 *            the owner
	 */
	public static FileEntry upload(File input, Entry entry, User user) {

		FileEntry file = new FileEntry(input.getName(), entry, user);

		// Rename and Move File
		String newFilename = file.picID + "." + file.extension;

		input
				.renameTo(new File(Play.applicationPath + uploadPath
						+ newFilename));

		file.save();

		return file;
	}

	/**
	 * Deletes a File.
	 * 
	 * @return true if its successfully deleted, false if its note or it didn't
	 *         even exist.
	 */
	public boolean deleteFile() {
		File file = new File(Play.applicationPath + uploadPath + this.picID
				+ "." + this.extension);
		if (file.exists()) {
			return file.delete();

		}

		return false;
	}

	/**
	 * Gets the absolute filename.
	 * 
	 * @return absolute filename
	 */
	public String absoluteFilename() {

		return this.picID + "." + this.extension;
	}

	public static boolean deleteFile(FileEntry file) {

		return false;
	}

	/**
	 * Gets the file extension.
	 * 
	 * @param f
	 *            the string
	 * @param the
	 *            file extension
	 */

	public static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Gets the absolute path.
	 * 
	 * @return the absolute path
	 */
	public String getAbsolutePath() {

		return Play.applicationPath + uploadPath + this.picID + "."
				+ this.extension;

	}

	@Override
	public <T extends JPASupport> T delete() {

		this.deleteFile();

		return super.delete();

	}

	public long rating() {
		return 0;
	}

	public String getFilename() {
		return content;
	}

}
