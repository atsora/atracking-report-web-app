// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.util;

import java.util.logging.Level;

import org.eclipse.birt.core.exception.BirtException;

import com.lemoinetechnologies.pulse.reporting.birt.BirtEngineHelper;

public class BirtLogger {

  public static void log(String level, String message){
    Level logLevel = Utils.getBirtLogLevel(level);
    if(null ==logLevel){
      logLevel = Level.INFO;
    } 
      try {
        BirtEngineHelper.getReportEngine().getLogger().log(logLevel, message);
      }
      catch (BirtException e) {
       
      }
  }

  
}
