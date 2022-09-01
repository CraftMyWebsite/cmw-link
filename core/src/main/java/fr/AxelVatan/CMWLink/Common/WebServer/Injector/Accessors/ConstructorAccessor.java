package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor {

	public Object invoke(Object... args);
	
	public Constructor<?> getConstructor();
}