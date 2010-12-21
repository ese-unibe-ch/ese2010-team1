package models.fraudpointscale;

import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class FraudPointController.
 */
public class FraudPointController {

	/** The Constant expiration. */
	private final static long expiration = 24 * 60 * 60 * 1000;

	/** The instance. */
	private static FraudPointController instance;

	/** The last run. */
	private Date lastRun;

	/**
	 * Gets the single instance of FraudPointController.
	 * 
	 * @return single instance of FraudPointController
	 */
	public static FraudPointController getInstance() {
		if (instance == null) {
			instance = new FraudPointController();
		}
		return instance;
	}

	/**
	 * Instantiates a new fraud point controller.
	 */
	public FraudPointController() {
		lastRun = new Date(0);
	}

	/**
	 * Run.
	 */
	public void run() {
		cleanOldPoints();
		applyRules();
		lastRun = new Date();
	}

	/**
	 * Apply rules.
	 */
	private void applyRules() {
		List<FraudPointRule> rules = RuleLoader.getRuleInstances();
		for (FraudPointRule rule : rules) {
			rule.checkSince(lastRun);
		}
	}

	/**
	 * Clean old points.
	 */
	public void cleanOldPoints() {
		Date expired = new Date(new Date().getTime() - expiration);
		FraudPoint.delete("timestamp < ?", expired);
	}
}
