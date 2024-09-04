package fr.CraftMyWebsite.CMWLink.Common.Config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

	private JsonObject jsObj = new JsonObject();
	
	public JsonBuilder() {
	}
	
	public JsonBuilder(String key, String value) {
		jsObj.addProperty(key, value);
	}
	
	public JsonBuilder(String key, Number value) {
		jsObj.addProperty(key, value);
	}
	
	public JsonBuilder(String key, boolean value) {
		jsObj.addProperty(key, value);
	}
	
	public JsonBuilder(String key, JsonElement value) {
		jsObj.add(key, value);
	}
	
	public JsonBuilder append(String key, String value) {
		jsObj.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder append(String key, Number value) {
		jsObj.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder append(String key, boolean value) {
		jsObj.addProperty(key, value);
		return this;
	}
	
	public JsonBuilder append(String key, JsonElement value) {
		jsObj.add(key, value);
		return this;
	}
	
	public String build(){
		return jsObj.toString();
	}
}
