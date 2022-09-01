package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors;

import java.lang.reflect.Method;

public interface MethodAccessor {

	public Object invoke(Object target, Object... args);
	
	public Method getMethod();
}