package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

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

	/** The reporters. **/
	@ManyToMany
	public Set<User> reporter;

	public MajorEntry(User owner, String content) {
		this.content = content;
		this.owner = owner;
		this.timestamp = new Date();
		this.reporter = new HashSet<User>();
	}

	public void report(User user) {
		reporter.add(user);
		this.save();
	}

	public boolean isReportedFrom(User user) {
		return reporter.contains(user);
	}

	abstract public long rating();

}
