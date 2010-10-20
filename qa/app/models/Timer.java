package models;

import play.jobs.Job;

public class Timer extends Job {

	private long answerID;

	public void doJob() {
		Answer answer = Answer.<Answer> findById(answerID);
		answer.bestAnswerSetter().canBeUndone(false);
		answer.question().save();
		answer.save();
	}

	public void answerID(long answerID) {
		this.answerID = answerID;
	}
}
