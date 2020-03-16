package com.bondex.util.sql;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.bondex.util.Bytes;
import com.bondex.util.Regexs;


public class Strings {

    public static String        STR_CONFIG_PROPERTIES = "config/config.properties";
    public static String        STR_UTF8              = "UTF-8";
    public static final String  DATETIME_DEFAULT      = "yyyy-MM-dd HH:mm:ss";
    public static final Boolean IS_DEBUG              = true;
    public static final Integer PAGE_NUMBER           = 1;
    public static final Integer PAGE_SIZE             = 20;
	
    /**
     * The empty String
     */
    public static final String EMPTY           = "";

    /**
     * A String for a space character.
     */
    public static final String SPACE           = " ";

    /**
     * Represents a failed index search.
     */
    public static final int    INDEX_NOT_FOUND = -1;

    public static final char   SEPARATOR       = '_';

    public static final String ALPHANUMERIC    = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    /**
     * 用Charsets.UTF_8解码，再用charset编码
     */
    public static String newString(String str, String charset) {
        return newString(str, Charsets.UTF_8, Charset.forName(charset));
    }

    /**
     * 用Charsets.UTF_8解码，再用charset编码
     */
    public static String newString(String str, Charset charset) {
        return newString(str, Charsets.UTF_8, charset);
    }

    /**
     * 用decode_charset解码，再用charset编码
     */
    public static String newString(String str, String decode_charset, String charset) {
        return newString(str, Charset.forName(decode_charset), Charset.forName(charset));
    }

    /**
     * 用decode_charset解码，再用charset编码
     */
    public static String newString(String str, Charset decode_charset, Charset charset) {
        return new String(str.getBytes(decode_charset), charset);
    }

