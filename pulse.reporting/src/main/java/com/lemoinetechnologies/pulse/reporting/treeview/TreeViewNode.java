// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.treeview;

import java.util.LinkedList;

public class TreeViewNode {

	public enum State {
		/**
		 * 
		 */
		CHECKED,
		/**
		 * 
		 */
		UNCHECKED,
		/**
		 * 
		 */
		INTERMEDIATE;
	};

	String key;
	String label;
	String display;
	int level;
	State state;
	TreeViewNode parent;
	LinkedList<TreeViewNode> children;

	public TreeViewNode(String key, String label, int level, State state) {
		this.key = key;
		this.label = label;
		this.level = level;
		this.state = state;
		parent = null;
		children = new LinkedList<TreeViewNode>();
	}

	public TreeViewNode(String key, String label, int level)
	{
		this(key, label, level, State.UNCHECKED);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public TreeViewNode getParent() {
		return parent;
	}

	public void setParent(TreeViewNode parent) {
		this.parent = parent;
	}

	public LinkedList<TreeViewNode> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<TreeViewNode> children) {
		this.children = children;
	}

	public void addChild(TreeViewNode child) {
		this.children.add(child);
	}

	@Override
  public String toString() {
	  return "TreeViewNode [key=" + key + ", label=" + label + ", display=" + display + ", level=" + level + ", state=" + state + "]";
  }
}
