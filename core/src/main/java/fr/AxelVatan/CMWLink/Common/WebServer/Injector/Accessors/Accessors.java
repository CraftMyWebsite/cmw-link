package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.google.common.base.Joiner;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.ExactReflection;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyReflection;

public final class Accessors {

	public static final class SynchronizedFieldAccessor implements FieldAccessor {
		private final FieldAccessor accessor;
		private SynchronizedFieldAccessor(FieldAccessor accessor) {
			this.accessor = accessor;
		}

		@Override
		public void set(Object instance, Object value) {
			Object lock = accessor.get(instance);

			if (lock != null) {
				synchronized (lock) {
					accessor.set(instance, value);
				}
			} else {
				accessor.set(instance, value);
			}
		}

		@Override
		public Object get(Object instance) {
			return accessor.get(instance);
		}

		@Override
		public Field getField() {
			return accessor.getField();
		}
	}

	public static FieldAccessor getFieldAccessor(Class<?> instanceClass, Class<?> fieldClass, boolean forceAccess) {
		Field field = FuzzyReflection.fromClass(instanceClass, forceAccess).getFieldByType(null, fieldClass);
		return Accessors.getFieldAccessor(field);
	}

	public static FieldAccessor[] getFieldAccessorArray(Class<?> instanceClass, Class<?> fieldClass, boolean forceAccess) {
		List<Field> fields = FuzzyReflection.fromClass(instanceClass, forceAccess).getFieldListByType(fieldClass);
		FieldAccessor[] accessors = new FieldAccessor[fields.size()];

		for (int i = 0; i < accessors.length; i++) {
			accessors[i] = getFieldAccessor(fields.get(i));
		}
		return accessors;
	}

	public static FieldAccessor getFieldAccessor(Class<?> instanceClass, String fieldName, boolean forceAccess) {
		return Accessors.getFieldAccessor(ExactReflection.fromClass(instanceClass, true).getField(fieldName));
	}

	public static FieldAccessor getFieldAccessor(final Field field) {
		return Accessors.getFieldAccessor(field, true);
	}

	public static FieldAccessor getFieldAccessor(final Field field, boolean forceAccess) {
		field.setAccessible(true);
		return new DefaultFieldAccessor(field);
	}

	public static FieldAccessor getFieldAcccessorOrNull(Class<?> clazz, String fieldName, Class<?> fieldType) {
		try {
			FieldAccessor accessor = Accessors.getFieldAccessor(clazz, fieldName, true);
			if (fieldType.isAssignableFrom(accessor.getField().getType())) {
				return accessor;
			}
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static MethodAccessor getMethodAcccessorOrNull(Class<?> clazz, String methodName) {
		try {
			return Accessors.getMethodAccessor(clazz, methodName);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static ConstructorAccessor getConstructorAccessorOrNull(Class<?> clazz, Class<?>... parameters) {
		try {
			return Accessors.getConstructorAccessor(clazz, parameters);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static FieldAccessor getCached(final FieldAccessor inner) {
		return new FieldAccessor() {
			private final Object EMPTY = new Object();
			private volatile Object value = EMPTY;

			@Override
			public void set(Object instance, Object value) {
				inner.set(instance, value);
				update(value);
			}

			@Override
			public Object get(Object instance) {
				Object cache = value;

				if (cache != EMPTY)
					return cache;
				return update(inner.get(instance));
			}

			private Object update(Object value) {
				return this.value = value;
			}

			@Override
			public Field getField() {
				return inner.getField();
			}
		};
	}

	public static FieldAccessor getSynchronized(final FieldAccessor accessor) {
		if (accessor instanceof SynchronizedFieldAccessor){
			return accessor;
		}
		return new SynchronizedFieldAccessor(accessor);
	}

	public static MethodAccessor getConstantAccessor(final Object returnValue, final Method method) {
		return new MethodAccessor() {
			@Override
			public Object invoke(Object target, Object... args) {
				return returnValue;
			}

			@Override
			public Method getMethod() {
				return method;
			}
		};
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

	public static ConstructorAccessor getConstructorAccessor(Class<?> instanceClass, Class<?>... parameters) {
		try {
			return getConstructorAccessor(instanceClass.getDeclaredConstructor(parameters));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(String.format("Unable to find constructor %s(%s).", instanceClass, Joiner.on(",").join(parameters)));
		} catch (SecurityException e) {
			throw new IllegalStateException("Cannot access constructors.", e);
		}
	}

	public static ConstructorAccessor getConstructorAccessor(final Constructor<?> constructor) {
		return new DefaultConstrutorAccessor(constructor);
	}

	private Accessors() {
	}
}