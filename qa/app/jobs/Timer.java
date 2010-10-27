package jobs;

import models.Answer;
import play.jobs.Job;

//OT bad name, it's not just a timer, it's the Timer who sets the best answer final.
public class Timer extends Job {

	private long answerID;

	public void doJob() {
		Answer answer = Answer.<Answer> findById(answerID);
		answer.bestAnswerSetter().canBeUndone(false);
		answer.question().save();
		answer.save();
	}
	//OT should be setAnswerID, is there a reason not to give the ID in a Constructor?
	public void answerID(long answerID) {
		this.answerID = answerID;
	}
}
