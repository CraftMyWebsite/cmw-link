package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

final class CachedPackage {

	private final String packageName;
	private final ClassSource source;
	private final Map<String, Optional<Class<?>>> cache;

	public CachedPackage(String packageName, ClassSource source) {
		this.source = source;
		this.packageName = packageName;
		this.cache = new ConcurrentHashMap<>();
	}

	public static String combine(String packageName, String className) {
		if (packageName == null || packageName.isEmpty()) {
			return className;
		} else {
			return packageName + "." + className;
		}
	}

	public void setPackageClass(String className, Class<?> clazz) {
		if (clazz != null) {
			this.cache.put(className, Optional.of(clazz));
		} else {
			this.cache.remove(className);
		}
	}

	public Optional<Class<?>> getPackageClass(final String className) {
		return this.cache.computeIfAbsent(className, x -> {
			try {
				return Optional.ofNullable(this.source.loadClass(combine(this.packageName, className)));
			} catch (ClassNotFoundException ex) {
				return Optional.empty();
			}
		});
	}
}