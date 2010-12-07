package jobs;

import model.fraudpointscale.FraudPointController;
import play.jobs.Every;
import play.jobs.Job;

@Every("1h")
public class FraudPointJob extends Job {
	public void doJob() {
		FraudPointController.getInstance().run();
	}
}
