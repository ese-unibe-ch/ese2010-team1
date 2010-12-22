package jobs;

import models.User;
import models.fraudpointscale.FraudPointController;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		if (User.count() == 0) {
			// Loads default user
			Fixtures.load("initial-data.yml");

		}
		// Create instance of the fraudPointController
		FraudPointController.getInstance();
	}
}