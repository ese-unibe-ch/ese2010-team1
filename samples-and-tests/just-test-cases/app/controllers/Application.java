package controllers;

import java.util.*;
import java.io.File;
import java.util.concurrent.Future;

import play.*;
import play.mvc.*;
import play.libs.*;

import play.modules.spring.*;

import models.*;
import utils.*;

import javax.mail.internet.InternetAddress;

public class Application extends Controller {

    // bug

    public static void aa() {
        try {
            @SuppressWarnings("unused")
            int test = 1;
        } catch (Exception ex) {
        }
    }

    // bug

    public static void aaa() {
        try {
            boolean test = TestUtil.invokeTest("a");
            Logger.info("test:" + test);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        renderText("IT WORKS");
    }

    public static void index() {
        render();
    }

    public static void index2() {
        renderText(Router.reverse("Application.index2"));
    }

    public static void simpleStatusCode() {
        response.status = 204;
    }

    public static void hello(String name) {
        render(name);
    }

    public static void yop() {
        render();
    }

    public static void dynamicClassBinding(boolean fail) {
        render(fail);
    }

    public static void tagContexts() {
        render();
    }

    public static void tags() {
        render();
    }
    
    public static void escapeData() {
        String oops = "&nbsp;<i>Yop <!-- Coucou --></i>&nbsp;";
        render(oops);
    }

    public static void aGetForm(String name) {
        render("Application/hello.html", name);
    }

    public static void aGetForm2(String name) {
        name = "2" + name;
        render("Application/hello.html", name);
    }

    public static void optional() {
        renderText("OK");
    }

    public static void reverserouting() {
        render("Application/reverse.html");
    }
    
    public static void reverserouting2() {
        render("Application/reverse2.html");
    }
    
    public static void reverserouting3() {
        Object def = reverse(); {
            JPABinding.save(new Project("COLCOZ"));
        }
        renderText(def);
    }

    public static void mail() {
        notifiers.Welcome.welcome();
        renderText("OK");
    }

    public static void mail2() {
        Welcome.welcome();
        renderText("OK2");
    }

    public static void mail3() {
        notifiers.Welcome.welcome2();
        renderText("OK3");
    }

    public static void mail4() {
        notifiers.Welcome.welcome3();
        renderText("OK4");
    }

    /**
     * This is to make sure that there is no endless recursion
     */
    public static void mail5() throws Exception {
        Mail.send("from@toto.com", "replyTo@toto.com", "subject", "body");
        Mail.send("from@toto.com", "replyTo@toto.com", "subject", "body", "alternate");
        Mail.send("from@toto.com", "replyTo@toto.com", "subject", "body", "alternate", new File[0]);
        Mail.send("from@toto.com", new String[]{"recipient@toto.com"}, "subject", "body");
        Mail.send("from@toto.com", "replyTo@toto.com", "subject", "subject", Play.getFile("test/fond1.jpg"));
        Mail.send("from@toto.com", new String[]{"recipient@toto.com"}, "subject", "body", Play.getFile("test/fond1.jpg"));
        Mail.send(new InternetAddress("from@toto.com"), null, new String[]{"recipient@toto.com"}, "subject", "body", null, null,  Play.getFile("test/fond1.jpg"), Play.getFile("test/fond1.jpg"));
        Mail.send("from@toto.com", "replyTo@toto.com", new String[]{"recipient@toto.com"}, "subject", "body", "alternate", "text/html", new File[0]);
        Mail.sendEmail(new InternetAddress("from@toto.com"), new InternetAddress("replyTo@toto.com"), new InternetAddress[]{new InternetAddress("recipient@toto.com")}, "subject", "body", "alternate", "text/html", null, null, (Object[])new File[0]);
        renderText("OK5");
    }

    public static void ifthenelse() {
        boolean a = true;
        boolean b = false;
        String c = "";
        String d = "Yop";
        int e = 0;
        int f = 5;
        Boolean g = null;
        Boolean h = true;
        Object i = null;
        Object j = new Object();
        render(a, b, c, d, e, f, g, h, i, j);
    }

    public static void listTag() {
        List<String> a = new ArrayList<String>();
        a.add("aa");
        a.add("ab");
        a.add("ac");
        int[] b = new int[]{0, 1, 2, 3};
        Iterator d = a.iterator();
        render(a, b, d);
    }

    public static void a() {
        render();
    }
    
    public static void googleSearch(String word) {
        WS.HttpResponse response = WS.url("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=%s", word).get();
        long results = response.getJson().getAsJsonObject().getAsJsonObject("responseData").getAsJsonObject("cursor").getAsJsonPrimitive("estimatedResultCount").getAsLong();
        renderText(results);
    }
    
    private static void toList(String str) {
        str = "YOP";
        int i = 0;
        str.charAt(i++);
    }

}
