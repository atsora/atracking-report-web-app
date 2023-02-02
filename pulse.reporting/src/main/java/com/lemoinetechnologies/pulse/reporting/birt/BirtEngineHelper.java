// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.birt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EmitterInfo;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import com.lemoinetechnologies.pulse.reporting.util.EmitterUtil;
import com.lemoinetechnologies.pulse.reporting.util.Log4jHandler;
import com.lemoinetechnologies.pulse.reporting.util.Utils;

public class BirtEngineHelper {
  static Logger LOGGER = LogManager.getLogger(BirtEngineHelper.class.getName());

  /**
   * BIRT report engine. this is a singleton used in the program
   */
  private static IReportEngine reportEngine;

  /**
   * Create the report engine if it is not initialize, else just return it
   * 
   * @param sc
   *          Servlet context
   * @return BIRT report engine
   * @throws BirtException
   * @throws Exception
   * @see com.lemoinetechnologies.pulse.report.web.client.exception.BirtException
   *      BirtException
   */
  @SuppressWarnings("unchecked")
  public static synchronized IReportEngine getReportEngine() throws BirtException {
    if (reportEngine == null) {
      try {
        EngineConfig config = new EngineConfig();
        config.setResourcePath(Configuration.BIRT_RESOURCE_PATH);
        config.setLogRollingSize(Configuration.BIRT_LOG_ROLLING_SIZE);
        config.setLogMaxBackupIndex(Configuration.BIRT_LOG_MAX_BACKUP_INDEX);
        config.getAppContext().put(EngineConstants.WEBAPP_CLASSPATH_KEY, "");
        config.getAppContext().put(EngineConstants.APPCONTEXT_DATASET_CACHE_OPTION, true);

        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = factory.createReportEngine(config);
        //reportEngine.getLogger().addHandler(new Log4jHandler());
        LOGGER.debug("BIRT report engine was created");
        
        if(null != config.getLogger()){
          config.getLogger().addHandler(new Log4jHandler());
        }
        else {
          LOGGER.debug("EngineConfig logger is null!");
          reportEngine.getLogger().setLevel(Utils.getBirtLogLevel(Configuration.BIRT_LOG_LEVEL));
          LOGGER.debug("ReportEngine log level : "+ reportEngine.getLogger().getName()+"-"+ reportEngine.getLogger().getLevel()+"-"+reportEngine.getLogger().getHandlers().length);
          reportEngine.getLogger().addHandler(new Log4jHandler());
          LOGGER.debug("Handler length "+ reportEngine.getLogger().getHandlers().length);
        }

        
        String m = "Lists of supported formats: ";
        for(String s : reportEngine.getSupportedFormats()){
          m += s+"  ";
          EmitterUtil.available_format.add(s);
        }
        
        m += "\n\nEmitter infos : ";
        for(EmitterInfo e : reportEngine.getEmitterInfo()){
          m += "\n- "+ getEmitterInfo(e);
          EmitterUtil.emitterInfos.add(e);
        }
        m+="\n\n";
        LOGGER.debug(m);
        
      }
      catch (BirtException e) {
        LOGGER.error("BIRT report engine creation failed \n\n " + Utils.getStackTraceAsString(e));
        throw e;
      }

      if (reportEngine == null) {
        LOGGER.error("BIRT report engine is null after its creation.");
      }
    }
    return reportEngine;
  }

  /**
   * Destroy BIRT report engine and shutdown platform
   */
  public static synchronized void destroyReportEngine() {
    if (reportEngine != null) {
      reportEngine.destroy();
      reportEngine = null;
    }
    Platform.shutdown();
    LOGGER.debug("BIRT report engine is destroyed");
  }

  
  private static String getEmitterInfo(EmitterInfo e) {
    String m ="";
    String attr = "  Emitter: [name="+e.getEmitter().getName()+", value="+e.getEmitter().getValue()+"  Attribute=";
    for(String a : e.getEmitter().getAttributeNames()){
      attr += a+":"+e.getEmitter().getAttribute(a)+" ";
    }
    attr += " ]";
    m += " ID:"+e.getID()+"  Format:"+e.getFormat()+"  File extension:"+e.getFileExtension()
        +"  Namespace:"+e.getNamespace()+"  Mime type:"+e.getMimeType()
        +", "+attr;
    /*
    try {
      Class.forName(e.getEmitter().getAttribute("class"));
    }
    catch (ClassNotFoundException e1) {
      LOGGER.warn(Utils.getStackTraceAsString(e1));
    }
    */
    return m;
  }
  
}
