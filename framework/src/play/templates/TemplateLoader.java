package play.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.Play;
import play.vfs.VirtualFile;
import play.exceptions.TemplateCompilationException;
import play.exceptions.TemplateNotFoundException;

/**
 * Load templates
 */
public class TemplateLoader {

    protected static Map<String, Template> templates = new HashMap<String, Template>();

    /**
     * Load a template from a virtual file
     * @param file A VirtualFile
     * @return The executable template
     */
    public static Template load(VirtualFile file) {
        String key = (file.relativePath().hashCode()+"").replace("-", "M");
        if (!templates.containsKey(key) || templates.get(key).compiledTemplate == null) {
            if(Play.usePrecompiled) {
                Template template = new Template(file.relativePath().replaceAll("\\{(.*)\\}", "from_$1").replace(":", "_").replace("..", "parent"), file.contentAsString());
                template.loadPrecompiled();
                templates.put(key, template);
                return template;
            }
            Template template = new Template(file.relativePath(), file.contentAsString());
            if(template.loadFromCache()) {
                templates.put(key, template);
            } else {
                templates.put(key, TemplateCompiler.compile(file));
            }
        } else {
            Template template = templates.get(key);
            if (Play.mode == Play.Mode.DEV && template.timestamp < file.lastModified()) {
                templates.put(key, TemplateCompiler.compile(file));
            }
        }
        if (templates.get(key) == null) {
            throw new TemplateNotFoundException(file.relativePath());
        }
        return templates.get(key);
    }

	/**
     * Load a template from a String
     * @param key A unique identifier for the template, used for retreiving a cached template
     * @param source The template source, leave as null, if you want to get the cached template
     * @return A Template
     */
    public static Template load(String key, String source) {
        if (!templates.containsKey(key) || templates.get(key).compiledTemplate == null) {
            Template template = new Template(key, source);
            if(template.loadFromCache()) {
                templates.put(key, template);
            } else {
                templates.put(key, TemplateCompiler.compile(key, source));
            }
        } else {
            if (Play.mode == Play.Mode.DEV) {
                templates.put(key, TemplateCompiler.compile(key, source));
            }
        }
        if (templates.get(key) == null) {
            throw new TemplateNotFoundException(key);
        }
        return templates.get(key);
    }

    public static void cleanCompiledCache() {
        // nothing to do in this version
    }

    /**
     * Load a template
     * @param path The path of the template (ex: Application/index.html)
     * @return The executable template
     */
    public static Template load(String path) {
        Template template = null;
        for (VirtualFile vf : Play.templatesPath) {
            if(vf == null) {
                continue;
            }
            VirtualFile tf = vf.child(path);
            if (tf.exists()) {
                template = TemplateLoader.load(tf);
                break;
            }
        }
		if (template == null) {
            template = templates.get(path);
        }
        //TODO: remove ?
        if (template == null) {
            VirtualFile tf = Play.getVirtualFile(path);
            if (tf != null && tf.exists()) {
                template = TemplateLoader.load(tf);
            } else {
                throw new TemplateNotFoundException(path);
            }
        }
        return template;
    }

    /**
     * List all found templates
     * @return A list of executable templates
     */
    public static List<Template> getAllTemplate() {
        List<Template> res = new ArrayList<Template>();
        for (VirtualFile virtualFile : Play.templatesPath) {
            scan(res, virtualFile);
        }
        for(VirtualFile root : Play.roots) {
            VirtualFile vf = root.child("conf/routes");
            if(vf != null && vf.exists()) {
                Template template = load(vf);
                template.compile();
            }
        }
        return res;
    }

    private static void scan(List<Template> templates, VirtualFile current) {
        if (!current.isDirectory()) {
            long start = System.currentTimeMillis();
            Template template = load(current);
            try {
                template.compile();
                Logger.trace("%sms to load %s", System.currentTimeMillis() - start, current.getName());
            } catch (TemplateCompilationException e) {
                Logger.error("Template %s does not compile at line %d", e.getTemplate().name, e.getLineNumber());
                throw e;
            }
            templates.add(template);
        } else if (!current.getName().startsWith(".")) {
            for (VirtualFile virtualFile : current.list()) {
                scan(templates, virtualFile);
            }
        }
    }
}
