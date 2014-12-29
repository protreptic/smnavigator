/**
 * 
 */
package ru.magnat.smnavigator.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;

/**
 * @author petr_bu
 *
 */
public class Fonts {
	
	private static Fonts sInstance;
	
	private Context mContext;
	private Map<String, Typeface> mTypefaces = new HashMap<String, Typeface>();
	private Typeface mDefaultTypeface;
	
	public interface Roboto {}
	
	public interface RobotoCondensed {}
	
	private Fonts(Context context) {
		mContext = context;

		mDefaultTypeface = Typeface.createFromAsset(context.getAssets(), "font/RobotoCondensed-Light.ttf");
		
//		mTypefaces.put("Roboto-Black", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf"));
//		mTypefaces.put("Roboto-BlackItalic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BlackItalic.ttf"));
//		mTypefaces.put("Roboto-Bold", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf"));
//		mTypefaces.put("Roboto-BoldItalic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldItalic.ttf"));
//		mTypefaces.put("RobotoCondensed-Bold", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Bold.ttf"));
//		mTypefaces.put("RobotoCondensed-BoldItalic", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-BoldItalic.ttf"));
//		mTypefaces.put("RobotoCondensed-Italic", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Italic.ttf"));
//		mTypefaces.put("RobotoCondensed-Light", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf"));
//		mTypefaces.put("RobotoCondensed-LightItalic", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-LightItalic.ttf"));
//		mTypefaces.put("RobotoCondensed-Regular", Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Regular.ttf"));
//		mTypefaces.put("Roboto-Italic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf"));
//		mTypefaces.put("Roboto-Light", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
//		mTypefaces.put("Roboto-LightItalic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-LightItalic.ttf"));
//		mTypefaces.put("Roboto-Medium", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
//		mTypefaces.put("Roboto-MediumItalic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-MediumItalic.ttf"));
//		mTypefaces.put("Roboto-Regular", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
//		mTypefaces.put("Roboto-Thin", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf"));
//		mTypefaces.put("Roboto-ThinItalic", Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-ThinItalic.ttf"));
	}
	
	public static Fonts get(Context context) {
		if (sInstance == null) {
			sInstance = new Fonts(context);
		}
		
		return sInstance;
	}
	
	public Typeface getTypeface(String typeface) {
		Typeface result = mTypefaces.get(typeface);
		
		if (result == null) {
			result = Typeface.createFromAsset(mContext.getAssets(), "font/" + typeface + ".ttf");
			if (result == null) {
				result = Typeface.DEFAULT;
			} else {
				mTypefaces.put(typeface, result);
			}
		}
		
		return result;
	}
	
	public Typeface getDefaultTypeface () {
		return mDefaultTypeface;
	}
}
