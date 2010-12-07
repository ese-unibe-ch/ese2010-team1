package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import models.helper.TimeFreezer;

import play.db.jpa.Model;

/**
 * A vote on a {@link Entry} belonging to a {@link User}. The <code>Vote</code>
 * can be positive or negative.
 * 
 */
@Entity
public class Vote extends Model {

	/** The state of the vote. */
	public boolean up;

	/** The entry. */
	@ManyToOne
	public Entry entry;

	/** The owner. */
	@ManyToOne
	public User owner;

	/** The freezer. */
	@OneToOne
	public TimeFreezer freezer;

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
		this.freezer = new TimeFreezer(60 * 2).save();
		owner.addVote(this);
		entry.owner.calcReputation();
	}

	/**
	 * Check if a vote can't get changed.
	 * 
	 * @return true, if successful
	 */
	public boolean frozen() {
		return this.freezer.frozen();
	}

}
