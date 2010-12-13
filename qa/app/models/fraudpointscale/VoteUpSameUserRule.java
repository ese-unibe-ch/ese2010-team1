package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Entry;
import models.User;

public class VoteUpSameUserRule extends FraudPointRule {

	private Date checkDate;
	private static int MIN_USER_VOTES = 2;

	@Override
	public void checkSince(Date lastCheck) {

		this.checkDate = lastCheck;

	}

	private List<User> findPotentialCheaters() {

		List<User> userList = new ArrayList<User>();

		return null;
	}

	public List<Entry> findEntrysWithNewVotes() {

		return Entry
				.find(
						"select entry e, vote v, count(e.owner) > 2 and e.votes = v and votes.timestamp >= ?",
						checkDate).fetch();
	}
}
