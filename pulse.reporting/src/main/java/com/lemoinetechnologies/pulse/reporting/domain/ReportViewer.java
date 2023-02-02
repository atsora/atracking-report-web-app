// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportViewer extends ReportTemplate
{
	protected Logger logger = LogManager.getLogger(getClass());	

	// Viewing session id
	String _viewingSessionId = null;
	public String getViewingSessionId() { return _viewingSessionId; }

	// Number of pages
	int _totalPage = 0;
	public int getTotalPage() { return _totalPage; }
	public void setTotalPage(int totalPage) { _totalPage = totalPage; }

	// Bookmarks of report
	private TableOfContent _toc = null;
	public TableOfContent getToc() { return _toc; }
	public void setToc(TableOfContent toc) { _toc = toc; }

	public ReportViewer(ReportTemplate reporttemplate, String viewingSessionId)
	{
		super(reporttemplate);
		_viewingSessionId = viewingSessionId.toUpperCase();
	}
	
	@Override public String toString()
	{
		return "ReportViewer [viewingSessionId=" + _viewingSessionId +
				", toc=" + _toc + ", scalarParameters=" + getScalarParameters() + ", groupParameters=" + getGroupParameters() + ", id=" + _id + "]";
	}
}
