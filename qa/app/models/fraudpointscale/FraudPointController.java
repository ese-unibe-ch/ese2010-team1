package models.fraudpointscale;

import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;

public class FraudPointController {
	private final static long expiration = 24 * 60 * 60 * 1000;

	private static FraudPointController instance;

	private Date lastRun;

	public static FraudPointController getInstance() {
		if (instance == null) {
			instance = new FraudPointController();
		}
		return instance;
	}

	public FraudPointController() {
		lastRun = new Date(0);
	}

	public void run() {
		cleanOldPoints();
		applyRules();
		lastRun = new Date();
	}

	private void applyRules() {
		ServiceLoader<FraudPointRule> rules = ServiceLoader
				.load(FraudPointRule.class);
		for (FraudPointRule rule : rules) {
			rule.check();
		}
	}

	public void cleanOldPoints() {
		Date expired = new Date(new Date().getTime() - expiration);
		List<FraudPoint> fraudPoints = FraudPoint.all().fetch();
		for (FraudPoint point : fraudPoints) {
			if (point.timestamp.before(expired)) {
				point.delete();
			}
		}
	}
}
