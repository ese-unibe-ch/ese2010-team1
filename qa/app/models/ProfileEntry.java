package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class ProfileEntry extends Model {

	@Lob
	public String entry;

	@ManyToOne
	public ProfileItem item;

	public User user;

	public ProfileEntry(ProfileItem item, String entry, User user) {

		this.item = item;
		this.entry = entry;

	}

}
