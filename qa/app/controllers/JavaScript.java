package controllers;

import play.mvc.Controller;

/**
 * Provide functions for every controller
 */
public class JavaScript extends Controller {
	public static void controller() {
		int n = Questions.NUMBER_OF_LOADED_QUESTIONS;
		render("JavaScript/controller.js", n);
	}
}