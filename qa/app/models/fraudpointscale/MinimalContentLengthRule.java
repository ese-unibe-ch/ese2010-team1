package models.fraudpointscale;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.FileEntry;
import models.MajorEntry;
import models.User;

public class MinimalContentLengthRule extends FraudPointRule {
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

			if (e.content.length() < MINIMAL_CONTENT_LENGTH
					&& !(e instanceof FileEntry))
				potentialCheaters.add(e.owner);

		}

		return potentialCheaters;
	}

	@Override
	public String description() {
		return "Posting entries with content length smaller than"
				+ MINIMAL_CONTENT_LENGTH + "characters";
	}

}
