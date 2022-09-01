package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

public class ExactReflection {

	private Class<?> source;
	private boolean forceAccess;

	private ExactReflection(Class<?> source, boolean forceAccess) {
		this.source = Preconditions.checkNotNull(source, "source class cannot be NULL");
		this.forceAccess = forceAccess;
	}

	public static ExactReflection fromClass(Class<?> source) {
		return fromClass(source, false);
	}

	public static ExactReflection fromClass(Class<?> source, boolean forceAccess) {
		return new ExactReflection(source, forceAccess);
	}

	public static ExactReflection fromObject(Object reference) {
		return new ExactReflection(reference.getClass(), false);
	}

	public static ExactReflection fromObject(Object reference, boolean forceAccess) {
		return new ExactReflection(reference.getClass(), forceAccess);
	}

	public Method getMethod(String methodName, Class<?>... parameters) {
		return getMethod(source,  methodName, parameters);
	}

	private Method getMethod(Class<?> instanceClass, String methodName, Class<?>... parameters) {
		for (Method method : instanceClass.getDeclaredMethods()) {
			if ((forceAccess  		|| Modifier.isPublic(method.getModifiers())) &&
					(methodName == null || method.getName().equals(methodName)) && 
					Arrays.equals(method.getParameterTypes(), parameters)) {

				method.setAccessible(true);
				return method;
			}
		}
		if (instanceClass.getSuperclass() != null){
			return getMethod(instanceClass.getSuperclass(), methodName, parameters);
		}
		throw new IllegalArgumentException(String.format("Unable to find method %s (%s) in %s.", methodName, Arrays.asList(parameters), source));
	}

	public Field getField(String fieldName) {
		return getField(source, fieldName);
	}

	private Field getField(Class<?> instanceClass, @Nonnull String fieldName) {
		for (Field field : instanceClass.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}
		if (instanceClass.getSuperclass() != null){
			return getField(instanceClass.getSuperclass(), fieldName);
		}
		throw new IllegalArgumentException(String.format("Unable to find field %s in %s.", fieldName, source));
	}

	public ExactReflection forceAccess() {
		return new ExactReflection(source, true);
	}

	public boolean isForceAccess() {
		return forceAccess;
	}

	public Class<?> getSource() {
		return source;
	}
}