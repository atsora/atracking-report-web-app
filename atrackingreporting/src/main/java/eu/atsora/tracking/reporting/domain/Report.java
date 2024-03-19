// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.domain;
import java.io.File;
import eu.atsora.tracking.reporting.birt.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Report
{
  protected Logger logger = LogManager.getLogger(getClass());

  // Corresponding file path
  protected String _filePath;
  public String getFilePath() { return _filePath; }

  // Id
  protected String _id;
  public String getId() { return _id; }

  // Title
  protected String _title;
  public String getTitle() { return _title; }
  public void setTitle(String title) { _title = title; }

  // Description
  protected String _description;
  public String getDescription() { return _description; }
  public void setDescription(String description) { _description = description; }

  // Comment
  protected String _comment;
  public String getComment() { return _comment; }
  public void setComment(String comment) { _comment = comment; }

  // Thumbnail path
  protected String _thumbnailPath;
  public String getThumbnailPath() { return _thumbnailPath; }
  public void setThumbnailPath(String thumbnailPath) { _thumbnailPath = thumbnailPath; }
  
  /**
   * True if the report is visible in the tree
   * False otherwise (sub-report accessible from another report)
   */
  protected boolean _isVisible;
  public boolean getIsVisible() { return _isVisible; }
  public void setIsVisible(boolean isVisible) { _isVisible = isVisible; }
  
  /**
   * Default constructors
   */
  public Report(String reportId)
  {
    _id = reportId;
    _isVisible = true;
    
    // Store the original filepath (absolute file path)
    File file = new File(Configuration.DESIGN_FOLDER + "/" + reportId + ".rptdesign");
    _filePath = file.getAbsolutePath();
  }

  public Report(Report other)
  {
    _filePath = other._filePath;
    _id = other._id;
    _title = other._title;
    _description = other._description;
    _comment = other._comment;
    _thumbnailPath = other._thumbnailPath;
    _isVisible = other._isVisible;
  }
  
  @Override public String toString()
  {
    return "Report [id=" + _id + ", title=" + _title + ", visibility=" + _isVisible + "]";
  }
}
