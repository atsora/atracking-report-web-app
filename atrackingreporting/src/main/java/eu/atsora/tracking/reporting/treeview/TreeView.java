// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.treeview;

import java.util.LinkedList;
import java.util.List;

public class TreeView {

	public static final String SEP = " - ";
	List<TreeViewNode> roots;
	int size;
	boolean singleSelection;

	public TreeView() {
		roots = new LinkedList<TreeViewNode>();
	}

	public List<TreeViewNode> getRoots() {
		return roots;
	}

	public void setRoots(List<TreeViewNode> roots) {
		this.roots = roots;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean isSingleSelection() {
		return singleSelection;
	}

	public void setSingleSelection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}

	public void addRoot(TreeViewNode node) {
		roots.add(node);
	}

	@Override
	public String toString() {
		String txt = "";
		for (TreeViewNode rootNode : roots) {
			txt = txt + '\n' + rootNode.display;
			for (TreeViewNode childNode : rootNode.children) {
				displayDescendant(txt, childNode, 2);
			}
		}
		return txt;
	}

	private void displayDescendant(String txt, TreeViewNode node, int level) {
		txt = txt + '\n';
		for (int i = 0; i > (level - 1); i++) {
			txt = txt + '\t';
		}
		txt = txt + node.display;
		for (TreeViewNode childNode : node.children) {
			displayDescendant(txt, childNode, level + 1);
		}
	}
}
