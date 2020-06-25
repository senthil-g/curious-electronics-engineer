package org.sen.webapp.utilities;

public class StringUtils {
    public static Boolean isNotNull(Object input) {
        return input != null;
    }
    public static Boolean isNotEmpty(Object input) {
        return !input.toString().trim().isEmpty();
    }

    public static boolean isNullOrEmpty( Object pString )
    {
        if(pString == null)
            return true;
        return pString.toString().trim().isEmpty() || pString.toString().equals("null");
    }

    public static boolean isNotNullOrEmpty( Object pString )
        {
            return !isNullOrEmpty(pString);
        }
}