/**
 * 
 */
package ru.magnat.smnavigator.util;

import android.text.TextUtils;

public class StateRegistrationCertificate {
    public static final int TYPE_NATURAL = 15;
    public static final int TYPE_LEGAL = 13;

    public static boolean validate(String value) {
	if (value == null) {
	    return false;
	}
	if (!TextUtils.isDigitsOnly(value)) {
	    return false;
	}
	if (value.length() == TYPE_LEGAL) {
	    Long sum = Long.valueOf(value.substring(0, value.length() - 1));
	    Long check = Long.valueOf(value.substring(value.length() - 1, value.length()));
	    if (sum % 11 == check) {
		return true;
	    } else {
		return false;
	    }
	} else if (value.length() == TYPE_NATURAL) {
	    Long sum = Long.valueOf(value.substring(0, value.length() - 1));
	    Long check = Long.valueOf(value.substring(value.length() - 1, value.length()));
	    if (sum % 13 == check) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }
}
