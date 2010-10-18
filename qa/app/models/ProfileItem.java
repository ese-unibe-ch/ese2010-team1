package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class ProfileItem extends Model {

	public String title;

	@OneToMany(mappedBy = "item", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<ProfileEntry> entrys;

	public ProfileItem(String title) {
		this.title = title;
		this.entrys = new ArrayList<ProfileEntry>();

	}

	private ProfileItem addEntry(ProfileEntry entry) {
		entrys.add(entry);
		this.save();
		return this;
	}

	public String title() {
		return this.title;
	}

	public ProfileEntry findUserEntry(User user) {

		return ProfileEntry.find("byItemAndUser", this, user).first();

	}

	public boolean hasUserEntry(User user) {
		return findUserEntry(user) != null;
	}

	// TODO something is wrong here (or in the controller)
	public ProfileItem editUserEntry(User user, String entry) {

		if (!hasUserEntry(user)) {
			ProfileEntry pentry = new ProfileEntry(this, entry, user).save();
			this.addEntry(pentry);
		} else {
			ProfileEntry userEntry = findUserEntry(user);
			userEntry.entry = entry;
			userEntry.save();
		}

		this.save();
		return this;
	}
}
