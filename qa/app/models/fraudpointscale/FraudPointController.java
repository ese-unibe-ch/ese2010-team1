package models.fraudpointscale;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/** The rules. */
	private List<FraudPointRule> rules;

	/** The rule description. */
	private Map<Class<? extends FraudPointRule>, String> ruleDescription;

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
		this.lastRun = new Date(0);
		this.rules = new ArrayList<FraudPointRule>();
		this.ruleDescription = new HashMap<Class<? extends FraudPointRule>, String>();
		this.loadRules();

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
		if (rules.isEmpty())
			this.loadRules();

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

	/**
	 * Load all fraud point rules in the controller.
	 */
	public void loadRules() {
		this.rules.clear();
		this.ruleDescription.clear();
		this.rules = RuleLoader.getRuleInstances();
		for (FraudPointRule p : rules) {
			ruleDescription.put(p.getClass(), p.description());
		}

	}

	/**
	 * Gets the description for a given fraud point rule.
	 * 
	 * @param c
	 *            the given class
	 * @return the description
	 */
	public String getDescription(Class<? extends FraudPointRule> c) {

		return ruleDescription.get(c);
	}
}
