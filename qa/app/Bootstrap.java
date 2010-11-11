import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import models.Question;
import models.User;

import org.xml.sax.SAXException;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		if (User.count() == 0) {
			Fixtures.load("initial-data.yml");

			User tobias = User.find("byName", "Tobias").first();
			User simon = User.find("byName", "Simon").first();
			Question question = Question.find("byOwner", tobias).first();
			question.voteUp(simon);

		}

		try {
			new XMLreader("\\QA3.xml");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}