package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.MajorEntry;
import models.User;

public class NoSimilarContentRule extends FraudPointRule {

	private Date checkDate;

	@Override
	public void checkSince(Date lastCheck) {

		this.checkDate = lastCheck;

		for (User user : this.findPotentialCheaters()) {

			addPoint(user);
		}

	}

	public List<User> findPotentialCheaters() {

		List<User> potentialCheater = new ArrayList<User>();
		List<MajorEntry> entryWithSimilarContent = MajorEntry
				.find(
						"select a from MajorEntry a, MajorEntry b where a.content = b.content and a.timestamp >= ?",
						checkDate).fetch();

		for (MajorEntry entry : entryWithSimilarContent) {
			if (!potentialCheater.contains(entry.owner))
				potentialCheater.add(entry.owner);
		}
		return potentialCheater;
	}

}
