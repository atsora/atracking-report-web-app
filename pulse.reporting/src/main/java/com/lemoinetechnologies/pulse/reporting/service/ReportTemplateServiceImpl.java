// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
/*
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.model.api.ExpressionHandle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lemoinetechnologies.pulse.reporting.birt.BirtEngineHelper;
import com.lemoinetechnologies.pulse.reporting.birt.Configuration;
import com.lemoinetechnologies.pulse.reporting.domain.Report;
import com.lemoinetechnologies.pulse.reporting.domain.ReportNode;
import com.lemoinetechnologies.pulse.reporting.domain.ReportTree;
import com.lemoinetechnologies.pulse.reporting.util.Utils;

@Transactional
@Service("reportTemplateService")
public class ReportTemplateServiceImpl {

  static Logger LOGGER = LogManager.getLogger(ReportTemplateServiceImpl.class);

  // Number based on the last modified date of all files in the DESIGN_FOLDER
  // Useful to know when we should update the report list
  int _magicNumber = 1;

  public synchronized ReportTree getReportTree() throws NullPointerException, SecurityException, EngineException, IOException, BirtException
  {
    // Detect if there is a change
    int currentMagicNumber = getMagicNumber();

    if (Configuration.reportTree == null || _magicNumber != currentMagicNumber)
    {
      LOGGER.debug("Building report tree, the design folder being '" + Configuration.DESIGN_FOLDER + "'");

      File dir = new File(Configuration.DESIGN_FOLDER);
      if (!dir.exists())
      {
        LOGGER.debug("Report design directory not found: " + dir.getAbsolutePath());
        return null;
      }
      if (!dir.isDirectory())
      {
        LOGGER.error("Report design directory is not a directory: " + dir.getAbsolutePath());
        return null;
      }

      // Build the different nodes for the menu and the map for calling reports
      Map<String, Report> map = new TreeMap<String, Report>();
      ReportNode rootNode = new ReportNode("/", Configuration.DESIGN_FOLDER);
      buildReportTree(rootNode, map);
      
      Configuration.reportTree = new ReportTree(Configuration.DESIGN_FOLDER);
      Configuration.reportTree.setRoot(rootNode);
      Configuration.reportTree.setReportMap(map);

      LOGGER.debug("Successfully built the report tree");

      // Compute the magic number again (since thumbnails might have been created)
      _magicNumber = getMagicNumber();
    }

    return Configuration.reportTree;
  }

  public synchronized ReportTree refreshApplicationSettings(String contextpath, String configfile) throws BirtException, IOException
  {
    LOGGER.debug("Refreshing application setting: context path = " + contextpath + ", configuration file = " + configfile);
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(contextpath + configfile));
    Properties properties = new Properties();
    properties.load(in);
    in.close();
    Configuration.initConfigSettings(properties, contextpath);

    Configuration.reportTree = null; // So that the report tree is rebuilt
    return getReportTree();
  }

  public Report getReportTemplate(String reportId) throws NullPointerException, SecurityException, EngineException, IOException, BirtException
  {
    LOGGER.debug("Getting report with name: '" + reportId + "'");
    return getReportTree().getReport(reportId);
  }

  // Build the tree with the help of a description file "report_categories.txt" made of several lines like:
  // ReportName=Category1,Category2, ...
  // A report possibly pertains to several categories
  private void buildReportTree(ReportNode rootNode, Map<String, Report> map) throws EngineException, IOException
  {
    // Get the report list by category
    Map<String, String[] > mapCategoryReports = getReportCategories();

    // Browse all files in DESIGN_FOLDER and sort reports in categories
    File dir = new File(Configuration.DESIGN_FOLDER);
    Map<String, ReportNode> categoryNodes = new TreeMap<String, ReportNode>();
    for (String file : dir.list())
    {
      // Skip if it's not a rptdesign
      if (!file.endsWith(".rptdesign"))
        continue;

      LOGGER.debug("Processing path '" + Configuration.DESIGN_FOLDER + "/" + file + "'");

      // Get the corresponding Report object
      Report report = getReportFromPath(Configuration.DESIGN_FOLDER + "/" + file);
      if (report == null)
        continue;

      // Add the report to the map
      String reportName = file.substring(0, file.length() - 10); // Extension removed
      map.put(reportName, report);

      // Prepare a node if the report is visible
      if (report.getIsVisible())
      {
        // Find the associated categories
        String[] categories = mapCategoryReports.containsKey(reportName) ? mapCategoryReports.get(reportName) : new String[] { "Uncategorized" };
        
        // Possibly skip the report
        if (categories.length == 1 && categories[0].toLowerCase().compareTo("hidden") == 0)
          continue;
        
        // Loop over all categories and create the tree
        for (String category : categories)
        {
          // Possibly create a new category?
          if (!categoryNodes.containsKey(category))
            categoryNodes.put(category, new ReportNode(category, ""));

          // Create a node for the report
          String reportDisplayedName = report.getTitle();
          if (reportDisplayedName == null || reportDisplayedName.equals(""))
            reportDisplayedName = report.getId();
          categoryNodes.get(category).addChild(new ReportNode(reportDisplayedName, report.getId()));
        }
      }
    }

    // Attach all category nodes to the parent node and sort the entire tree
    for (ReportNode categoryNode : categoryNodes.values())
      rootNode.addChild(categoryNode);
    rootNode.sort();
  }

  Report getReportFromPath(String reportPath)
  {
    // Read the report file
    IReportRunnable reportRunnable;
    try
    {
      reportRunnable = BirtEngineHelper.getReportEngine().openReportDesign(reportPath);        
    }
    catch (Exception e)
    {
      LOGGER.error("Cannot read the report design '" + reportPath + "': " + Utils.getStackTraceAsString(e));
      return null;
    }

    // Extract the report id (name of the file without the extension)
    String reportId = reportPath.substring(reportPath.replace("\\", "/").lastIndexOf("/") + 1); // Possibly remove the directory
    if (reportId.endsWith(".rptdesign")) // Possibly remove the extension
      reportId = reportId.substring(0, reportId.length() - 10);

    // Prepare a report object and fill properties
    Report report = new Report(reportId);
    report.setTitle((String)reportRunnable.getProperty("title"));
    report.setDescription((String)reportRunnable.getProperty("description"));
    report.setComment((String)reportRunnable.getProperty("comments"));
    report.setThumbnailPath(getThumbnailPath(new File(reportPath), reportRunnable)); // This line also creates the thumbnail as a .png file

    // Visibility of the report in the treeview
    ExpressionHandle eh = reportRunnable.getDesignHandle().getExpressionProperty("IS_VISIBLE");
    if (eh != null)
      report.setIsVisible(eh.getStringValue().toLowerCase().equals("true"));

    return report;
  }

  private Map<String, String[] > getReportCategories()
  {
    // Location of the file path describing the categories
    String rootDirectory = Configuration.DESIGN_FOLDER.replace('\\', '/');
    if (!rootDirectory.endsWith("/"))
      rootDirectory += "/";

    // Read the different description files and return the result
    Map<String, String[] > result = new TreeMap<String, String[] >();
    getReportCategories(result, rootDirectory + "report_categories_default.txt", true);
    getReportCategories(result, rootDirectory + "report_categories_installer.txt", false);
    getReportCategories(result, rootDirectory + "report_categories_custom.txt", false);
    return result;
  }

  private void getReportCategories(Map<String, String[] > mapReportCategories, String descFilePath, boolean mandatory)
  {
    // Open the file
    File f = new File(descFilePath);
    if (!f.exists())
    {
      if (mandatory)
        LOGGER.error("File '" + descFilePath + "' doesn't exist");
      return;
    }
    if (!f.canRead())
    {
      if (mandatory)
        LOGGER.error("File '" + descFilePath + "' has been found but cannot be read");
      return;
    }

    LOGGER.info("Reading file '" + descFilePath + "' to define the categories");

    // Read line by line and store the category list for each report
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
      String strCurrentLine;
      while ((strCurrentLine = bufferedReader.readLine()) != null)
      {
        // Skip empty lines and comments
        if (strCurrentLine.trim().isEmpty() || strCurrentLine.startsWith("//"))
          continue;
        
        // Read and store the elements
        String[] parts = strCurrentLine.split("=");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty())
          LOGGER.error("Following line is not in the form 'X=Y': " + strCurrentLine);
        else
          mapReportCategories.put(parts[0], parts[1].split(","));
      }
      bufferedReader.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Error while processing file '" + descFilePath + "': " + e.toString());
    }
  }

  private String getThumbnailPath(File reportFile, IReportRunnable report)
  {
    // Get current os seperator.
    String fileSep = File.separator;

    // A report template and its thumbnail are in the same folder, have same name but different extensions (.rptdesign, .png)
    String thumbnailPath = reportFile.getAbsolutePath().replaceAll(".rptdesign", ".png");
    
    int index = thumbnailPath.lastIndexOf(fileSep);
    String parentPath = thumbnailPath.substring(0, index);
    String imgName = thumbnailPath.substring(index+1);

    String thumbnailConfig = Configuration.THUMBNAIL_FOLDER;
    if ( (thumbnailConfig == null) || (thumbnailConfig == "") ) {
      thumbnailConfig = parentPath; // Default = next to reports
    }
    if ( (thumbnailConfig != null) && (thumbnailConfig != "") ) {
      // Here, it should be. USE IT !
      try {
        File dir = new File(thumbnailConfig);
        if (!dir.exists()) {
          LOGGER.error("Report design THUMBNAIL_FOLDER not found - NO preview : " + dir.getAbsolutePath());
          return "";
        }
        else {
          if (!dir.isDirectory()) {
            LOGGER.error("Report design THUMBNAIL_FOLDER is not a directory - NO preview: " + dir.getAbsolutePath());
            return "";
          }
        }
        if ( !dir.canWrite()) {
          LOGGER.error("Report design THUMBNAIL_FOLDER is NOT writable - check rights - " + dir.getAbsolutePath());
          return "";
        }
      } catch (Exception e) {      
        LOGGER.error("Exception THUMBNAIL_FOLDER '" + e + "'");
        return "";
      }
    }

    thumbnailPath = thumbnailConfig + fileSep + imgName;

    // If the thumbnail is old: we update it
    File thumbnailFile = new File(thumbnailPath);
    if (!thumbnailFile.exists() || thumbnailFile.lastModified() < reportFile.lastModified())
    {
      if (thumbnailFile.exists()){
        thumbnailFile.delete();
      }

      // Is it possible to extract it?
      if (report.getProperty("thumbnail") == null) {
        LOGGER.warn("Thumbnail is not defined - CLEAN path (" + imgName + ")");
        thumbnailPath = "";
        return "";
      } 
      else{
        try {
          byte[] byteArray = ((String)report.getProperty("thumbnail")).getBytes("ISO-8859-1");
          int length = Math.round(byteArray.length/8) * 8 + 8;
          thumbnailFile.createNewFile();
          thumbnailFile.setReadable(true);
          thumbnailFile.setWritable(true);
          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(thumbnailPath), length);
           //thumbnailFile));
          bos.write(byteArray); // ((String)report.getProperty("thumbnail")).getBytes("ISO-8859-1"));
          bos.flush();
          bos.close();
        } catch (Exception e) {
          LOGGER.error("Couldn't create thumbnail '" + thumbnailPath + "'");
          LOGGER.error("Exception thumbnail '" + e + "'");
        } // end catch
      }
    }

    // Return the path
    return thumbnailPath.replace('\\', '/');
  }

  private int getMagicNumber()
  {
    int magicNumber = 1;

    try {
      File dir = new File(Configuration.DESIGN_FOLDER);
      for (String fileName : dir.list())
      {
        File file = new File(Configuration.DESIGN_FOLDER + "/" + fileName);
        if (file.isFile())
          magicNumber = 1 + (magicNumber * (int)(file.lastModified() % 2147483647));
      }

      LOGGER.debug("Magic number is " + magicNumber);
    }
    catch (Exception e)
    {
      LOGGER.error("Couldn't compute a magic in folder '" + Configuration.DESIGN_FOLDER + "': " + e.toString());
    }

    return magicNumber;
  }
}
