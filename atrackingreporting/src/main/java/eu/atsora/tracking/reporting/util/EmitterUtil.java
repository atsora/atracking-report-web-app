// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.birt.report.engine.api.EmitterInfo;

public class EmitterUtil {

  public static List<String> available_format = new ArrayList<String>();
  public static Map<String, String> display;
  public static List<EmitterInfo> emitterInfos;
  
  static {
    display = new TreeMap<String, String>();
    display.put("doc", "Word");
    display.put("docx", "Word OpenXML");
    display.put("html", "HTML");
    display.put("odp", "OpenDocument Presentation");
    display.put("ods", "OpenDocument Spreadsheet");
    display.put("odt", "OpenDocument Text");
    display.put("pdf", "PDF");
    display.put("postscript", "Postscript");
    display.put("ppt", "Powerpoint");
    display.put("pptx", "Powerpoint OpenXML");
    display.put("xls", "Excel");
    display.put("xlsx", "Excel OpenXML");
    display.put("xls_atsora", "Excel Atsora");
    display.put("xls_spudsoft", "Excel Spudsoft");
    emitterInfos = new ArrayList<EmitterInfo>();
  }
  
  
  public static String getDisplay(String format){
    if(display.containsKey(format)){
      return display.get(format);
    }
    else {
      return format;
    }
  }
  
  public static Map<String, String> getAvailableFormat(){
    Map<String, String> map = new TreeMap<String, String>();
    for(String f :  available_format){
      map.put(f, getDisplay(f));      
    }
    return Utils.sortMapByValue(map);
  }
  
  public static EmitterInfo getEmitterInfo(String format){
    for(EmitterInfo ei : emitterInfos){
      if(ei.getFormat().equalsIgnoreCase(format)){
        return ei;
      }
    }
    return null;
  }
  
  
  
}
