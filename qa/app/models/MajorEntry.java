package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public abstract class MajorEntry extends Model {

	/** The timestamp. */
	public Date timestamp;

	/** The faked id for XML importer */
	public long fakeId;

	/** The content */
	@Lob
	public String content;

	/** The owner. */
	@ManyToOne
	public User owner;

	/** The related spam reports. */
	@OneToMany
	public Set<Report> reports;

	public MajorEntry(User owner, String content) {
		this.content = content;
		this.owner = owner;
		this.timestamp = new Date();
		this.reports = new HashSet<Report>();
	}

	/**
	 * Reports entry as spam.
	 * 
	 * @param user
	 *            the reporting user
	 */
	public void report(User user) {
		if (user != this.owner) {
			this.reports.add((Report) new Report(user, this).save());
			this.save();
		}
	}

	/**
	 * Checks if is already reported from.
	 * 
	 * @param user
	 *            the user
	 * @return true, if is reported from
	 */
	public boolean isReportedFrom(User user) {
		return !Report.find("byReporterAndEntry", user, this).fetch().isEmpty();
	}

	/**
	 * Checks if the entry is already reported.
	 * 
	 * @return true, if is reported
	 */
	public boolean isReported() {
		return !reports.isEmpty();
	}

	/**
	 * Sets the rating value of a given entry.
	 * 
	 * @return the long
	 */
	public long rating() {
		return 0;
	}

}
