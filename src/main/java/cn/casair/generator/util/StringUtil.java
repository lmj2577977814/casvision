package cn.casair.generator.util;

import com.google.common.base.Strings;

import java.lang.reflect.Array;

public class StringUtil {
    /**
     * Gets the camel case string.
     *
     * @param inputString
     *            the input string
     * @param firstCharacterUppercase
     *            the first character uppercase
     * @return the camel case string
     */
    public static String getCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    public static int hash(int aSeed, Object aObject) {
        int result = aSeed;
        if (aObject == null) {
            result = hash(result, 0);
        } else if (!aObject.getClass().isArray()) {
            result = hash(result, aObject.hashCode());
        } else {
            int length = Array.getLength(aObject);
            for (int idx = 0; idx < length; ++idx) {
                Object item = Array.get(aObject, idx);
                // recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }

    public static String composeFullyQualifiedTableName(String catalog,
                                                        String schema, String tableName, char separator) {
        StringBuilder sb = new StringBuilder();

        if (!Strings.isNullOrEmpty(catalog)) {
            sb.append(catalog);
            sb.append(separator);
        }

        if (!Strings.isNullOrEmpty(schema)) {
            sb.append(schema);
            sb.append(separator);
        } else {
            if (sb.length() > 0) {
                sb.append(separator);
            }
        }

        sb.append(tableName);

        return sb.toString();
    }

    public static boolean stringContainsSQLWildcard(String s) {
        if (s == null) {
            return false;
        }

        return s.indexOf('%') != -1 || s.indexOf('_') != -1;
    }

    public static String convertPlural(String word) {
        if(Strings.isNullOrEmpty(word)) {
            return "";
        }

        if(word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        }

        if(word.endsWith("o") || word.endsWith("s") || word.endsWith("x") || word.endsWith("sh") || word.endsWith("ch")) {
            return word + "es";
        }

        return  word + "s";
    }
}
