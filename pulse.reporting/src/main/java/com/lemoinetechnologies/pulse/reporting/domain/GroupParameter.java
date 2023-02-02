// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lemoinetechnologies.pulse.reporting.domain.ScalarParameter.ParameterType;
import com.lemoinetechnologies.pulse.reporting.treeview.TreeView;
import com.lemoinetechnologies.pulse.reporting.treeview.TreeViewNode;
import com.lemoinetechnologies.pulse.reporting.util.Utils;

public class GroupParameter implements Serializable, Comparable<GroupParameter>
{
  private static final long serialVersionUID = -4811340327779978450L;

  static Logger logger = LogManager.getLogger(GroupParameter.class);

  // name of group of parameters
  String _name;
  public String getName() { return _name; }

  // Display name of group
  String _displayName;
  public String getDisplayName() { return _displayName; }
  public void setDisplayName(String displayName) { _displayName = displayName; }

  // Text used to label group of parameters
  String _promptText;
  public String getPromptText() { return (_promptText != null && !_promptText.trim().isEmpty()) ? _promptText : _displayName; }
  public void setPromptText(String promptText) { _promptText = promptText; }

  // Type of the group of parameter, which give display form of this group of parameters
  String _displayForm;
  public String getDisplayForm() { return _displayForm; }

  // True if the group should be hidden
  boolean _isHidden;
  public boolean isHidden() { return _isHidden; }

  // Help Text
  String _helpText;
  public String helpText() { return _helpText; }
  public String getHelpText() { return _helpText; }
  public void setHelpText(String helpText) { _helpText = helpText; }

  /**
   * Map scalar parameter's name with ReportParameter instance It concern scalar
   * parameter belonging to this group
   */
  LinkedHashMap<String, ScalarParameter> _scalarParameterMap;
  public LinkedHashMap<String, ScalarParameter> getScalarParameterMap() { return _scalarParameterMap; }
  public ScalarParameter getScalarParameter(String index) { return _scalarParameterMap.get(index); }

  public GroupParameter(String name, String displayForm, boolean isHidden)
  {
    _name = name;
    _displayForm = displayForm;
    _isHidden = isHidden;
    _scalarParameterMap = new LinkedHashMap<String, ScalarParameter>();
  }

  public GroupParameter(GroupParameter gp)
  {
    // Copy properties
    _name = gp._name;
    _displayName = gp._displayName;
    _promptText = gp._promptText;
    _displayForm = gp._displayForm;
    _isHidden = gp._isHidden;

    // Copy the parameter list
    _scalarParameterMap = new LinkedHashMap<String, ScalarParameter>(gp._scalarParameterMap.size());
    for (String key : gp._scalarParameterMap.keySet())
      _scalarParameterMap.put(new String(key), new ScalarParameter(gp._scalarParameterMap.get(key)));
  }

  public int compareTo(GroupParameter groupParam)
  {
    return _name.compareTo(groupParam._name);
  }

  public TreeView getTreeView()
  {
	TreeView treeView = new TreeView();
	
    // Get first scalar parameter and create root elements
    ScalarParameter scalarParameter = getScalarParameter("1");
    Map<String, String> orderedSelectionList = Utils.sortMapByValue(scalarParameter.getSelectionList());
    orderedSelectionList.remove("-1");
    for (Map.Entry<String, String> entry : orderedSelectionList.entrySet()) {
      TreeViewNode rootNode = new TreeViewNode(entry.getKey(), entry.getValue(), 1);
      rootNode.setDisplay(entry.getValue());
      //logger.info("Root Node : "+entry.getValue()+" - level : "+1);
      buildDescendants(rootNode, 2);
      treeView.addRoot(rootNode);
    }
    treeView.setSize(getScalarParameterMap().size());
    scalarParameter = getScalarParameter(Integer.toString(treeView.getSize()));
    treeView.setSingleSelection(scalarParameter.getParameterType() == ParameterType.SIMPLE);
    
    return treeView;
  }

  private void buildDescendants(TreeViewNode parent, int childLevel)
  {
    if (childLevel > getScalarParameterMap().size()) {
      //logger.info("Exit with level : "+childLevel);
      return;
    }
    Map<String, String> childEntries = Utils.sortMapByValue(getScalarParameter(Integer.toString(childLevel)).getSelectionList());
    childEntries.remove("-1");
    for (Map.Entry<String, String> entry : childEntries.entrySet()) {
      if (isPrefixed(parent.getLabel(), entry.getValue())) {
        TreeViewNode treeViewNode = new TreeViewNode(entry.getKey(), entry.getValue(), childLevel);
        treeViewNode.setDisplay(entry.getValue().substring(parent.getLabel().length() + 3));
        parent.addChild(treeViewNode);
        //logger.info("Node : "+entry.getValue()+" - level : "+childLevel);
        buildDescendants(treeViewNode, childLevel + 1);
      }
      else {
        logger.info("No corresponding child: " + entry.getValue() + " for parent " + parent.getLabel());
      }
    }
  }

