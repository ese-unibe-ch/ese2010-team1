import java.util.Date;

import models.TimeFreezer;

import org.junit.Test;

import play.test.UnitTest;

public class TimeFreezerTest extends UnitTest {

	@Test
	public void createTimeFreezer() {
		TimeFreezer tf = new TimeFreezer(10);
		assertTrue((new Date()).compareTo(tf.timestamp) >= 0);
		assertFalse(tf.frozen());
	}

	@Test
	public void freeze() {
		TimeFreezer tf = new TimeFreezer(-1);
		assertTrue(tf.frozen());
	}

}
