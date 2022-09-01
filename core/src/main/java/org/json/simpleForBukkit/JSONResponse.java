package org.json.simpleForBukkit;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JSONResponse {

	String tag = "";
	String methodName;
	String username;
	String key;
	JSONArray arguments;
	JSONAPIAuthResponse auth;
	boolean stream = false;
	boolean showOlder = false;

	JSONObject error = null;

	public JSONResponse(JSONObject req, boolean stream) {
		this.stream = stream;
		if(req.containsKey("tag")) {
			tag = req.get("tag").toString();
		}

		methodName = req.get("name").toString();

		if(!req.containsKey("username")) {
			error = APIError("Missing username from payload", 10);
			return;
		}

		username = req.get("username").toString();
		key = req.get("key").toString();

		if(req.containsKey("show_previous")) {
			showOlder = Boolean.valueOf(req.get("show_previous").toString());
		}

		Object args = req.get("arguments");
		if(args != null && args instanceof JSONArray) {
			arguments = (JSONArray) args;
		}
	}

	public JSONObject getJSONObject() {
		if(error != null) {
			return error;
		}
		return serveAPICall(arguments);
	}

	public JSONObject serveAPICall(Object args) {
		try {
			if (!(args instanceof JSONArray)) {
				args = new JSONArray();
			}
			//Object result = caller.call(methodName, (Object[]) ((ArrayList<Object>) args).toArray(new Object[((ArrayList<Object>) args).size()]));
			return APISuccess("success TOOKK");
		} catch (Exception e) {
			return APIError(e.getMessage(), 4);
		}
	}

	public JSONObject APIException(Throwable e, int errorCode) {
		JSONObject r = new JSONObject();
		r.put("result", "error");
		r.put("is_success", false);
		StringWriter pw = new StringWriter();
		e.printStackTrace(new PrintWriter(pw));
		e.printStackTrace();
		r.put("source", methodName);

		JSONObject err_obj = new JSONObject();
		err_obj.put("message", "Caught exception: " + pw.toString().replaceAll("\\n", "\n").replaceAll("\\r", "\r"));
		err_obj.put("code", errorCode);

		r.put("error", err_obj);

		if(!tag.equals("")) {
			r.put("tag", tag);
		}

		return r;
	}

	public JSONObject APIError(String error, int errorCode) {
		return APIError(error, errorCode, methodName, tag);
	}

	public static JSONObject APIError(String error, int errorCode, String methodName, String tag) {
		JSONObject r = new JSONObject();
		r.put("result", "error");
		r.put("source", methodName);
		r.put("is_success", false);

		JSONObject err_obj = new JSONObject();
		err_obj.put("message", error);
		err_obj.put("code", errorCode);

		r.put("error", err_obj);

		if(!tag.equals("")) {
			r.put("tag", tag);
		}

		return r;
	}

	public JSONObject APISuccess(Object result) {
		JSONObject r = new JSONObject();
		r.put("result", "success");
		r.put("is_success", true);
		if(methodName != null) r.put("source", methodName);
		r.put("success", result);

		if(!tag.equals("")) {
			r.put("tag", tag);
		}

		return r;
	}

	public String getTag() {
		return tag;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getUsername() {
		return username;
	}

	public String getKey() {
		return key;
	}

	public JSONArray getArguments() {
		return arguments;
	}

	public boolean isStream() {
		return stream;
	}

	public boolean isShowOlder() {
		return showOlder;
	}

	public JSONAPIAuthResponse auth() {
		return this.auth;
	}
}
