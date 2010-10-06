package controllers;

import java.util.*;
import models.*;
import play.*;
import play.mvc.*;

public class Application extends Controller {

	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.get(Security.connected());
            renderArgs.put("user", user);
        }
    }
	
    public static void index() {
    	List<Question> questions = Question.questions();
        render(questions);
    }
    
    public static void question(int id) {
    	Question question = Question.get(id);
    	if(question == null) {
    		render();
    	} else {
    		List<Answer> answers = question.answers();
    		render(question, answers);
    	}
    }
    


}	