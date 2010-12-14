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
		List<MajorEntry> entryWithSimilarContent = (List<MajorEntry>) MajorEntry
				.find("select MajorEntry a, MajorEntry b where a.content = b.content");

		for (MajorEntry entry : entryWithSimilarContent) {
			if (!potentialCheater.contains(entry))
				potentialCheater.add(entry.owner);
		}
		return potentialCheater;
	}

}
