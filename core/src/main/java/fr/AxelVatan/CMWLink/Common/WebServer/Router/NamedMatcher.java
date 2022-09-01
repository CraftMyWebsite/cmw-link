package fr.AxelVatan.CMWLink.Common.WebServer.Router;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class NamedMatcher implements NamedMatchResult {

	private Matcher matcher;
	private NamedPattern parentPattern;

	NamedMatcher() {
	}

	NamedMatcher(NamedPattern parentPattern, MatchResult matcher) {
		this.parentPattern = parentPattern;
		this.matcher = (Matcher) matcher;
	}

	NamedMatcher(NamedPattern parentPattern, CharSequence input) {
		this.parentPattern = parentPattern;
		this.matcher = parentPattern.pattern().matcher(input);
	}

	public boolean matches() {
		return matcher.matches();
	}

	public String group() {
		return matcher.group();
	}

	public String group(int group) {
		return matcher.group(group);
	}

	public int groupCount() {
		return matcher.groupCount();
	}

	public List<String> orderedGroups() {
		ArrayList<String> groups = new ArrayList<String>();
		for (int i = 1; i <= groupCount(); i++) {
			groups.add(group(i));
		}
		return groups;
	}

	public String group(String groupName) {
		return group(groupIndex(groupName));
	}

	public Map<String, String> namedGroups() {
		Map<String, String> result = new LinkedHashMap<String, String>();

		for (int i = 1; i <= groupCount(); i++) {
			String groupName = parentPattern.getGroupNames().get(i-1);
			String groupValue = matcher.group(i);
			result.put(groupName, groupValue);
		}

		return result;
	}

	private int groupIndex(String groupName) {
		return parentPattern.getGroupNames().indexOf(groupName) + 1;
	}

	public int start() {
		return matcher.start();
	}

	public int start(int group) {
		return matcher.start(group);
	}

	public int start(String groupName) {
		return start(groupIndex(groupName));
	}

	public int end() {
		return matcher.end();
	}

	public int end(int group) {
		return matcher.end(group);
	}

	public int end(String groupName) {
		return end(groupIndex(groupName));
	}

	public NamedMatcher region(int start, int end) {
		matcher.region(start, end);
		return this;
	}

	public int regionEnd() {
		return matcher.regionEnd();
	}

	public int regionStart() {
		return matcher.regionStart();
	}

	public boolean hitEnd() {
		return matcher.hitEnd();
	}

	public boolean requireEnd() {
		return matcher.requireEnd();
	}

	public boolean hasAnchoringBounds() {
		return matcher.hasAnchoringBounds();
	}

	public boolean hasTransparentBounds() {
		return matcher.hasTransparentBounds();
	}

	public String replaceAll(String replacement) {
		return matcher.replaceAll(replacement);
	}

	public String replaceFirst(String replacement) {
		return matcher.replaceFirst(replacement);
	}

	public NamedMatcher useAnchoringBounds(boolean b) {
		matcher.useAnchoringBounds(b);
		return this;
	}

	public NamedMatcher useTransparentBounds(boolean b) {
		matcher.useTransparentBounds(b);
		return this;
	}

	public boolean equals(Object obj) {
		return matcher.equals(obj);
	}

	public int hashCode() {
		return matcher.hashCode();
	}

	public String toString() {
		return matcher.toString();
	}

}
