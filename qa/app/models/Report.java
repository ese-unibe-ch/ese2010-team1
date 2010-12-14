package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Report extends Model {

	@ManyToOne
	public User reporter;
	@ManyToOne
	public MajorEntry entry;

	public Date timeStamp;

	public Report(User reporter, MajorEntry entry) {
		this.reporter = reporter;
		this.entry = entry;
		this.timeStamp = new Date();
	}

}
