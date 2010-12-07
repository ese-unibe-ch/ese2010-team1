package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends MajorEntry {

	/** The entry. */
	@ManyToOne
	public Entry entry;

	@ManyToMany
	public List<User> fans;

	/**
	 * Instantiates a new comment.
	 * 
	 * @param owner
	 *            the owner
	 * @param entry
	 *            the entry
	 * @param content
	 *            the content
	 */
	public Comment(User owner, Entry entry, String content) {
		super(owner, content);
		this.entry = entry;
		this.fans = new LinkedList<User>();
	}

	public void like(User user) {
		if (!likedBy(user) && user != this.owner) {
			this.fans.add(user);
			this.save();
		}
	}

	public void unlike(User user) {
		if (likedBy(user)) {
			this.fans.remove(user);
			this.save();
		}
	}

	public boolean likedBy(User user) {
		return fans.contains(user);
	}

	public long rating() {
		return 0;
	}

}
