package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public abstract class MethodInfo implements GenericDeclaration, Member {

	public static MethodInfo fromMethod(final Method method) {
		return new MethodInfo() {
			@Override
			public String getName() {
				return method.getName();
			}
			@Override
			public Class<?>[] getParameterTypes() {
				return method.getParameterTypes();
			}
			@Override
			public Class<?> getDeclaringClass() {
				return method.getDeclaringClass();
			}
			@Override
			public Class<?> getReturnType() {
				return method.getReturnType();
			}
			@Override
			public int getModifiers() {
				return method.getModifiers();
			}
			@Override
			public Class<?>[] getExceptionTypes() {
				return method.getExceptionTypes();
			}
			@Override
			public TypeVariable<?>[] getTypeParameters() {
				return method.getTypeParameters();
			}
			@Override
			public String toGenericString() {
				return method.toGenericString();
			}
			@Override
			public String toString() {
				return method.toString();
			}
			@Override
			public boolean isSynthetic() {
				return method.isSynthetic();
			}
			@Override
			public int hashCode() {
				return method.hashCode();
			}
			@Override
			public boolean isConstructor() {
				return false;
			}
			@Override
			public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
				return method.getAnnotation(annotationClass);
			}
			@Override
			public Annotation[] getAnnotations() {
				return method.getAnnotations();
			}
			@Override
			public Annotation[] getDeclaredAnnotations() {
				return method.getDeclaredAnnotations();
			}
		};
	}

	public static Collection<MethodInfo> fromMethods(Method[] methods) {
		return fromMethods(Arrays.asList(methods));
	}

	public static List<MethodInfo> fromMethods(Collection<Method> methods) {
		List<MethodInfo> infos = Lists.newArrayList();

		for (Method method : methods)
			infos.add(fromMethod(method));
		return infos;
	}

	public static MethodInfo fromConstructor(final Constructor<?> constructor) {
		return new MethodInfo() {
			@Override
			public String getName() {
				return constructor.getName();
			}
			@Override
			public Class<?>[] getParameterTypes() {
				return constructor.getParameterTypes();
			}
			@Override
			public Class<?> getDeclaringClass() {
				return constructor.getDeclaringClass();
			}
			@Override
			public Class<?> getReturnType() {
				return Void.class;
			}
			@Override
			public int getModifiers() {
				return constructor.getModifiers();
			}
			@Override
			public Class<?>[] getExceptionTypes() {
				return constructor.getExceptionTypes();
			}
			@Override
			public TypeVariable<?>[] getTypeParameters() {
				return constructor.getTypeParameters();
			}
			@Override
			public String toGenericString() {
				return constructor.toGenericString();
			}
			@Override
			public String toString() {
				return constructor.toString();
			}
			@Override
			public boolean isSynthetic() {
				return constructor.isSynthetic();
			}
			@Override
			public int hashCode() {
				return constructor.hashCode();
			}
			@Override
			public boolean isConstructor() {
				return true;
			}
			@Override
			public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
				return constructor.getAnnotation(annotationClass);
			}
			@Override
			public Annotation[] getAnnotations() {
				return constructor.getAnnotations();
			}
			@Override
			public Annotation[] getDeclaredAnnotations() {
				return constructor.getDeclaredAnnotations();
			}
		};
	}

	public static Collection<MethodInfo> fromConstructors(Constructor<?>[] constructors) {
		return fromConstructors(Arrays.asList(constructors));
	}

	public static List<MethodInfo> fromConstructors(Collection<Constructor<?>> constructors) {
		List<MethodInfo> infos = Lists.newArrayList();

		for (Constructor<?> constructor : constructors)
			infos.add(fromConstructor(constructor));
		return infos;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	public abstract String toGenericString();

	public abstract Class<?>[] getExceptionTypes();

	public abstract Class<?> getReturnType();

	public abstract Class<?>[] getParameterTypes();

	public abstract boolean isConstructor();
}