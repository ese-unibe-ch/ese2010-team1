package controllers;

import models.User;

public class Security extends Secure.Security {

	/**
	 * Check the user for login.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return true, if successful
	 */
	static boolean authenticate(String username, String password) {
		User user = User.connect(username, password);
		if (user != null && user.isActive)
			return true;
		else
			return false;
	}

	/**
	 * Switch to the home screen after logout.
	 */
	static void onDisconnected() {
		Questions.home();
	}

	static boolean check(String profile) {
		if ("isAdmin".equals(profile)) {
			return User.find("byName", connected()).<User> first().isAdmin;
		}
		return false;
	}

}