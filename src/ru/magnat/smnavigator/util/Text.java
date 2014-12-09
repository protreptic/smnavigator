package ru.magnat.smnavigator.util;

public class Text {
	public static String prepareAddress(String address){
		String[] parts = address.split(",");
		String out = "";
		for (String part: parts){
			part = part.trim();
			if (part.isEmpty()) continue;
			if (!out.isEmpty())
				out+=", ";
			out+=part;
		}
		return out;
	}
	
	public static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
}
