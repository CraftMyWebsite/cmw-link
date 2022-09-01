package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.ExactReflection;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.MethodInfo;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors.DefaultMethodAccessor;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors.MethodAccessor;

public class FuzzyReflection {

	private Class<?> source;
	private boolean forceAccess;

	public FuzzyReflection(Class<?> source, boolean forceAccess) {
		this.source = source;
		this.forceAccess = forceAccess;
	}

	public static FuzzyReflection fromClass(Class<?> source) {
		return fromClass(source, false);
	}

	public static FuzzyReflection fromClass(Class<?> source, boolean forceAccess) {
		return new FuzzyReflection(source, forceAccess);
	}

	public static FuzzyReflection fromObject(Object reference) {
		return new FuzzyReflection(reference.getClass(), false);
	}

	public static FuzzyReflection fromObject(Object reference, boolean forceAccess) {
		return new FuzzyReflection(reference.getClass(), forceAccess);
	}

	public Class<?> getSource() {
		return source;
	}

	public Object getSingleton() {	
		Method method = null;
		Field field = null;

		try {
			method = getMethod(
					FuzzyMethodContract.newBuilder().
					parameterCount(0).
					returnDerivedOf(source).
					requireModifier(Modifier.STATIC).
					build()
					);
		} catch (IllegalArgumentException e) {
			field = getFieldByType("instance", source);
		}
		if (method != null) {
			try {
				method.setAccessible(true);
				return method.invoke(null);
			} catch (Exception e) {
				throw new RuntimeException("Cannot invoke singleton method " + method, e);
			}
		}
		if (field != null) {
			try {
				field.setAccessible(true);
				return field.get(null);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot get content of singleton field " + field, e);
			}
		}
		throw new IllegalStateException("Impossible.");
	}

	public Method getMethod(AbstractFuzzyMatcher<MethodInfo> matcher) {
		List<Method> result = getMethodList(matcher);

		if (result.size() > 0)
			return result.get(0);
		else
			throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
	}

	public List<Method> getMethodList(AbstractFuzzyMatcher<MethodInfo> matcher) {
		List<Method> methods = Lists.newArrayList();
		for (Method method : getMethods()) {
			if (matcher.isMatch(MethodInfo.fromMethod(method), source)) {
				methods.add(method);
			}
		}
		return methods;
	}

	public Method getMethodByName(String nameRegex) {
		Pattern match = Pattern.compile(nameRegex);
		for (Method method : getMethods()) {
			if (match.matcher(method.getName()).matches()) {
				return method;
			}
		}

		throw new IllegalArgumentException("Unable to find a method with the pattern " + nameRegex + " in " + source.getName());
	}

	public Method getMethodByParameters(String name, Class<?>... args) {
		for (Method method : getMethods()) {
			if (Arrays.equals(method.getParameterTypes(), args)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
	}

	public Method getMethodByParameters(String name, Class<?> returnType, Class<?>[] args) {
		List<Method> methods = getMethodListByParameters(returnType, args);
		if (methods.size() > 0) {
			return methods.get(0);
		} else {
			throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
		}
	}

	public Method getMethodByParameters(String name, String returnTypeRegex, String[] argsRegex) {
		Pattern match = Pattern.compile(returnTypeRegex);
		Pattern[] argMatch = new Pattern[argsRegex.length];
		for (int i = 0; i < argsRegex.length; i++) {
			argMatch[i] = Pattern.compile(argsRegex[i]);
		}
		for (Method method : getMethods()) {
			if (match.matcher(method.getReturnType().getName()).matches()) {
				if (matchParameters(argMatch, method.getParameterTypes()))
					return method;
			}
		}
		throw new IllegalArgumentException("Unable to find " + name + " in " + source.getName());
	}

	public Object invokeMethod(Object target, String name, Class<?> returnType, Object... parameters) {
		Class<?>[] types = new Class<?>[parameters.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = parameters[i].getClass();
		}
		return getMethodAccessor(getMethodByParameters(name, returnType, types)).invoke(target, parameters);
	}

	public static MethodAccessor getMethodAccessor(Class<?> instanceClass, String methodName, Class<?>... parameters) {
		return new DefaultMethodAccessor(ExactReflection.fromClass(instanceClass, true).getMethod(methodName, parameters));
	}

	public static MethodAccessor getMethodAccessor(final Method method) {
		return getMethodAccessor(method, true);
	}

	public static MethodAccessor getMethodAccessor(final Method method, boolean forceAccess) {
		method.setAccessible(forceAccess);
		return new DefaultMethodAccessor(method);
	}

	private boolean matchParameters(Pattern[] parameterMatchers, Class<?>[] argTypes) {
		if (parameterMatchers.length != argTypes.length){
			throw new IllegalArgumentException("Arrays must have the same cardinality.");
		}
		for (int i = 0; i < argTypes.length; i++) {
			if (!parameterMatchers[i].matcher(argTypes[i].getName()).matches())
				return false;
		}

		return true;
	}

	public List<Method> getMethodListByParameters(Class<?> returnType, Class<?>[] args) {
		List<Method> methods = new ArrayList<Method>();
		for (Method method : getMethods()) {
			if (method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), args)) {
				methods.add(method);
			}
		}
		return methods;
	}

