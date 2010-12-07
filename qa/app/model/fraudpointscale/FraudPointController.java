package model.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.MajorEntry;
import play.db.jpa.Model;

public class FraudPointController extends Model {
	private final static long expiration = 24 * 60 * 60 * 1000;

	private static FraudPointController instance;

	@OneToOne
	private Date lastRun;

	@OneToMany
	private List<FraudPoint> fraudPoints;

	@OneToMany
	private List<FraudPointRule> rules;

	public static FraudPointController getInstance() {
		if (instance == null) {
			instance = FraudPointController.all().first();
			if (instance == null) {
				instance = new FraudPointController().save();
			}
		}
		return instance;
	}

	private FraudPointController() {
		lastRun = new Date(0);
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

	public static void addPoint(FraudPoint point) {
		point.save();
		getInstance().fraudPoints.add(point);
		getInstance().save();
	}
}
