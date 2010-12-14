package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Entry;
import models.User;
import models.Vote;

// TODO: Auto-generated Javadoc
/**
 * The Class VoteUpSameUserRule.
 */
public class VoteUpSameUserRule extends FraudPointRule {

	/** The check date. */
	private Date checkDate;

	/** The maximum limit a user can vote without being a cheater. */
	private static int USER_VOTES_THRESHOLD = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.fraudpointscale.FraudPointRule#checkSince(java.util.Date)
	 */
	@Override
	public void checkSince(Date lastCheck) {

		this.checkDate = lastCheck;

	}

	// TS for the moment it's a bad algorithm o(n^2) in worst case

	/**
	 * Find potential cheaters.
	 * 
	 * @return the list
	 */
	public List<User> findPotentialCheaters() {

		List<User> potentialCheater = new ArrayList<User>();
		List<Vote> newVotes = Vote.find("freezer.timestamp >= ?", checkDate)
				.fetch();

		Map<UserPair, Integer> relationMap = new HashMap<UserPair, Integer>();

		for (Vote v : newVotes) {

			UserPair pair = new UserPair(v.owner, v.entry.owner);

			if (!relationMap.containsKey(pair)) {
				relationMap.put(pair, 1);
			} else {
				Integer i = relationMap.get(pair);
				relationMap.put(pair, i + 1);
			}

		}

		for (Map.Entry<UserPair, Integer> e : relationMap.entrySet()) {

			if (e.getValue() >= USER_VOTES_THRESHOLD) {
				potentialCheater.add(e.getKey().voter);
			}

		}

		return potentialCheater;
	}

	/**
	 * Find entrys with new votes.
	 * 
	 * @return the list
	 */
	public List<Entry> findEntrysWithNewVotes() {

		return Entry.find(
				"select entry e, vote v, e.votes = v and v.timestamp >= ?",
				checkDate).fetch();
	}

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
			int hash = 0;
			hash ^= voter.hashCode();
			hash ^= author.hashCode();

			return hash;
		}

	}
}
