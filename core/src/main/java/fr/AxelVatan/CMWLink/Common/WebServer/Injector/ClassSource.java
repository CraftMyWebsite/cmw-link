package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.util.Collections;
import java.util.Map;

@FunctionalInterface
public interface ClassSource {

	static ClassSource fromClassLoader() {
		return fromClassLoader(ClassSource.class.getClassLoader());
	}

	static ClassSource fromPackage(String packageName) {
		return fromClassLoader().usingPackage(packageName);
	}

	static ClassSource fromClassLoader(final ClassLoader loader) {
		return loader::loadClass;
	}

	static ClassSource fromMap(final Map<String, Class<?>> map) {
		return canonicalName -> {
			Class<?> loaded = map == null ? null : map.get(canonicalName);
			if (loaded == null) {
				throw new ClassNotFoundException("The specified class could not be found by this ClassLoader.");
			}

			return loaded;
		};
	}

	static ClassSource empty() {
		return fromMap(Collections.emptyMap());
	}

	static String append(String a, String b) {
		boolean left = a.endsWith(".");
		boolean right = b.endsWith(".");
		if (left && right) {
			return a.substring(0, a.length() - 1) + b;
		} else if (left != right) {
			return a + b;
		} else {
			return a + "." + b;
		}
	}

	default ClassSource retry(final ClassSource other) {
		return canonicalName -> {
			try {
				return ClassSource.this.loadClass(canonicalName);
			} catch (ClassNotFoundException e) {
				return other.loadClass(canonicalName);
			}
		};
	}

	default ClassSource usingPackage(final String packageName) {
		return canonicalName -> this.loadClass(append(packageName, canonicalName));
	}

	Class<?> loadClass(String canonicalName) throws ClassNotFoundException;
}