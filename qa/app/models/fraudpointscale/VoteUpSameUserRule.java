package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Entry;
import models.User;

public class VoteUpSameUserRule extends FraudPointRule {

	private Date checkDate;

	@Override
	public void checkSince(Date lastCheck) {

		this.checkDate = lastCheck;

	}

	private List<User> findPotentialCheaters() {

		List<Entry> entriesWithNewVotes = Entry.find(
				"select entry e, vote v, e.votes = v and votes.timestamp >= ?",
				checkDate).fetch();

		List<User> userList = new ArrayList<User>();

		return null;
	}

}
