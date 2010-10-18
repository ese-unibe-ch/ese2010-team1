package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class ProfileItem extends Model {

	private String title;

	@OneToMany
	private List<ProfileEntry> entrys;

	public ProfileItem(String title) {

		this.entrys = new ArrayList<ProfileEntry>();
		this.title = title;

	}

	public ProfileItem addEntry(ProfileEntry entry) {
		entrys.add(entry);
		this.save();
		return this;
	}

	public String title() {
		return this.title;
	}

}
