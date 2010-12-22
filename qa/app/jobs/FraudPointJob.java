package jobs;

import models.fraudpointscale.FraudPointController;
import play.jobs.Every;
import play.jobs.Job;

@Every("2min")
public class FraudPointJob extends Job {
	public void doJob() {
		FraudPointController.getInstance().run();
	}
}
