package models.fraudpointscale;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * The Class RuleLoader.
 */
public class RuleLoader {

	/**
	 * Gets the rule instances.
	 * 
	 * @return the rule instances
	 */
	public static List<FraudPointRule> getRuleInstances() {
		List<Class<? extends FraudPointRule>> rules = getRules();
		List<FraudPointRule> instances = new ArrayList();
		for (Class<? extends FraudPointRule> rule : rules) {
			try {
				instances.add(rule.newInstance());
			} catch (Exception e) {
				continue;
			}
		}
		return instances;
	}

	/**
	 * Gets the rules.
	 * 
	 * @return the rules
	 */
	public static List<Class<? extends FraudPointRule>> getRules() {
		List<Class> classes = getClasses("models.fraudpointscale");
		List<Class<? extends FraudPointRule>> rules = new ArrayList();
		for (Class c : classes) {
			if (extendsClass(c, FraudPointRule.class))
				rules.add(c);
		}
		return rules;
	}

	/**
	 * Extends class.
	 * 
	 * @param c
	 *            the c
	 * @param superC
	 *            the super c
	 * @return true, if successful
	 */
	private static boolean extendsClass(Class c, Class superC) {
		if (Modifier.isAbstract(c.getModifiers()))
			return false;
		while (c != Object.class) {
			if (c.equals(superC))
				return true;
			c = c.getSuperclass();
		}
		return false;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 */
	private static List<Class> getClasses(String packageName) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		} catch (IOException e) {
			return null;
		}
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 */
	private static List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "."
						+ file.getName()));
			} else if (file.getName().endsWith(".java")) {
				try {
					classes.add(Class.forName(packageName
							+ '.'
							+ file.getName().substring(0,
									file.getName().length() - 5)));
				} catch (ClassNotFoundException e) {
				}
			}
		}
		return classes;
	}
}
