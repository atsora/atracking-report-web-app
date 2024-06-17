// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reports.functions;

import java.io.*;
import java.util.regex.*;

class StreamReader extends Thread {
	private InputStream is;
	private StringWriter sw;

	StreamReader(InputStream is) {
		this.is = is;
		sw = new StringWriter();
	}

	public void run() {
		try {
			int c;
			while ((c = is.read()) != -1)
				sw.write(c);
		} catch (IOException e) {
			;
		}
	}

	String getResult() {
		return sw.toString();
	}
}

public class JdbcParameters {

	/**
	 * @param parentkeypath Path of the parent
	 * @param key           Key of the string value
	 * @return Found string key value or null in case of error
	 */
	private static String getRegStringValue(String parentkeypath, String key) {
		final String regquery = "reg query ";
		String result = getRegStringValue(regquery, parentkeypath, key, true);
		if (null == result) { // Alternative: 32 bits
			return getRegStringValue(regquery, parentkeypath, key, false);
		}
		return result;
	}

	/**
	 * @param regquery
	 * @param parentkeypath
	 * @param x64
	 * @param key
	 * @return
	 */
	private static String getRegStringValue(String regquery, String parentkeypath, String key, boolean x64) {
		final String regstr_token = "REG_SZ";

		String reg3264 = "";
		if (x64) {
			reg3264 = " /reg:64";
		} else {
			reg3264 = " /reg:32";
		}

		try {
			Process process = Runtime.getRuntime().exec(regquery + parentkeypath + reg3264 + " /v " + key);
			StreamReader reader = new StreamReader(process.getInputStream());

			reader.start();
			process.waitFor();
			reader.join();

			String result = reader.getResult();
			int p = result.indexOf(regstr_token);

			if (p == -1) {
				return null;
			} else {
				return result.substring(p + regstr_token.length()).trim();
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return the value of the environment variable "pulsejdbcurl" or null
	 */
	public static String getEnvUrl() {
		String jdbcurl = null;
		try {
			jdbcurl = System.getenv("pulsejdbcurl");
		} catch (Exception e) {
			jdbcurl = null;
		}
		return jdbcurl;
	}

	/**
	 * @return the JDBC URL
	 */
	public static String getLemoineGDBUrl() {
		String result = getEnvUrl();
		if (null == result) {
			result = getLemoineGDBUrlFromDbConnectionKey();
		}
		if (null == result) {
			result = getLemoineGDBUrlFromOdbcWithCmd();
		}
		return result;
	}

	/**
	 * @return the JDBC URL
	 */
	public static String getLemoineGDBUrlWithCmd() {
		return getLemoineGDBUrl();
	}
	
	/**
	 * @return the library path
	 */
	public static String getLibraryPath() {
		return System.getProperty("java.library.path");
	}

	/**
	 * @return the JDBC URL taken from the DSN Name atracking with the help of the
	 *         reg.exe command
	 */
	public static String getLemoineGDBUrlFromOdbcWithCmd() {
		String host;
		String port = "5432";
		String database = "DatabaseName";
		final String DSNName = "atracking";
		final String parentkeypath = "\"HKLM\\SOFTWARE\\ODBC\\ODBC.INI\\" + DSNName + "\"";

		String result = getRegStringValue(parentkeypath, "Servername");
		if (null == result) {
			return null;
		} else { // result != null
			host = result;
		}

		result = getRegStringValue(parentkeypath, "Port");
		if (result != null) {
			port = result;
		}

		result = getRegStringValue(parentkeypath, "Database");
		if (result != null) {
			database = result;
		}

		return getJdbcUrl(host, port, database);
	}

	static String getJdbcUrl(String host, String port, String database) {
		return "jdbc:postgresql://" + host + ":" + port + "/" + database;
	}

	public static String getLemoineGDBUrlFromDbConnectionKey() {
		String dbConnectionValue = getRegStringValue("\"HKLM\\SOFTWARE\\Atsora\\Tracking\"", "DbConnection.Atsora");
		if (null == dbConnectionValue) {
			return null;
		}
		Pattern p = Pattern.compile("((\\w+)(:(\\w+))?@)?([^@]*)");
		Matcher m = p.matcher(dbConnectionValue);
		if (m.matches()) {
			String hostPortDatabase = m.group(5);
			String user = m.group(2);
			String password = m.group(4);
			String jdbcUrl = "jdbc:postgresql://" + hostPortDatabase;
			if (null != user) {
				jdbcUrl += "?user=" + user;
				if (null != password) {
					jdbcUrl += "&password=" + password;
				}
			}
			return jdbcUrl;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Library path: " + getLibraryPath());

		System.out.println("getEnvUrl: " + getEnvUrl());
		System.out.println("getLemoineGDBUrl: " + getLemoineGDBUrl());
		System.out.println("getLemoineGDBUrlFromDbConnectionKey: " + getLemoineGDBUrlFromDbConnectionKey());
		System.out.println("getLemoineGDBUrlFromOdbcWithCmd: " + getLemoineGDBUrlFromOdbcWithCmd());
	}

}
