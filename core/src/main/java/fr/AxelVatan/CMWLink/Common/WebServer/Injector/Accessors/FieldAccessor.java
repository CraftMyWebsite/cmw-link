package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors;

import java.lang.reflect.Field;

public interface FieldAccessor {

	public Object get(Object instance);
	
	public void set(Object instance, Object value);
	
	public Field getField();
}