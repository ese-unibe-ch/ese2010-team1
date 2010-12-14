package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Entry;
import models.User;
import models.Vote;

public class VoteUpSameUserRule extends FraudPointRule {

	private Date checkDate;
	private static int USER_VOTES_THRESHOLD = 2;

	@Override
	public void checkSince(Date lastCheck) {

		this.checkDate = lastCheck;

	}

	// TS for the moment it's a bad algorithm o(n^2) in worst case

	public List<User> findPotentialCheaters() {

		List<User> potentialCheater = new ArrayList<User>();
		List<Vote> newVotes = Vote.find("timestamp >= ?", checkDate).fetch();

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

	public List<Entry> findEntrysWithNewVotes() {

		return Entry
				.find(
						"select entry e, vote v, count(e.owner) > 2 and e.votes = v and votes.timestamp >= ?",
						checkDate).fetch();
	}

	public class UserPair {

		public User voter;
		public User author;

		public UserPair(User voter, User author) {
			this.voter = voter;
			this.author = author;
		}

		public boolean equals(UserPair o1, UserPair o2) {

			return o1.voter == o2.voter && o1.author == o2.author;

		}

	}
}
