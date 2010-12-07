package model.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.MajorEntry;

public class FraudPointController {
	private final static long expiration = 24 * 60 * 60 * 1000;

	private static FraudPointController instance;

	private Date lastRun;

	private List<FraudPoint> fraudPoints;

	private List<FraudPointRule> rules;

	public static FraudPointController getInstance() {
		if (instance == null) {
			instance = new FraudPointController();
		}
		return instance;
	}

	public static void addPoint(FraudPoint point) {
		point.save();
		getInstance().fraudPoints.add(point);
	}

	public FraudPointController() {
		lastRun = new Date(0);
		fraudPoints = new ArrayList();
		// SM can't find an appropriate pattern.
		rules = new ArrayList();
		rules.add(new TestRule());
	}

	public void run() {
		cleanOldPoints();
		checkNewEntries();
		lastRun = new Date();
	}

	private void checkNewEntries() {
		List<MajorEntry> entries = MajorEntry.find("timestamp > ?", lastRun)
				.fetch();
		for (FraudPointRule rule : rules) {
			for (MajorEntry entry : entries) {
				rule.check(entry);
			}
		}
	}

	public void cleanOldPoints() {
		Date expired = new Date(new Date().getTime() - expiration);
		for (FraudPoint point : fraudPoints) {
			if (point.timestamp.before(expired)) {
				point.delete();
			}
		}
	}
}
