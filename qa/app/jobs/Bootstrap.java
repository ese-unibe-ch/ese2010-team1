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
			Fixtures.load("initial-data.yml");
			// Default users
			User admin = new User("Admin", "admin@root.local", "secret");
			admin.isAdmin = true;
			admin.isActivated = true;
			admin.save();
			new User("Anonymous", "anonymous@qa.local", "notAllowedToLogIn")
					.save();

		}
		// Create instance of the fraudPointController
		FraudPointController.getInstance();
	}
}