package models;

import jobs.Timer;
import play.db.jpa.Model;
//OT I'm not really sure, if this class is even necessary, but let's discuss this in the next meeting!
public class BestAnswerSetter extends Model {

	private long answerID;
	private boolean canBeUndone = true;
	private int delay = 10;

	public BestAnswerSetter(long answerID) {

		this.answerID = answerID;
		Answer answer = Answer.<Answer> findById(answerID);
		answer.bestAnswerSetter(this);
		answer.isBestAnswer(true);
		answer.question().setBestAnswerFlag(true);
		answer.save();
		answer.question().save();
		Timer timer = new Timer();
		timer.answerID(answerID);
		timer.in(delay);
	}

	public Answer answer() {
		return Answer.<Answer> findById(answerID);
	}

	public boolean canBeUndone() {
		return canBeUndone;
	}

	public void undo() {
		assert (canBeUndone == true);
		Answer answer = Answer.<Answer> findById(answerID);
		answer.bestAnswerSetter(null);
		answer.isBestAnswer(false);
		answer.question().setBestAnswerFlag(false);
		answer.save();
		answer.question().save();
	}

	public void canBeUndone(boolean b) {
		canBeUndone = b;
	}

	public void delay(int sec) {
		delay = sec;
	}

}
