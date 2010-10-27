package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * A vote on a {@link Entry} belonging to a {@link User}. The <code>Vote</code>
 * can be positive or negative.
 * 
 */
@Entity
public class Vote extends Model {

	public boolean up;
	@ManyToOne
	public Entry entry;

	@ManyToOne
	public User owner;

	/**
	 * Create a <code>Vote</code>.
	 * 
	 * @param owner
	 *            the {@link User} who created the <code>Vote</code>
	 * @param entry
	 *            the {@link Entry} the <code>Vote</code> belongs to.
	 * @param up
	 *            true if the <code>Vote</code> is positive
	 */
	public Vote(User owner, Entry entry, boolean up) {
		this.owner = owner;
		this.up = up;
		this.entry = entry;
		owner.addVote(this);
	}

	/**
	 * Check if a <code>Vote</code> is positive or negative.
	 * 
	 * @return true if the <code>Vote</code> is positive
	 */
	public boolean up() {
		return this.up;
	}

}
