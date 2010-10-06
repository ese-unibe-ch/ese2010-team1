import static org.junit.Assert.*;
import org.junit.Test;
import models.*;
import play.test.*;


public class UserTest extends UnitTest {

	@Test
	public void shouldCreateUser() {
		User user = new User("Jack");
		assertTrue(user != null);
	}
	
	@Test
	public void shouldBeCalledJack() {
		User user = new User("Jack");
		assertEquals(user.name(), "Jack");
	}

}
