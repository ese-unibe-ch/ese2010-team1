package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.vfs.*;

import java.io.*;
import java.util.*;

public class PlayDocumentation extends Controller {
    
    public static void index() throws Exception {
        page("home", null);
    }
    
    public static void page(String id, String module) throws Exception {
        File page = new File(Play.frameworkPath, "documentation/manual/"+id+".textile");
        if(module != null) {
             page = new File(Play.modules.get(module).getRealFile(), "documentation/manual/"+id+".textile");
        }
        if(!page.exists()) {
            notFound("Manual page for "+id+" not found");
        }
        String textile = IO.readContentAsString(page);
        String html = toHTML(textile);
        String title = getTitle(textile);
        
        List<String> modules = new ArrayList();
        if(id.equals("home") && module == null) {
            for(String key : Play.modules.keySet()) {
                VirtualFile mr = Play.modules.get(key);
                VirtualFile home = mr.child("documentation/manual/home.textile");
                if(home.exists()) {
                    modules.add(key);
                }
            }
        }
        
        render(id, html, title, modules, module);
    }
    
    public static void image(String name, String module) {
        File image = new File(Play.frameworkPath, "documentation/images/"+name+".png");
        if(module != null) {
             image = new File(Play.modules.get(module).getRealFile(),"documentation/images/"+name+".png");
        }
        if(!image.exists()) {
            notFound();
        }
        renderBinary(image);
    }
    
    public static void file(String name) {
        File file = new File(Play.frameworkPath, "documentation/files/"+name);
        if(!file.exists()) {
            notFound();
        }
        renderBinary(file);
    }    
    
    //
    
    static String toHTML(String textile) {
        String html = new org.eclipse.mylyn.wikitext.core.parser.MarkupParser(new org.eclipse.mylyn.wikitext.textile.core.TextileLanguage()).parseToHtml(textile);
        html = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
        return html;
    }
    
    static String getTitle(String textile) {
        if(textile.length() == 0) {
            return "";
        }
        return textile.split("\n")[0].substring(3).trim();
    }
    
}