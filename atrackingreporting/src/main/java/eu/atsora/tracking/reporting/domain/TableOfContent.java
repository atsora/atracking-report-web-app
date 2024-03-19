// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.domain;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TableOfContent implements Comparable<TableOfContent> {

	static Logger logger = LogManager.getLogger(TableOfContent.class);

	/*
	 * Node id such as given in report template
	 */
	private String nodeId;

	/*
	 * Texte to be displayed in table of content
	 */
	private String displayText;

	/*
	 * bookmark such as given in report template
	 */
	private String bookmark;

	/*
	 * Children bookmark list of current bookmark
	 */
	private List<TableOfContent> children;

	/**
	 * page number pointed to by this bookmark
	 */
	private int pageNumber;

	public TableOfContent() {
	}

	public TableOfContent(String nodeId, String displayText, String bookmark, int pageNumber) {
		super();
		this.nodeId = nodeId;
		this.displayText = (displayText == null) ? "" : displayText;
		this.bookmark = (bookmark == null) ? "" : bookmark;
		this.pageNumber = pageNumber;
		this.children = new ArrayList<TableOfContent>();
	}

	/**
	 * Get node id
	 * 
	 * @return node id
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * Set node id
	 * 
	 * @param nodeId
	 *          node id
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * Get display text
	 * 
	 * @return display text
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * Set display text
	 * 
	 * @param displayText
	 *          display text
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * Get bookmark text
	 * 
	 * @return bookmark text
	 */
	public String getBookmark() {
		return bookmark;
	}

	/**
	 * Set bookmark text
	 * 
	 * @param bookmark
	 *          bookmark text
	 */
	public void setBookmark(String bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * Get page number
	 * 
	 * @return page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * Set page number
	 * 
	 * @param pageNumber
	 *          page number
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * Get list of children
	 * 
	 * @return list of children
	 */
	public List<TableOfContent> getChildren() {
		return children;
	}

	/**
	 * Set list of children
	 * 
	 * @param children
	 *          list of children
	 */
	public void setChildren(List<TableOfContent> children) {
		this.children = children;
	}

	/**
	 * Add table of content in children list
	 * 
	 * @param toc
	 *          child table of content
	 */
	public void addChild(TableOfContent toc) {
		children.add(toc);
	}

	@Override
	public int compareTo(TableOfContent toc) {
		int p = toc.getPageNumber();
		if (this.pageNumber < p)
			return -1;
		else if (this.pageNumber > p)
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		String str = "TableOfContent [nodeId=" + nodeId + ", displayText=" + displayText + ", bookmark=" + bookmark + ", pageNumber=" + pageNumber + "]";
		return str;
	}

	public void display() {
		logger.info(this.toString());
		for (TableOfContent toc : children) {
			toc.display();
		}
	}

}
