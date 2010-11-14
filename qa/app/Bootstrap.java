import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		// if (User.count() == 0) {
		// Fixtures.load("initial-data.yml");
		//
		// User tobias = User.find("byName", "Tobias").first();
		// User simon = User.find("byName", "Simon").first();
		// Question question = Question.find("byOwner", tobias).first();
		// question.voteUp(simon);
		//
		// }
		// new User("test", "email", "secret").save();
		User admin = new User("Admin", "admin@root.local", "secret");
		admin.isAdmin = true;
		admin.save();
		User anonymous = new User("Anonymous", "anonymous@qa.local",
				"notAllowedToLogIn").save();
		new User("Default", "default", "").save();

	}
}