package models.helper;

import models.User;

/**
 * The Class UserPair.
 */
public class UserPair {

	/** The voter. */
	public User voter;

	/** The author. */
	public User author;

	/**
	 * Instantiates a new user pair.
	 * 
	 * @param voter
	 *            the voter
	 * @param author
	 *            the author
	 */
	public UserPair(User voter, User author) {
		assert voter != author;
		this.voter = voter;
		this.author = author;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o1) {

		return ((UserPair) o1).voter == this.voter
				&& ((UserPair) o1).author == this.author;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == this.voter ? 0 : voter.hashCode());
		hash = 31 * hash + (null == this.author ? 0 : author.hashCode());

		return hash;
	}

}