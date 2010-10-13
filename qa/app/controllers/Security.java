package controllers;

import models.User;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		if (User.connect(username, password) != null)
			return true;
		else
			return false;
	}

	static void onDisconnected() {
		Application.index();
	}
}