// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

import com.lemoinetechnologies.pulse.reporting.birt.BirtEngineHelper;
import com.lemoinetechnologies.pulse.reporting.birt.Configuration;
import com.lemoinetechnologies.pulse.reporting.util.Utils;

public class MyDispatcherServlet extends DispatcherServlet {
  /**
	 * 
	 */
  private static final long serialVersionUID = 7096326636053087354L;
  protected static Logger LOGGER = LogManager.getLogger(MyDispatcherServlet.class);

  @Override
  protected void initFrameworkServlet() throws ServletException {
    super.initFrameworkServlet();
    
    String configfile = this.getInitParameter("pulse.config.file");
    System.out.println("pulse.config.file : " + configfile);
    LOGGER.debug("pulse.config.file : " + configfile);
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
    LOGGER.debug("pulse.config.file : " + configfile);
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
