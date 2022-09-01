package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.lang.reflect.Field;

import com.google.common.base.Objects;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors.Accessors;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Accessors.FieldAccessor;

public class VolatileField {
	
	private FieldAccessor accessor;
	private Object container;
	private Object previous;
	private Object current;
	private boolean previousLoaded;
	private boolean currentSet;
	
	public VolatileField(Field field, Object container) {
		this.accessor = Accessors.getFieldAccessor(field);
		this.container = container;
	}
	
	public VolatileField(FieldAccessor accessor, Object container) {
		this.accessor = accessor;
		this.container = container;
	}
	
	public Field getField() {
		return accessor.getField();
	}
	
	public Object getValue() {
		if (!currentSet) {
			ensureLoaded();
			return previous;
		} else {
			return current;
		}
	}
	
	public Object getOldValue() {
		ensureLoaded();
		return previous;
	}
	
	public void setValue(Object newValue) {
		ensureLoaded();
		writeFieldValue(newValue);
		current = newValue;
		currentSet = true;
	}

	public void refreshValue() {
		Object fieldValue = readFieldValue();
		if (currentSet) {
			if (!Objects.equal(current, fieldValue)) {
				previous = readFieldValue();
				previousLoaded = true;
				writeFieldValue(current);
			}
		} else if (previousLoaded) {
			previous = fieldValue;
		}
	}
	
	public void saveValue() {
		previous = current;
		currentSet = false;
	}
	
	public void revertValue() {
		if (currentSet) {
			if (getValue() == current) {
				setValue(previous);
				currentSet = false;
			}
		}
	}
	
	public VolatileField toSynchronized() {
		return new VolatileField(Accessors.getSynchronized(accessor), container);
	}
	
	private void ensureLoaded() {
		if (!previousLoaded) {
			previous = readFieldValue();
			previousLoaded = true;
		}
	}
	
	private Object readFieldValue() {
		return accessor.get(container);
	}
	
	private void writeFieldValue(Object newValue) {
		accessor.set(container, newValue);
	}
		
	@Override
	protected void finalize() throws Throwable {
		revertValue();
	}
}