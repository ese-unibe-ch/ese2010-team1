import java.util.List;

import models.ProfileEntry;
import models.ProfileItem;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ProfileTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void createProfilItem() {

		User user = new User("testuser", "test@mail.com", "password").save();

		assertEquals(0, ProfileItem.count());
		ProfileItem item1 = new ProfileItem("Test1").save();
		ProfileItem item2 = new ProfileItem("Test2").save();

		assertEquals(2, ProfileItem.count());

		item2.editUserEntry(user, "my entry 1");
		assertEquals(1, ProfileEntry.count());
		// assertNotNull(item2.findUserEntry(user));

		assertEquals(1, ProfileEntry.count());
		List<ProfileEntry> entrys = ProfileEntry.findAll();
		assertTrue(entrys.size() > 0);

		System.out.println(entrys.get(0).user.name());

	}
}
