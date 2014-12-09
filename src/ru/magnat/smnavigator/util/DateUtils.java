package ru.magnat.smnavigator.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {
	
	public static String format(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));		
	
		return format.format(date);
	}
	
}
