import java.util.List;

import models.fraudpointscale.FraudPointRule;
import models.fraudpointscale.RuleLoader;
import models.fraudpointscale.TestRule;

import org.junit.Test;

import play.test.UnitTest;

public class RuleLoaderTest extends UnitTest {

	@Test
	public void containsTestRule() {
		List<Class<? extends FraudPointRule>> rules = RuleLoader.getRules();
		assertTrue(rules.size() > 0);

		// SM needs to be removed later
		assertTrue(rules.contains(TestRule.class));
	}

}
