package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.User;
import models.Vote;
import models.helper.UserPair;

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

		for (User user : this.findPotentialCheaters()) {

			addPoint(user);
		}

	}

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

		// Find number of same relations
		for (Vote v : newVotes) {

			UserPair pair = new UserPair(v.owner, v.entry.owner);

			if (!relationMap.containsKey(pair)) {
				relationMap.put(pair, 1);
			} else {
				Integer i = relationMap.get(pair);
				relationMap.put(pair, i + 1);
			}

		}
		// search relations with more than the user votes threshold value
		for (Map.Entry<UserPair, Integer> e : relationMap.entrySet()) {

			if (e.getValue() >= USER_VOTES_THRESHOLD) {
				potentialCheater.add(e.getKey().voter);
			}

		}

		return potentialCheater;
	}

}
