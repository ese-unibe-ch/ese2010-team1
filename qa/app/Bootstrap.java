import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.User;

import org.xml.sax.SAXException;

import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() throws SAXException, IOException,
			ParserConfigurationException {
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
		new User("Default", "default", "").save();
		// File file = new File("qa\\QA3.xml");
		// new XMLreader(file);

	}
}