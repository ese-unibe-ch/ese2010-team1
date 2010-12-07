package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
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

	public MajorEntry(User owner, String content) {
		this.content = content;
		this.owner = owner;
		this.timestamp = new Date();
	}

	abstract public long rating();

}
