package com.zelwise.spiewnik;

import java.text.SimpleDateFormat;

import android.content.Context;

public final class Utils {
	public static final String NewLine = "\r\n";
	public static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String ToTitleCase(String str){
		if (str == null) {
            return "";
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i=0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
	}
	
	public static float pixelsToSp(Context context, Float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}
	
	public static int ToInt(String value, int defaultValue) {
        int result;
        
        try 
        {
          result = Integer.parseInt(value);
        } 
        catch(NumberFormatException e) 
        { 
        	result = defaultValue; 
        }
        
        return result;
	}
	
	public static String Trim(String content) {
		return Trim(content, 60);
	}
	
	public static String Trim(String content, int length) {
		if (content.length() > length) {
			return content.trim().substring(0, length - 1 ).replace("\n", " ")
					.replace("\r", "").trim()
					+ "...";
		}
		return content;
	}
	
	public static String Join(String r[], String delimiter)
	{
		if (r.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        int i;
        for(i=0;i<r.length-1;i++)
            sb.append(r[i] + delimiter);
        return sb.toString() + r[i];
	}
	
	public static String Join(char r[], String delimiter)
	{
		if (r.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        int i;
        for(i=0;i<r.length-1;i++)
            sb.append(r[i] + delimiter);
        return sb.toString() + r[i];
	}
}
