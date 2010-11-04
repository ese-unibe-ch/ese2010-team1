package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Notification extends Model {

	@ManyToOne
	public User owner;

	@ManyToOne
	public Entry entry;

	public Date timestamp;

	public Boolean isNew;

	public String activity;

	public Notification(User owner, Entry entry, String activity) {

		this.owner = owner;
		this.entry = entry;
		this.timestamp = new Date();
		this.activity = activity;
		this.isNew = true;

	}

	public static void hasBeenRed(long id) {
		Notification notification = Notification.findById(id);
		notification.isNew = false;
		notification.save();
	}
}
