// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jHandler extends Handler {
  
  private static Logger LOGGER = LogManager.getLogger(Log4jHandler.class);
  /*
  public Log4jHandler() {
    super();
    setFormatter(new SimpleFormatter());
    LOGGER.debug("Constructor");
  }
*/
  public void publish(LogRecord record) {
    if((null != record) &&  (null != record.getSourceClassName())){
      Logger log = LogManager.getLogger(record.getLoggerName());
      Level level = record.getLevel();
      String message;
      if(getFormatter() == null){
        message = record.getMessage();
      }
      else {
        message = getFormatter().format(record);
      }

      //LOGGER.debug("Log level="+level.toString()+" message : "+message);

      
      if((null == log) || (null == level)){
        LOGGER.warn("Exit function because logger or level is null");
        return;
      }
      
      if (Level.ALL.equals(level)) {
        log.debug(message);
      }
      else if (Level.FINEST.equals(level)) {
        log.debug(message);
      }
      else if (Level.FINER.equals(level)) {
        log.debug(message);
      }
      else if (Level.FINE.equals(level)) {
        LOGGER.debug(message);
        log.debug(message);
      }
      else if (Level.CONFIG.equals(level)) {
        log.debug(message);
      }
      else if (Level.INFO.equals(level)) {
        log.info(message);
      }
      else if (Level.WARNING.equals(level)) {
        log.warn(message);
      } 
      else if (Level.SEVERE.equals(level)) {
        log.error(message);
      }
      else if (Level.OFF.equals(level)) {
        LOGGER.warn("Log level is OFF");
      }
      else{
        LOGGER.warn("Log level is unknown");
      }      
      
    }
    else {
      LOGGER.warn("Exit function because record or record.getSourceClassName is null");
    }

  }
  
  @Override
  public void flush() {
    // TODO Auto-generated method stub

  }
  @Override
  public void close() throws SecurityException {
    // TODO Auto-generated method stub
    
  }
}
