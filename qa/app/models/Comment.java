package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends MajorEntry {

	/** The entry. */
	@ManyToOne
	public Entry entry;

	@ManyToMany(cascade = CascadeType.ALL)
	public Set<User> fans;

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
		this.fans = new HashSet<User>();
	}

	public void like(User user) {
		if (!likedBy(user) && user != this.owner) {
			this.fans.add(user);
			user.likedComments.add(this);
			user.save();
			this.save();
		}
	}

	/**
	 * Unlike the given comment.
	 * 
	 * @param user
	 *            the user
	 */
	public void unlike(User user) {
		if (likedBy(user)) {
			this.fans.remove(user);
			user.likedComments.remove(this);
			user.save();
			this.save();
		}
	}

	/**
	 * Returns whether the comment is liked by the given user.
	 * 
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean likedBy(User user) {
		return fans.contains(user);
	}

	public long rating() {
		return 0;
	}

}