    public static String newStringFromUnicode(String str) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = str.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = str.substring(start + 2, str.length());
            } else {
                charStr = str.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }


    public static boolean isNullOrEmpty(Object... objs) {
        for (Object obj : objs) {
            String str;
            if (obj instanceof String) {
                str = (String) obj;
            } else {
                if (obj == null) {
                    str = "";
                } else {
                    str = String.valueOf(obj);
                }
            }
            if (isEmpty(str) || isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotNullOrEmpty(Object... objs) {
        boolean result = true;
        if (objs == null || objs.length == 0) {
            result = false;
        } else {
            for (Object obj : objs) {
                String value = null;
                if (obj instanceof String) {
                    value = (String) obj;
                } else {
                    if (obj == null) {
                        value = "";
                    } else {
                        String.valueOf(obj);
                    }
                }
                result &= !((null == value) || ("".equals(value)) ? true : false);
            }
        }
        return result;
    }

    /**
     * 判断是否为空
     * 
     * <pre>
     * Strings.isEmpty(null)      = true
     * Strings.isEmpty("")        = true
     * Strings.isEmpty(" ")       = false
     * Strings.isEmpty("bob")     = false
     * Strings.isEmpty("  bob  ") = false
     * </pre>
     */
    public static boolean isEmpty(final CharSequence cs) {
        return StringUtils.isEmpty(cs);
    }

    /**
     * 判断是否不为空
     * 
     * <pre>
     * Strings.isNotEmpty(null)      = false
     * Strings.isNotEmpty("")        = false
     * Strings.isNotEmpty(" ")       = true
     * Strings.isNotEmpty("bob")     = true
     * Strings.isNotEmpty("  bob  ") = true
     * </pre>
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return StringUtils.isNotEmpty(cs);
    }

    /**
     * 判断所有传入参数是否为空
     * <p>
     * Checks if any one of the CharSequences are empty ("") or null.
     * </p>
     * 
     * <pre>
     * Strings.isAnyEmpty(null)             = true
     * Strings.isAnyEmpty(null, "foo")      = true
     * Strings.isAnyEmpty("", "bar")        = true
     * Strings.isAnyEmpty("bob", "")        = true
     * Strings.isAnyEmpty("  bob  ", null)  = true
     * Strings.isAnyEmpty(" ", "bar")       = false
     * Strings.isAnyEmpty("foo", "bar")     = false
     * </pre>
     */
    public static boolean isEmpty(CharSequence... css) {
        return StringUtils.isAnyEmpty(css);
    }

    /**
     * 判断所有传入参数是否都不为空
     * <p>
     * Checks if none of the CharSequences are empty ("") or null.
     * </p>
     * 
     * <pre>
     * Strings.isNoneEmpty(null)             = false
     * Strings.isNoneEmpty(null, "foo")      = false
     * Strings.isNoneEmpty("", "bar")        = false
     * Strings.isNoneEmpty("bob", "")        = false
     * Strings.isNoneEmpty("  bob  ", null)  = false
     * Strings.isNoneEmpty(" ", "bar")       = true
     * Strings.isNoneEmpty("foo", "bar")     = true
     * </pre>
     */
    public static boolean isNoneEmpty(CharSequence... css) {
        return StringUtils.isNoneEmpty(css);
    }

    /**
     * 判断是否为空(含空字符串)
     * <p>
     * Checks if a CharSequence is whitespace, empty ("") or null.
     * </p>
     * 
     * <pre>
     * Strings.isBlank(null)      = true
     * Strings.isBlank("")        = true
     * Strings.isBlank(" ")       = true
     * Strings.isBlank("bob")     = false
     * Strings.isBlank("  bob  ") = false
     * </pre>
     */
    public static boolean isBlank(final CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    /**
     * 判断是否不为空(含空字符串)
     * <p>
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     * </p>
     * 
     * <pre>
     * Strings.isNotBlank(null)      = false
     * Strings.isNotBlank("")        = false
     * Strings.isNotBlank(" ")       = false
     * Strings.isNotBlank("bob")     = true
     * Strings.isNotBlank("  bob  ") = true
     * </pre>
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    /**
     * 判断所有参数是否为空(含空字符串)
     * <p>
     * Checks if any one of the CharSequences are blank ("") or null and not whitespace only..
     * </p>
     * 
     * <pre>
     * Strings.isAnyBlank(null)             = true
     * Strings.isAnyBlank(null, "foo")      = true
     * Strings.isAnyBlank(null, null)       = true
     * Strings.isAnyBlank("", "bar")        = true
     * Strings.isAnyBlank("bob", "")        = true
     * Strings.isAnyBlank("  bob  ", null)  = true
     * Strings.isAnyBlank(" ", "bar")       = true
     * Strings.isAnyBlank("foo", "bar")     = false
     * </pre>
     */
    public static boolean isAnyBlank(CharSequence... css) {
        return StringUtils.isAnyBlank(css);
    }

    /**
     * 判断所有参数是否不为空(含空字符串)
     * <p>
     * Checks if none of the CharSequences are blank ("") or null and whitespace only..
     * </p>
     * 
     * <pre>
     * Strings.isNoneBlank(null)             = false
     * Strings.isNoneBlank(null, "foo")      = false
     * Strings.isNoneBlank(null, null)       = false
     * Strings.isNoneBlank("", "bar")        = false
     * Strings.isNoneBlank("bob", "")        = false
     * Strings.isNoneBlank("  bob  ", null)  = false
     * Strings.isNoneBlank(" ", "bar")       = false
     * Strings.isNoneBlank("foo", "bar")     = true
     * </pre>
     */
    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    /**
     * 判断是否数字
     */
    public static boolean isNumeric(String cs) {
        return StringUtils.isNumeric(cs);
    }

    /**
     * 判断是否整形数字
     */
    public static boolean isInteger(String str) {
        int begin = 0;
        if (str == null || str.trim().equals("")) {
            return false;
        }
        str = str.trim();
        if (str.startsWith("+") || str.startsWith("-")) {
            if (str.length() == 1) {
                // "+" "-"
                return false;
            }
            begin = 1;
        }
        for (int i = begin; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Equals
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Compares two CharSequences, returning {@code true} if they represent equal sequences of characters.
     * </p>
     * 
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     */
    public static boolean isEquals(final CharSequence cs1, final CharSequence cs2) {
        return StringUtils.equals(cs1, cs2);
    }

    public static boolean isNotEquals(final CharSequence cs1, final CharSequence cs2) {
        return !isEquals(cs1, cs2);
    }

    /**
     * <p>
     * Compares two CharSequences, returning {@code true} if they represent equal sequences of characters, ignoring case.
     * </p>
     * 
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     */
    public static boolean isEqualsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    public static boolean isNotEqualsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        return !isEqualsIgnoreCase(str1, str2);
    }

    /**
     * 分隔字符串，判断是否相等
     */
    public static boolean isEquals(String p1, String p2, String separator) {
        p1 = StringUtils.isBlank(p1) ? "" : p1;
        p2 = StringUtils.isBlank(p1) ? "" : p2;
        String[] paramsArray1 = p1.split(separator);
        String[] paramsArray2 = p2.split(separator);
        return Arrays.equals(paramsArray1, paramsArray2);
    }

    /**
     * 分隔字符串，判断是否存在
     */
    public static boolean isContains(String params, String param, String separator) {
        return ArrayUtils.contains(params.split(separator), param);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses {@link String#indexOf(String)} if possible.
     * </p>
     * 
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     */
    public static boolean isContains(final CharSequence seq, final CharSequence searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    /**
     * <p>
     * Checks if CharSequence contains a search CharSequence irrespective of case, handling {@code null}. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}.
     * </p>
     * 
     * <pre>
     * StringUtils.contains(null, *) = false
     * StringUtils.contains(*, null) = false
     * StringUtils.contains("", "") = true
     * StringUtils.contains("abc", "") = true
     * StringUtils.contains("abc", "a") = true
     * StringUtils.contains("abc", "z") = false
     * StringUtils.contains("abc", "A") = true
     * StringUtils.contains("abc", "Z") = false
     * </pre>
     */
    public static boolean isContainsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    /**
     * 判断是否URL
     */
    public static boolean isUrl(String str) {
        return Regexs.isUrl(str);
    }

    /**
     * 判断指定的字符串是否为null且length=0
     * 
     * <pre class="code">
     * Strings.hasLength(null) = false
     * Strings.hasLength("") = false
     * Strings.hasLength(" ") = true
     * Strings.hasLength("Hello") = true
     * </pre>
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * 判断指定的字符串是否为null且length=0
     */
    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    /**
     * 判断是否包含文字
     * <p>
     * 
     * <pre class="code">
     * Strings.hasText(null) = false
     * Strings.hasText("") = false
     * Strings.hasText(" ") = false
     * Strings.hasText("12345") = true
     * Strings.hasText(" 12345 ") = true
     * </pre>
     */
    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含文字
     * <p>
     * 
     * <pre class="code">
     * Strings.hasText(null) = false
     * Strings.hasText("") = false
     * Strings.hasText(" ") = false
     * Strings.hasText("12345") = true
     * Strings.hasText(" 12345 ") = true
     * </pre>
     */
    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    /**
     * <p>
     * Splits the provided text into an array, separators specified. This is an alternative to using StringTokenizer.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     */
    public static String[] split(final String str, final String separatorChars) {
        return StringUtils.split(str, separatorChars);
    }

    /**
     * 将首字符转换为大写
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * 将首字符转换为小写
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     * <p>
     * Removes a substring only if it is at the beginning of a source string, otherwise returns the source string.
     * </p>
     * 
     * <pre>
     * Strings.removeStart(null, *)      = null
     * Strings.removeStart("", *)        = ""
     * Strings.removeStart(*, null)      = *
     * Strings.removeStart("www.domain.com", "www.")   = "domain.com"
     * Strings.removeStart("domain.com", "www.")       = "domain.com"
     * Strings.removeStart("www.domain.com", "domain") = "www.domain.com"
     * Strings.removeStart("abc", "")    = "abc"
     * </pre>
     */
    public static String removeStart(final String str, final String remove) {
        return StringUtils.removeStart(str, remove);
    }

    /**
     * <p>
     * Case insensitive removal of a substring if it is at the beginning of a source string, otherwise returns the source string.
     * </p>
     * 
     * <pre>
     * Strings.removeStartIgnoreCase(null, *)      = null
     * Strings.removeStartIgnoreCase("", *)        = ""
     * Strings.removeStartIgnoreCase(*, null)      = *
     * Strings.removeStartIgnoreCase("www.domain.com", "www.")   = "domain.com"
     * Strings.removeStartIgnoreCase("www.domain.com", "WWW.")   = "domain.com"
     * Strings.removeStartIgnoreCase("domain.com", "www.")       = "domain.com"
     * Strings.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * Strings.removeStartIgnoreCase("abc", "")    = "abc"
     * </pre>
     */
    public static String removeStartIgnoreCase(final String str, final String remove) {
        return StringUtils.removeStartIgnoreCase(str, remove);
    }

    /**
     * <p>
     * Removes a substring only if it is at the end of a source string, otherwise returns the source string.
     * </p>
     * 
     * <pre>
     * Strings.removeEnd(null, *)      = null
     * Strings.removeEnd("", *)        = ""
     * Strings.removeEnd(*, null)      = *
     * Strings.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * Strings.removeEnd("www.domain.com", ".com")   = "www.domain"
     * Strings.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * Strings.removeEnd("abc", "")    = "abc"
     * </pre>
     */
    public static String removeEnd(final String str, final String remove) {
        return StringUtils.removeEnd(str, remove);
    }

    /**
     * <p>
     * Case insensitive removal of a substring if it is at the end of a source string, otherwise returns the source string.
     * </p>
     * 
     * <pre>
     * Strings.removeEndIgnoreCase(null, *)      = null
     * Strings.removeEndIgnoreCase("", *)        = ""
     * Strings.removeEndIgnoreCase(*, null)      = *
     * Strings.removeEndIgnoreCase("www.domain.com", ".com.")  = "www.domain.com"
     * Strings.removeEndIgnoreCase("www.domain.com", ".com")   = "www.domain"
     * Strings.removeEndIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * Strings.removeEndIgnoreCase("abc", "")    = "abc"
     * Strings.removeEndIgnoreCase("www.domain.com", ".COM") = "www.domain")
     * Strings.removeEndIgnoreCase("www.domain.COM", ".com") = "www.domain")
     * </pre>
     */
    public static String removeEndIgnoreCase(final String str, final String remove) {
        return StringUtils.removeEndIgnoreCase(str, remove);
    }

    /**
     * <p>
     * Removes all occurrences of a character from within the source string.
     * </p>
     * 
     * <pre>
     * StringUtils.remove(null, *)       = null
     * StringUtils.remove("", *)         = ""
     * StringUtils.remove("queued", 'u') = "qeed"
     * StringUtils.remove("queued", 'z') = "queued"
     * </pre>
     */
    public static String remove(final String str, final char remove) {
        return StringUtils.remove(str, remove);
    }

    /**
     * 删除指定的字符串
     * <p>
     * Removes all occurrences of a substring from within the source string.
     * </p>
     * 
     * <pre>
     * Strings.remove(null, *)        = null
     * Strings.remove("", *)          = ""
     * Strings.remove(*, null)        = *
     * Strings.remove(*, "")          = *
     * Strings.remove("queued", "ue") = "qd"
     * Strings.remove("queued", "zz") = "queued"
     * </pre>
     */
    public static String remove(final String str, final String remove) {
        return StringUtils.remove(str, remove);
    }

    /**
     * 删除所有指定的字符串
     */
    public static String remove(final String str, final String... removes) {
        String result = new String(str);
        for (String remove : removes) {
            result = remove(result, remove);
        }
        return result;
    }

    /**
     * 删除\t\n
     */
    public static String removeNT(final String str) {
        return remove(str, "\n", "\t");
    }

    // index
    // -----------------------------------------------------------------------
    /**
     * 找到指定的字符串开头位置（忽略大小写）
     * <p>
     * Case in-sensitive find of the first index within a CharSequence.
     * </p>
     * 
     * <pre>
     * Strings.indexOfIgnoreCase(null, *)          = -1
     * Strings.indexOfIgnoreCase(*, null)          = -1
     * Strings.indexOfIgnoreCase("", "")           = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * Strings.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * Strings.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return StringUtils.indexOfIgnoreCase(str, searchStr, 0);
    }

    // IndexOfAny chars
    // -----------------------------------------------------------------------
    /**
     * <p>
     * Search a CharSequence to find the first index of any character in the given set of characters.
     * </p>
     * 
     * <pre>
     * StringUtils.indexOfAny(null, *)                = -1
     * StringUtils.indexOfAny("", *)                  = -1
     * StringUtils.indexOfAny(*, null)                = -1
     * StringUtils.indexOfAny(*, [])                  = -1
     * StringUtils.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringUtils.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringUtils.indexOfAny("aba", ['z'])           = -1
     * </pre>
     */
    public static int indexOfAny(final CharSequence cs, final char... searchChars) {
        return StringUtils.indexOfAny(cs, searchChars);
    }

    /**
     * <p>
     * Search a CharSequence to find the first index of any character in the given set of characters.
     * </p>
     * 
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba","z")          = -1
     * </pre>
     */
    public static int indexOfAny(final CharSequence cs, final String searchChars) {
        return StringUtils.indexOfAny(cs, searchChars);
    }

    /**
     * <p>
     * Search a CharSequence to find the first index of any character in the given set of characters.
     * </p>
     * 
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba","z")          = -1
     * </pre>
     */
    public static int indexOfAny(final CharSequence cs, final String... searchChars) {
        return StringUtils.indexOfAny(cs, searchChars);
    }

    /**
     * <p>
     * Gets the substring after the last occurrence of a separator. The separator is not returned.
     * </p>
     * 
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     */
    public static String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator);
    }

    /**
     * 截取separator之后的字符串
     */
    public static String substringAfterIgnoreCases(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = indexOfIgnoreCase(str, separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 截取separator之后的字符串
     */
    public static String substringAfterIgnoreCases(String str, String... separators) {
        for (String separator : separators) {
            str = Strings.substringAfterIgnoreCases(str, separator);
        }
        return str;
    }

    /**
     * 从separator开始截取(包含separator)
     */
    public static String substringAfterAndContainsIgnoreCases(String str, String separator) {
        return StringUtils.substring(str, indexOfIgnoreCase(str, separator));
    }

    /**
     * 从separator开始截取(包含separator)
     */
    public static String substringAfterAndContainsIgnoreCases(String str, String... separators) {
        for (String separator : separators) {
            str = substringAfterAndContainsIgnoreCases(str, separator);
        }
        return str;
    }

    /**
     * 截取separator之前的字符串
     */
    public static String substringBeforeIgnoreCases(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = indexOfIgnoreCase(str, separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取separator之前的字符串
     */
    public static String substringBeforeIgnoreCases(String str, String... separators) {
        for (String separator : separators) {
            str = Strings.substringBeforeIgnoreCases(str, separator);
        }
        return str;
    }


    /**
     * @see org.springframework.util.StringUtils
     */
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        sb.append(str.substring(1));
        return sb.toString();
    }

    /**
     * 将驼峰风格替换为下划线风格
     */
    public static String camelhumpToUnderline(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    /**
     * 分隔字符串，去重(两个字符串的separator必须一致) 如： p1: 1,2,3 p2: 2,3,4 result: 1,2,3,4
     */
    public static String unique(String p1, String p2, String separator) {
        List<String> list1 = null;
        List<String> list2 = null;
        if (Strings.isNotBlank(p1)) {
            list1 = Arrays.asList(p1.split(separator));
        } else {
            list1 = new ArrayList();
        }
        if (Strings.isNotBlank(p2)) {
            list2 = Arrays.asList(p2.split(separator));
        } else {
            list2 = new ArrayList();
        }
        list1.addAll(list2);
        List<String> resultList = list1;
        Set<String> set = new HashSet(resultList);
        resultList.clear();
        resultList.addAll(set);
        return StringUtils.join(resultList, separator);
    }

    /**
     * 分隔字符串，得到最后一个
     */
    public static String lastStringOfSeparator(String params, String separator) {
        String[] paramArray = params.split(separator);
        String result = paramArray[paramArray.length - 1];
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> String join(final T... elements) {
        return StringUtils.join(elements);
    }

    /**
     * Appends the suffix to the end of the string if the string does not
     * already end with any the suffixes.
     * 
     * <pre>
     * StringUtils.appendIfMissing(null, null) = null
     * StringUtils.appendIfMissing("abc", null) = "abc"
     * StringUtils.appendIfMissing("", "xyz") = "xyz"
     * StringUtils.appendIfMissing("abc", "xyz") = "abcxyz"
     * StringUtils.appendIfMissing("abcxyz", "xyz") = "abcxyz"
     * StringUtils.appendIfMissing("abcXYZ", "xyz") = "abcXYZxyz"
     * </pre>
     * <p>
     * With additional suffixes,
     * </p>
     * 
     * <pre>
     * StringUtils.appendIfMissing(null, null, null) = null
     * StringUtils.appendIfMissing("abc", null, null) = "abc"
     * StringUtils.appendIfMissing("", "xyz", null) = "xyz"
     * StringUtils.appendIfMissing("abc", "xyz", new CharSequence[]{null}) = "abcxyz"
     * StringUtils.appendIfMissing("abc", "xyz", "") = "abc"
     * StringUtils.appendIfMissing("abc", "xyz", "mno") = "abcxyz"
     * StringUtils.appendIfMissing("abcxyz", "xyz", "mno") = "abcxyz"
     * StringUtils.appendIfMissing("abcmno", "xyz", "mno") = "abcmno"
     * StringUtils.appendIfMissing("abcXYZ", "xyz", "mno") = "abcXYZxyz"
     * StringUtils.appendIfMissing("abcMNO", "xyz", "mno") = "abcMNOxyz"
     * </pre>
     */
    public static String appendIfMissing(final String str, final CharSequence suffix, final CharSequence... suffixes) {
        return StringUtils.appendIfMissing(str, suffix, suffixes);
    }

    /**
     * 获取str或者默认值
     */
    public static String get(String str, String defaults) {
        if (isNullOrEmpty(str)) {
            return defaults;
        }
        return str;
    }

    /**
     * 获取富文本内容中的图片链接地址
     */
    public static List<String> getTextImageSrc(String text) {
        if (isNullOrEmpty(text))
            return null;
        String regex = "<\\s*[I|i][m|M][g|G]\\s+([^>]*)\\s*>";
        Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        Matcher ma = pa.matcher(text);
        List<String> list = new ArrayList<String>();
        while (ma.find()) {// 首先判断话题内容中是否有图片
            list.add(ma.group());
        }
        if (list.size() != 0) {// 有图片文件
            List<String> imgSrcList = null;
            String a = null;
            for (String s : list) {
                ma = Pattern.compile("[s|S][R|r][c|C]=[\"|'](.*?)[\"|']").matcher(s);
                if (ma.find()) {
                    a = ma.group();
                    if (imgSrcList == null)
                        imgSrcList = new ArrayList<String>();
                } else {
                    a = null;
                }
                if (a != null) {
                    a = a.replaceAll("[s|S][R|r][c|C]=[\"|']", "").replaceAll("[\"|']", "");
                    imgSrcList.add(a);
                }
            }
            if (imgSrcList != null && imgSrcList.size() != 0)
                return imgSrcList;
            else
                return null;
        } else {
            return null;
        }
    }

    public static String format(String format, Object... args) {
        return String.format(format, args);
    }

    public static String[] toArray(String str, String prefix) {
        return str.split(prefix);
    }

    public static List<String> toList(String str, String prefix) {
        String[] strArr = toArray(str, prefix);
        List<String> list = new ArrayList<String>();
        if (str.length() > 0) {
            for (int i = 0; i < strArr.length; ++i) {
                list.add(strArr[i].toLowerCase());
            }
        }
        return list;
    }

    /**
     * 转换为Map
     * 
     * <pre>
     * query=name=zhangsan&age=24&sex=0
     * </pre>
     */
    public static Map<String, String> toQueryMap(String query) {
        Map<String, String> result = new HashMap();
        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }
        return result;
    }

    public static String toUnicode(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }


    /**
     * 转为16进制
     * 
     * <pre>
     * 
     * String s = &quot;timingbar&quot;;
     * Logs.msg(toHexString(s));
     * 等于
     * Logs.msg(Encodes.encodeHex(s.getBytes()));
     * 输出 74696d696e67626172
     * </pre>
     */
    public static String toHexString(String str) {
        return toHexString(str, STR_UTF8);
    }

    /**
     * 转为16进制
     * 
     * <pre>
     * 
     * String s = &quot;timingbar&quot;;
     * Logs.msg(toHexString(s));
     * 等于
     * Logs.msg(Encodes.encodeHex(s.getBytes()));
     * 输出 74696d696e67626172
     * </pre>
     */
    public static String toHexString(String str, String charset) {
        String result = null;
        try {
            byte[] b = str.getBytes(charset);
            result = Bytes.toHexString(b);
        } catch (Exception e) {
            new RuntimeException(e);
        }
        return result;
    }

    /**
     * 将char转换为byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将16进制转换为byte
     * 
     * <pre>
     * String h = &quot;74696d696e67626172&quot;; //16进制
     * Logs.msg(Bytes.toHexString(Strings.toByteFromHexString(h)));
     * 等于
     * Logs.msg(Bytes.toHexString(Encodes.decodeHex(h)));
     * 输出:74696d696e67626172
     * </pre>
     */
    public static byte[] toByteFromHexString(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String toStringCall(Object t) {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setUseClassName(false);
        style.setUseIdentityHashCode(false);
        style.setContentStart("{");
        style.setContentEnd("}");
        style.setArrayStart("[");
        style.setArrayEnd("]");
        style.setArraySeparator(",");
        style.setFieldNameValueSeparator("=");
        style.setNullText("null");
        ToStringBuilder builder = new ReflectionToStringBuilder(t, style);
        return builder.toString();
    }

 
    // endsWith
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Check if a CharSequence ends with a specified suffix.
     * </p>
     * <p>
     * {@code null}s are handled without exceptions. Two {@code null} references are considered to be equal. The comparison is case sensitive.
     * </p>
     * 
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "def")     = false
     * StringUtils.endsWith("abcdef", null)  = false
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * StringUtils.endsWith("ABCDEF", "cde") = false
     * </pre>
     * 
     * @see String#endsWith(String)
     * @param str the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case sensitive, or
     *         both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from endsWith(String, String) to endsWith(CharSequence, CharSequence)
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
        return StringUtils.endsWith(str, suffix);
    }

    /**
     * <p>
     * Case insensitive check if a CharSequence ends with a specified suffix.
     * </p>
     * <p>
     * {@code null}s are handled without exceptions. Two {@code null} references are considered to be equal. The comparison is case insensitive.
     * </p>
     * 
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null)      = true
     * StringUtils.endsWithIgnoreCase(null, "def")     = false
     * StringUtils.endsWithIgnoreCase("abcdef", null)  = false
     * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     * 
     * @see String#endsWith(String)
     * @param str the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case insensitive, or
     *         both {@code null}
     * @since 2.4
     * @since 3.0 Changed signature from endsWithIgnoreCase(String, String) to endsWithIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
        return StringUtils.endsWithIgnoreCase(str, suffix);
    }

    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0)
                        sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String firstCharToUpperCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }
}