  private boolean isPrefixed(String prefix, String word)
  {
    if (prefix.length() >= word.length()) {
      return false;
    } else {
      String[] s1 = prefix.split(TreeView.SEP);
      String[] s2 = word.split(TreeView.SEP);
      if (s1.length >= s2.length) {
        return false;
      } else {
        for (int i = 0; i < s1.length; i++) {
          if (!(s1[i]).trim().equals(s2[i].trim())) {
            return false;
          }
        }
        return true;
      }
    }
  }

  public TreeView getTreeViewAsCompanyDepartmentMachine()
  {
    TreeView treeview = new TreeView();
    ScalarParameter scalarParameter_1 = getScalarParameter(Integer.toString(1));
    ScalarParameter scalarParameter_2 = getScalarParameter(Integer.toString(2));
    ScalarParameter scalarParameter_3 = getScalarParameter(Integer.toString(3));
    
    ArrayList<ArrayList<String>> branches = new ArrayList<ArrayList<String>>();
    for (String entry : scalarParameter_3.getSelectionList().values()) {
      String[] tab = entry.split(TreeView.SEP);
      if (tab.length == 3) {
        ArrayList<String> array = new ArrayList<String>(3);
        array.add(tab[0].trim());
        array.add(tab[1].trim());
        array.add(entry);
        branches.add(array);
      }
	  }
	
    // build root TreeNode only if it appears in branches list
    Map<String, String> entries = Utils.sortMapByValue(scalarParameter_1.getSelectionList());
    entries.remove("-1");
    for (Map.Entry<String, String> entry : entries.entrySet()) {
      boolean flag = false;
      for (ArrayList<String> arraylist : branches){
        if (arraylist.get(0).equals(entry.getValue())){
          flag = true;
          break;
        }
      }
      if (flag) {
        TreeViewNode treeViewNode = new TreeViewNode(entry.getKey(), entry.getValue(), 1);
        treeViewNode.setDisplay(entry.getValue());
        treeview.addRoot(treeViewNode);
      }
    }

    // build TreeNode at level 2
    entries = Utils.sortMapByValue(scalarParameter_2.getSelectionList());
    entries.remove("-1");
    for (Map.Entry<String, String> entry : entries.entrySet()) {
      // list of parent's label
      TreeSet<String> set = new TreeSet<String>();
      for(ArrayList<String> arraylist : branches) {
        if(arraylist.get(1).equals(entry.getValue())) {
          set.add(arraylist.get(0));
        }
	  }
	  
      // create TreeNode and attach it to all parent TreeNode
      for (TreeViewNode rootNode : treeview.getRoots()) {
        if (set.contains(rootNode.getLabel())) {
          TreeViewNode treeViewNode = new TreeViewNode(entry.getKey(), entry.getValue(), 2);
          treeViewNode.setDisplay(entry.getValue());
          rootNode.addChild(treeViewNode);
        }
      }
    }
    
    // build TreeNode at level 3
    entries = Utils.sortMapByValue(scalarParameter_3.getSelectionList());
    entries.remove("-1");
    for (Map.Entry<String, String> entry : entries.entrySet()) {
      for(ArrayList<String> arraylist : branches) {
        if(arraylist.get(2).equals(entry.getValue())) {
          for(TreeViewNode rootNode : treeview.getRoots()) {
            if (rootNode.getLabel().equals(arraylist.get(0))){
              for (TreeViewNode parentNode : rootNode.getChildren()) {
                if (parentNode.getLabel().equals(arraylist.get(1))) {
                  TreeViewNode treeViewNode = new TreeViewNode(entry.getKey(), entry.getValue(), 3);
                  treeViewNode.setDisplay(entry.getValue().split(TreeView.SEP)[2]);
                  parentNode.addChild(treeViewNode);
                  break;
                }
              }
              break;
            }
          }
          break;
        }
      }
    }
    treeview.setSize(getScalarParameterMap().size());
    
    ScalarParameter scalarParameter = getScalarParameter(Integer.toString(treeview.getSize()));
    treeview.setSingleSelection(scalarParameter.getParameterType() == ParameterType.SIMPLE);
    return treeview;
  }

  @Override
  public String toString()
  {
    return "GroupParameter [name=" + _name + ", displayName=" + _displayName + ", displayForm=" +
      _displayForm + ", scalarParameterMap=" + _scalarParameterMap + "]";
  }
}
