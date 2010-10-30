package models;

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class FileEntry extends Model {

	public String uploadFilename;
	public String extension;

	@ManyToOne
	public Entry belongsTo;

	@ManyToOne
	public User owner;

	public Date timestamp;

	private static String uploadPath = "/public/files/";

	private FileEntry(String filename, Entry belongsTo, User owner) {
		this.uploadFilename = filename;
		this.belongsTo = belongsTo;
		this.owner = owner;
		this.timestamp = new Date();
		this.extension = getFileExtension(filename);
	}

	public static FileEntry upload(User user, Entry entry, File input) {

		FileEntry file = new FileEntry(input.getName(), entry, user);

		// Rename and Move File
		String fileExtension = getFileExtension(input.getName());
		String newFilename = file.id.toString() + "." + fileExtension;

		input.renameTo(new File(uploadPath, newFilename));

		file.save();

		return file;
	}

	public String absoluteFilename() {

		return this.id + "." + this.extension;
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
