// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.domain;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportTree
{
	static Logger logger = LogManager.getLogger(ReportTree.class);
  
	String baseDir;
	public String getBaseDir() { return baseDir; }
	public void setBaseDir(String baseDir) { this.baseDir = baseDir; }
  
	ReportNode root;
	public ReportNode getRoot() { return root; }
	public void setRoot(ReportNode root) { this.root = root; }
  
	Map<String,Report> reportMap;
	public Map<String, Report> getReportMap() { return reportMap; }
	public void setReportMap(Map<String, Report> reportMap) { this.reportMap = reportMap; }
	public Report getReport(String reportName) { return reportMap.get(reportName); }
	
	public ReportTree() {}

	public ReportTree(String baseDir)
  {
		this.baseDir = baseDir;
		root = null;
	}
}