	public Field getFieldByName(String nameRegex) {
		Pattern match = Pattern.compile(nameRegex);
		for (Field field : getFields()) {
			if (match.matcher(field.getName()).matches()) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unable to find a field with the pattern " + nameRegex + " in " + source.getName());
	}

	public Field getFieldByType(String name, Class<?> type) {
		List<Field> fields = getFieldListByType(type);
		if (fields.size() > 0) {
			return fields.get(0);
		} else {
			throw new IllegalArgumentException(String.format("Unable to find a field %s with the type %s in %s",
					name, type.getName(), source.getName())
					);
		}
	}

	public List<Field> getFieldListByType(Class<?> type) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : getFields()) {
			if (type.isAssignableFrom(field.getType())) {
				fields.add(field);
			}
		}
		return fields;
	}

	public Field getField(AbstractFuzzyMatcher<Field> matcher) {
		List<Field> result = getFieldList(matcher);
		if (result.size() > 0){
			return result.get(0);
		}else{
			throw new IllegalArgumentException("Unable to find a field that matches " + matcher);
		}
	}

	public List<Field> getFieldList(AbstractFuzzyMatcher<Field> matcher) {
		List<Field> fields = Lists.newArrayList();
		for (Field field : getFields()) {
			if (matcher.isMatch(field, source)) {
				fields.add(field);
			}
		}
		return fields;
	}

	public Field getFieldByType(String typeRegex) {
		Pattern match = Pattern.compile(typeRegex);
		for (Field field : getFields()) {
			String name = field.getType().getName();

			if (match.matcher(name).matches()) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + source.getName());
	}

	public Field getFieldByType(String typeRegex, Set<Class<?>> ignored) {
		Pattern match = Pattern.compile(typeRegex);
		for (Field field : getFields()) {
			Class<?> type = field.getType();

			if (!ignored.contains(type) && match.matcher(type.getName()).matches()) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unable to find a field with the type " + typeRegex + " in " + source.getName());
	}

	public Constructor<?> getConstructor(AbstractFuzzyMatcher<MethodInfo> matcher) {
		List<Constructor<?>> result = getConstructorList(matcher);
		if (result.size() > 0){
			return result.get(0);
		}else{
			throw new IllegalArgumentException("Unable to find a method that matches " + matcher);
		}
	}

	public Map<String, Method> getMappedMethods(List<Method> methods) {
		Map<String, Method> map = Maps.newHashMap();

		for (Method method : methods) {
			map.put(method.getName(), method);
		}
		return map;
	}

	public List<Constructor<?>> getConstructorList(AbstractFuzzyMatcher<MethodInfo> matcher) {
		List<Constructor<?>> constructors = Lists.newArrayList();
		for (Constructor<?> constructor : getConstructors()) {
			if (matcher.isMatch(MethodInfo.fromConstructor(constructor), source)) {
				constructors.add(constructor);
			}
		}
		return constructors;
	}

	public Set<Field> getFields() {
		if (forceAccess){
			return setUnion(source.getDeclaredFields(), source.getFields());
		}else{
			return setUnion(source.getFields());
		}
	}

	public Set<Field> getDeclaredFields(Class<?> excludeClass) {
		if (forceAccess) {
			Class<?> current = source;
			Set<Field> fields = Sets.newLinkedHashSet();
			while (current != null && current != excludeClass) {
				fields.addAll(Arrays.asList(current.getDeclaredFields()));
				current = current.getSuperclass();
			}
			return fields;
		}
		return getFields();
	}

	public Set<Method> getMethods() {
		if (forceAccess){
			return setUnion(source.getDeclaredMethods(), source.getMethods());
		}else{
			return setUnion(source.getMethods());
		}
	}

	public Set<Constructor<?>> getConstructors() {
		if (forceAccess)
			return setUnion(source.getDeclaredConstructors());
		else
			return setUnion(source.getConstructors());
	}

	@SafeVarargs
	private static <T> Set<T> setUnion(T[]... array) {
		Set<T> result = new LinkedHashSet<T>();
		for (T[] elements : array) {
			for (T element : elements) {
				result.add(element);
			}
		}
		return result;
	}

	public boolean isForceAccess() {
		return forceAccess;
	}

	public void setForceAccess(boolean forceAccess) {
		this.forceAccess = forceAccess;
	}
}