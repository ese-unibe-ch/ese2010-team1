package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * The Class Report gathers information about spam entries.
 */
@Entity
public class Report extends Model {

	/** The reporter. */
	@ManyToOne
	public User reporter;

	/** The related entry. */
	@ManyToOne
	public MajorEntry entry;

	/** The time stamp. */
	public Date timeStamp;

	/**
	 * Instantiates a new report.
	 * 
	 * @param reporter
	 *            the reporter
	 * @param entry
	 *            the entry
	 */
	public Report(User reporter, MajorEntry entry) {
		this.reporter = reporter;
		this.entry = entry;
		this.timeStamp = new Date();
	}

}
