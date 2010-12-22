package models.fraudpointscale;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.MajorEntry;
import models.helper.UserEntryFrequency;

/**
 * The Class ImplausibleFrequenceyRule.
 */
public class ImplausibleFrequenceyRule extends FraudPointRule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.fraudpointscale.FraudPointRule#checkSince(java.util.Date)
	 */
	@Override
	public void checkSince(Date lastCheck) {
		List<MajorEntry> entries = MajorEntry.find(
				"timestamp > ? order by timestamp", lastCheck).fetch();
		HashMap<String, UserEntryFrequency> users = new HashMap();
		for (MajorEntry entry : entries) {
			if (users.containsKey(entry.owner.name)) {
				users.get(entry.owner.name).add(entry);
			} else {
				users.put(entry.owner.name, new UserEntryFrequency(entry));
			}
		}
		for (UserEntryFrequency userFreq : users.values()) {
			if (userFreq.suspicious())
				addPoint(userFreq.user());
		}
	}

	@Override
	public String description() {

		return "Posting entries with to implausible frequencey";
	}

}
