// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ReportNode
{
	// Node comparator for being able to sort them
	static class MyComparator implements Comparator<ReportNode>
	{
		public int compare(ReportNode o1, ReportNode o2)
		{
			// First compare the name
			int result = o1._name.compareTo(o2._name);

			// Or possibly compare the id
			if (result == 0)
				result = o1._id.compareTo(o2._id);
			
		  return result;
		}
	}
	static final MyComparator COMPARATOR = new MyComparator();

	String _name;
	public String getName() { return _name; }
	
	String _id;
	public String getId() { return _id; }
	
	List<ReportNode> _children;
	public List<ReportNode> getChildren() { return _children; }
	
	public ReportNode(String name, String id)
	{
		_name = name;
		_id = id;
		_children = new LinkedList<ReportNode>();
	}

	public ReportNode getChild(int i)
	{
		return i < _children.size() ? _children.get(i) : null;
	}

	public void addChild(ReportNode node)
	{
		_children.add(node);
	}

	public int childCount()
	{
		return _children.size();
	}

	public void sort()
	{
		Collections.sort(_children, ReportNode.COMPARATOR);
		for (ReportNode child : _children)
			child.sort();
	}
}
