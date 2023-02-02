// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lemoinetechnologies.pulse.reporting.birt.Configuration;

public class Utils {

  static Logger logger = LogManager.getLogger(Utils.class.getName());

  /**
   * Gets exception mesage
   * 
   * @param exception
   *          an exception
   * @return message of an exception
   */
  public static String getStackTraceAsString(Throwable throwable)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    pw.print(" [ ");
    pw.print(throwable.getClass().getName());
    pw.println(" ] ");
    pw.println("");
    pw.print(throwable.getMessage());
    pw.println("");
    throwable.printStackTrace(pw);
    pw.println("");
    pw.println("");
    return sw.toString();
  }

  /**
   * Delete given folder, therefore, delete all sub-directories and files in
   * this given folder.
   * 
   * @param file
   *          given folder
   * @return <code>true</code> if deletion work fine, <code>false</code>
   *         otherwise.
   */
  public static boolean deleteDirectory(File file)
  {
    boolean result = true;
    if (file.exists()) {
      result = deleteDirectoryContent(file);
      result &= file.delete();
    }
    return result;
  }

  /**
   * Delete all sub-directories and files in the given folder
   * 
   * @param file
   *          given folder
   * @return <code>true</code> if deletion work fine, <code>false</code>
   *         otherwise.
   */
  public static boolean deleteDirectoryContent(File file)
  {
    boolean result = true;
    if (file.exists()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {
            result &= deleteDirectory(files[i]);
          }
          else {
            result &= files[i].delete();
          }
        }
      }
    }
    return result;
  }

  /**
   * Get locale that should be used on the sever side
   * 
   * @param localeAsParameter
   *          a string representation of a locale
   * @return locale
   */
  public static Locale getLocale(String localeAsParameter)
  {
    Locale locale = getLocaleIfAvailable(localeAsParameter);
    if (locale != null) {
      return locale;
    } else {
      locale = getLocaleIfAvailable(Configuration.BIRT_LOCALE);
      if (locale != null) {
        return locale;
      } else {
        return Locale.getDefault();
      }
    }
  }

  /**
   * Get a Locale if given string is representation of available locale, null
   * otherwise
   * 
   * @param locale
   *          string representation of locale
   * @return a locale or null
   */
  private static Locale getLocaleIfAvailable(String locale)
  {
    if (StringUtils.isEmpty(locale)) {
      return null;
    } else {
      Locale liste[] = Locale.getAvailableLocales();
      for (int i = 0; i < liste.length; i++) {
        if (locale.equalsIgnoreCase(liste[i].toString())) {
          return liste[i];
        }
      }
      return null;
    }
  }

  public static String getContentType(String extension)
  {
    if (("html".equalsIgnoreCase(extension)) || ("htm".equalsIgnoreCase(extension))) {
      return "text/html";
    }
    else if (("jpeg".equalsIgnoreCase(extension)) || ("jpg".equalsIgnoreCase(extension)) || ("jpe".equalsIgnoreCase(extension))) {
      return "image/jpeg";
    }
    else if ("gif".equalsIgnoreCase(extension)) {
      return "image/gif";
    }
    else if ("png".equalsIgnoreCase(extension)) {
      return "image/png";
    }
    else if ("pdf".equalsIgnoreCase(extension)) {
      return "application/pdf";
    }
    else if ("doc".equalsIgnoreCase(extension)) {
      return "application/msword";
    }
    else if ("docx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }
    else if ("xls".equalsIgnoreCase(extension)) {
      return "application/vnd.ms-excel";
    }
    else if ("xlsx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }
    else if ("ppt".equalsIgnoreCase(extension)) {
      return "application/vnd.ms-powerpoint";
    }
    else if ("pptx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }
    else if ("ps".equalsIgnoreCase(extension)) {
      return "application/postscript";
    }
    return "";
  }

  public static String getContentTypeDownload(String extension)
  {
    if (("html".equalsIgnoreCase(extension)) || ("htm".equalsIgnoreCase(extension))) {
      return "text/html-download";
    } else if (("jpeg".equalsIgnoreCase(extension)) || ("jpg".equalsIgnoreCase(extension)) || ("jpe".equalsIgnoreCase(extension))) {
      return "image/jpeg-download";
    } else if ("gif".equalsIgnoreCase(extension)) {
      return "image/gif-download";
    } else if ("png".equalsIgnoreCase(extension)) {
      return "image/png-download";
    } else if ("pdf".equalsIgnoreCase(extension)) {
      return "application/pdf-download";
    } else if ("doc".equalsIgnoreCase(extension)) {
      return "application/msword-download";
    } else if ("docx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.wordprocessingml.document-download";
    } else if ("xls".equalsIgnoreCase(extension)) {
      return "application/vnd.ms-excel-download";
    } else if ("xlsx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet-download";
    } else if ("ppt".equalsIgnoreCase(extension)) {
      return "application/vnd.ms-powerpoint-download";
    } else if ("pptx".equalsIgnoreCase(extension)) {
      return "application/vnd.openxmlformats-officedocument.presentationml.presentation-download";
    } else if ("ps".equalsIgnoreCase(extension)) {
      return "application/postscript-download";
    }
    return "";
  }

  /**
   * Get a Map and return equivalent map, but ordered by value
   * 
   * @param <K>
   *          Generic type for key elements
   * @param <V>
   *          Generic type for vlue elements
   * @param map
   *          Given map
   * @return map ordered by value
   */
  public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map)
  {
    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
      public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
        return (o1.getValue()).compareTo(o2.getValue());
      }
    });
    Map<K, V> result = new LinkedHashMap<K, V>();
    for (Map.Entry<K, V> entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }

  public static <K extends Comparable<? super K>, V extends Comparable<? super V>> List<K> getKeysOfSortedMapByValue(Map<K, V> map) {
    Map<K, V> sortedMap = sortMapByValue(map);
    List<K> keyList = new ArrayList<K>();
    for (Entry<K, V> entry : sortedMap.entrySet()) {
      keyList.add(entry.getKey());
    }
    return keyList;
  }

  /**
   * Get whether <code>text</code> is a valid page range for the number of page
   * <code>pagecount</code>
   * 
   * @param text
   * @param pagecount
   * @return true if it is valid, otherwise false
   */
  public static boolean isValidPageRange(String text, int pagecount)
  {
    String p = "((\\d+(\\-\\d+)?, ?)*(\\d+(\\-\\d+)?))";
    Pattern pattern = Pattern.compile(p);

    Matcher matcher = pattern.matcher(text);
    if (!matcher.matches()) {
      return false;
    }
    else {
      String[] tab = text.split(",");
      for (int i = 0; i < tab.length; i++) {
        int j = tab[i].indexOf("-");
        if (j != -1) {
          int min = Integer.parseInt(tab[i].substring(0, j));
          int max = Integer.parseInt(tab[i].substring(j + 1, tab[i].length()));
          if ((min == 0) || (min > max) || (max > pagecount)) {
            return false;
          }
        }
        else {
          int page = Integer.parseInt(tab[i]);
          if ((page == 0) || (page > pagecount)) {
            return false;
          }
        }
      }
      return true;
    }
  }

  public static String getWebAppBaseUrl(HttpServletRequest req)
  {
    return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/" + req.getContextPath();
  }

  public static boolean isParentFile(File possibleParent, File file)
  {
    File parent = file.getParentFile();
    while (parent != null) {
      if (parent.equals(possibleParent))
        return true;
      parent = parent.getParentFile();
    }
    return false;
  }

  public static java.sql.Date parseDate(String value) 
  {
    DateFormat df;
    Date date = null;
    try {
      df = new SimpleDateFormat("yyyy-MM-dd");
      date = df.parse(value);
      return new java.sql.Date(date.getTime());
    }
    catch (Exception e1) {
      try {
      df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
      date = df.parse(value);
      return new java.sql.Date(date.getTime());
      }
      catch (Exception e2) {
        try {
          df = DateFormat.getDateInstance(DateFormat.SHORT);
          date = df.parse(value);
          return new java.sql.Date(date.getTime());
        }
        catch (Exception e3) {
          try {
            df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            date = df.parse(value);
            return new java.sql.Date(date.getTime());
          }
          catch (Exception e4) {
            try {
              df = DateFormat.getDateInstance(DateFormat.LONG);
              date = df.parse(value);
              return new java.sql.Date(date.getTime());
            }
            catch (Exception e5) {
              try {
                df = DateFormat.getDateInstance(DateFormat.FULL);
                date = df.parse(value);
                return new java.sql.Date(date.getTime());
              }
              catch (Exception e) {
                return null;
              }
            }
          }
        }
      }
    }
  }

  public static java.sql.Time parseTime(String value)
  {
    DateFormat df;
    Date date = null;
    try {
      df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
      date = df.parse(value);
      return new java.sql.Time(date.getTime());
    }
    catch (Exception e1) {
      try {
        df = new SimpleDateFormat("HH:mm:ss");
        date = df.parse(value);
        return new java.sql.Time(date.getTime());
      }
      catch (Exception e2) {
        try {
          df = DateFormat.getTimeInstance(DateFormat.SHORT);
          date = df.parse(value);
          return new java.sql.Time(date.getTime());
        }
        catch (Exception e3) {
          try {
            df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            date = df.parse(value);
            return new java.sql.Time(date.getTime());
          }
          catch (Exception e4) {
            try {
              df = DateFormat.getTimeInstance(DateFormat.LONG);
              date = df.parse(value);
              return new java.sql.Time(date.getTime());
            }
            catch (Exception e5) {
              try {
                df = DateFormat.getTimeInstance(DateFormat.FULL);
                date = df.parse(value);
                return new java.sql.Time(date.getTime());
              }
              catch (Exception e6) {
                return null;
              }
            }
          }
        }
      }
    }
  }

  public static java.sql.Timestamp parseDateTime(String value) 
  {
    DateFormat df;
    Date date = null;
    try {
      df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // The only used format 2021-03
      date = df.parse(value);
      return new java.sql.Timestamp(date.getTime());
    }
    catch (Exception e1) {
      try {
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss"); // Format used before 2021-03
        date = df.parse(value);
        return new java.sql.Timestamp(date.getTime());
      }
      catch (Exception e2) {
        try {
          df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
          date = df.parse(value);
          return new java.sql.Timestamp(date.getTime());
        }
        catch (Exception e3) {
          try {
            df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            date = df.parse(value);
            return new java.sql.Timestamp(date.getTime());
          }
          catch (Exception e4) {
            try {
              df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
              date = df.parse(value);
              return new java.sql.Timestamp(date.getTime());
            }
            catch (Exception e5) {
              try {
                df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                date = df.parse(value);
                return new java.sql.Timestamp(date.getTime());
              }
              catch (Exception e) {
                return null;
              }
            }
          }
        }
      }
    }
  }

  public static String formatArray(Object[] array)
  {
    if (null == array) {
      return "[]";
    }
    String out = "[";
    for (int i = 0; i < (array.length - 1); i++) {
      out += array[0].toString() + ", ";
    }
    out += array[array.length - 1].toString() + "]";
    return out;
  }
  
  public static Level getBirtLogLevel(String level){
    if ("ALL".equalsIgnoreCase(level)) {
      return Level.ALL;
    } else if ("FINEST".equalsIgnoreCase(level)) {
      return Level.FINEST;
    } else if ("FINER".equalsIgnoreCase(level)) {
      return Level.FINER;
    } else if ("FINE".equalsIgnoreCase(level)) {
      return Level.FINE;
    } else if ("CONFIG".equalsIgnoreCase(level)) {
      return Level.CONFIG;
    } else if ("INFO".equalsIgnoreCase(level)) {
      return Level.INFO;
    } else if ("WARNING".equalsIgnoreCase(level)) {
      return Level.WARNING;
    } else if ("SEVERE".equalsIgnoreCase(level)) {
      return Level.SEVERE;
    } else if (Level.OFF.equals(level)) {
      return Level.OFF;
    } else {
      return null;
    }
  }
  
  public static String processName(String string)
  {
    String name = "";
    boolean previousLower = false;
    for (int i = 0; i < string.length(); i++) {
      char current = string.charAt(i);
      if (current >= 'a' && current <= 'z') {
        previousLower = true;
        name += current;
      } else {
        if (previousLower) {
          name += " " + Character.toLowerCase(current);
        } else
          name += current;
        previousLower = false;
      }
    }
    return name;
  }
  
  static String autoVersioningSuffix = "";
  public static String getAutoVersioningSuffix()
  {
    if (autoVersioningSuffix.equals("")) {
      try {
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd.hh.mm");
        autoVersioningSuffix = df.format(System.currentTimeMillis());
      } catch (Exception e) {}
    }
    return autoVersioningSuffix;
  }

  // Decode strings coming from the url - is NEVER used !!!
  // For example, "%7C" will become "|"
  public static String[] decodeUrl(String[] strs)
  {
    if (strs == null)
      return null;
    
    for (int i = 0; i < strs.length; i++) {
      try {
        // See https://www.urlencoder.io/java/
        // %xx is automaticaly replaced ! Always !
        // %20 is used in url -> It will be decoded. And '+' too
        strs[i] = java.net.URLDecoder.decode(strs[i], StandardCharsets.UTF_8.name());
      } catch (Exception e) {
        // not going to happen - value came from JDK's own StandardCharsets
      }
    }
    return strs;
  }
  
  // Encode SINGLE string value for being included in a url
  // For example, "|" will become "%7C"
  public static String encodeValue(String value)
  {
    if (value == null)
      return null;
    
    try {
      String tmp = java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name());
      String tmp2 = tmp.replaceAll(Pattern.quote("+"), "%20");
      // WARNING ! %20 is used here. '+' is removed
      // For details read https://www.urlencoder.io/java/
      return tmp2;
    } catch (Exception e) {
      // not going to happen - value came from JDK's own StandardCharsets
    }
    return value;
  }

  // Encode strings for being included in a url
  // For example, "|" will become "%7C"
  public static String[] encodeUrl(String[] strs)
  {
    if (strs == null)
      return null;
    
    for (int i = 0; i < strs.length; i++) {
      try {
        strs[i] = Utils.encodeValue(strs[i]);
        // WARNING ! %20 and '+'
        // For details read https://www.urlencoder.io/java/
      } catch (Exception e) {
        // not going to happen - value came from JDK's own StandardCharsets
      }
    }
    return strs;
  }
  
  // Encode single string for being included in a url
  // For example, "|" will become "%7C"
  public static String encodeFullUrl(String url)
  {
    if (url == null)
      return null;
    // Split
    String[] array = url.split("?");
    if ( array.length < 2 ) {
      return url;
    }

    String outURL = array[2] + "?";
    String[] keyValues = array[2].split("&");

    for (int i = 0; i < keyValues.length; i++) {
      String[] singleKeyValue = keyValues[i].split("=");
      if ( singleKeyValue.length != 2 ){
        return url;
      }
      // Encode parameters
      try {
        String tmp = Utils.encodeValue(singleKeyValue[i]);
        outURL += singleKeyValue[0] +tmp;
      } catch (Exception e) {
        // not going to happen - value came from JDK's own StandardCharsets
        return "";
      }
    }
    return outURL;
  }
}