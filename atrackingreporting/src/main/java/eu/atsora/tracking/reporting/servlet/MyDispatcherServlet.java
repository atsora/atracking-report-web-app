// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

import eu.atsora.tracking.reporting.birt.BirtEngineHelper;
import eu.atsora.tracking.reporting.birt.Configuration;
import eu.atsora.tracking.reporting.util.Utils;

public class MyDispatcherServlet extends DispatcherServlet {
  /**
	 * 
	 */
  private static final long serialVersionUID = 7096326636053087354L;
  protected static Logger LOGGER = LogManager.getLogger(MyDispatcherServlet.class);

  @Override
  protected void initFrameworkServlet() throws ServletException {
    super.initFrameworkServlet();
    
    String configfile = this.getInitParameter("atracking.config.file");
    System.out.println("atracking.config.file : " + configfile);
    LOGGER.debug("atracking.config.file : " + configfile);
    String contextpath = this.getServletContext().getRealPath("/") + File.separator;
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(contextpath + configfile));
      Properties properties = new Properties();
      properties.load(in);
      in.close();
      Configuration.initConfigSettings(properties, contextpath);
    }
    catch (Exception e) {
      LOGGER.error(Utils.getStackTraceAsString(e));
    }
    finally {
      if (null != in) {
        try {
          in.close();
        }
        catch (IOException e) {
          LOGGER.error(Utils.getStackTraceAsString(e));
        }
      }
    }
    LOGGER.debug("atracking.config.file : " + configfile);
  }

  @Override
  public void destroy() {
    if(Configuration.reportTree != null){
      Configuration.reportTree.getReportMap().clear();
      Configuration.reportTree = null;
    }
    super.destroy();
    BirtEngineHelper.destroyReportEngine();
    LOGGER.warn("Report engine has been destroyed.");
  }

}
