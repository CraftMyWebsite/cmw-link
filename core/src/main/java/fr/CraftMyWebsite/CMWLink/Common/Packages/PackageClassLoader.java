package fr.CraftMyWebsite.CMWLink.Common.Packages;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PackageClassLoader extends URLClassLoader {

	private final Map<String, Class<?>>	classes	= new HashMap<String, Class<?>>();
	private Packages mainClass;
	
	@SuppressWarnings("deprecation")
	public PackageClassLoader(final ClassLoader parent, final String main, final URL url, Packages mainClass) throws MalformedURLException {
		super(new URL[] { url }, parent);
		this.mainClass = mainClass;
		try {
			Class<?> jarClass = null;
			try {
				jarClass = Class.forName(main, true, this);
			}
			catch (final ClassNotFoundException ex) {
				ex.printStackTrace();
			}

			Class<? extends CMWLPackage> pluginClass = null;
			try {
				pluginClass = jarClass.asSubclass(CMWLPackage.class);
			}
			catch (final ClassCastException ex) {
				ex.printStackTrace();
			}
			pluginClass.newInstance();
		}
		catch (final IllegalAccessException ex) {
			ex.printStackTrace();
		}
		catch (final InstantiationException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		return findClass(name, true);
	}

	public Class<?> findClass(final String name, final boolean checkGlobal) throws ClassNotFoundException {
		if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
			throw new ClassNotFoundException(name);
		}
		Class<?> result = classes.get(name);
		if (result == null) {
			if (checkGlobal) {
				result = mainClass.getClassByName(name);
			}
			if (result == null) {
				result = super.findClass(name);
				if (result != null) {
					setClass(name, result);
				}
			}
			classes.put(name, result);
		}
		return result;
	}
	
	public void setClass(final String name, final Class<?> clazz) {
		if (!classes.containsKey(name)) {
			classes.put(name, clazz);
		}
	}
	
	public Set<String> getClasses() {
		return classes.keySet();
	}
}