package controllers;

import play.mvc.Controller;

/**
 * Provide functions for every controller
 */
public class JavaScript extends Controller {
	public static void controller() {
		render("JavaScript/controller.js");
	}
}