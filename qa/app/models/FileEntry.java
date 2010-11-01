package models;

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;

import play.Play;
import play.db.jpa.Model;

@Entity
public class FileEntry extends Model {

	public String uploadFilename;
	public String extension;
	/*
	 * @ManyToOne public Entry belongsTo;
	 * 
	 * @ManyToOne public User owner;
	 */

	public Date timestamp;
	public int picID;
	private static int pictureCounter = 1;

	private static String uploadPath = "/public/files/";

	private FileEntry(String filename) {
		this.uploadFilename = filename;
		// this.belongsTo = belongsTo;
		// this.owner = owner;
		this.timestamp = new Date();
		this.extension = getFileExtension(filename);
		this.picID = pictureCounter++;

	}

	public static FileEntry upload(File input) {

		FileEntry file = new FileEntry(input.getName());

		// Rename and Move File
		String newFilename = file.picID + "." + file.extension;

		input
				.renameTo(new File(Play.applicationPath + uploadPath
						+ newFilename));

		file.save();

		return file;
	}

	public boolean deleteFile() {
		File file = new File(Play.applicationPath + uploadPath + this.picID
				+ "." + this.extension);
		if (file.exists()) {
			return file.delete();
		}

		return false;
	}

	public String absoluteFilename() {

		return this.picID + "." + this.extension;
	}

	public static boolean deleteFile(FileEntry file) {

		return false;
	}

	public static String getFileExtension(String f) {
		String ext = "";
		int i = f.lastIndexOf('.');
		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
