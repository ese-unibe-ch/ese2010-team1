package models;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Comment extends Model {

	/** The content. */
	@Lob
	public String content;

	/** The owner. */
	@ManyToOne
	public User owner;

	/** The timestamp. */
	public Date timestamp;

	/** The entry. */
	@ManyToOne
	public Entry entry;

	@OneToMany
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
		this.owner = owner;
		this.content = content;
		this.timestamp = new Date();
		this.entry = entry;
		this.fans = new LinkedList<User>();
	}

	public void like(User user) {
		//assert !fans.contains(user);
		//assert user != this.owner;
		this.fans.add(user);
	}
	
	public void dislike(User user){
		assert fans.contains(user);
		assert user != this.owner;
		this.fans.remove(user);
	}

}
