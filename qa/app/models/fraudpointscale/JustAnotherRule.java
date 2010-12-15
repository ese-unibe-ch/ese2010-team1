package models.fraudpointscale;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.MajorEntry;
import models.User;

public class JustAnotherRule extends FraudPointRule {
	private Date checkDate;

	private static int MINIMAL_CONTENT_LENGTH = 15;

	@Override
	public void checkSince(Date lastCheck) {
		this.checkDate = lastCheck;

		for (User u : this.findPotentialCheaters()) {
			addPoint(u);
		}

	}

	public Set<User> findPotentialCheaters() {
		List<MajorEntry> changedEntries = MajorEntry.find("timestamp >= ?",
				checkDate).fetch();

		Set<User> potentialCheaters = new HashSet<User>();

		for (MajorEntry e : changedEntries) {

			if (e.content.length() < MINIMAL_CONTENT_LENGTH)
				potentialCheaters.add(e.owner);

		}

		return potentialCheaters;
	}

}
