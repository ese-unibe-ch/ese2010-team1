import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		// User
		User jack = new User("Jack", "jack@test.com", "secret");
		User john = new User("John", "john@test.com", "hallo");
		User bill = new User("Bill", "bill@example.com", "noone");
		User kate = new User("Kate", "kate@example.com", "password");

		// Questions
		/*
		 * 
		 * 
		 * Question question = new Question(jack,
		 * "Why did the chicken cross the road?"); question.answer(bill,
		 * "To get to the other side.");
		 * 
		 * question = new Question(john,
		 * "What is the answer to life the universe and everything?");
		 * question.answer(kate, "42"); question.answer(kate, "1337");
		 */
	}

}