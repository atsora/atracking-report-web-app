// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.atsora.tracking.reporting.util.Utils;

@Controller
public class DownloadImageController {
  protected static Logger LOGGER = LogManager.getLogger(DownloadImageController.class);

  @RequestMapping("/downloadimage")
  public void downloadImage(@RequestParam(value = "source", required = true) String source, HttpServletRequest req, HttpServletResponse resp)
      throws Exception {

    BufferedOutputStream out = null;
    BufferedInputStream in = null;
    try {
      File file = new File(source);
      if (!file.exists()) {
        LOGGER.warn("File " + source + " not found!");
        file = new File(req.getSession().getServletContext().getRealPath("/") + File.separator + "images/blank.png");
      }
      if (!file.exists()) {
        LOGGER.warn("Blank image do not exists at following path : " + file.getAbsolutePath());
        resp.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
      }
      String s = file.getName();
      int index = s.lastIndexOf(".");
      String extension = s.substring(index + 1);
      resp.setContentType(Utils.getContentType(extension));
      resp.setContentLength((int) file.length());
      byte[] buffer = new byte[1024];
      int n;
      out = new BufferedOutputStream(resp.getOutputStream());
      in = new BufferedInputStream(new FileInputStream(file));
      while ((n = in.read(buffer)) != -1) {
        try {
          out.write(buffer, 0, n);
        }
        catch (Exception e) {
        }
      }
      try {
        out.flush();
      }
      catch (Exception e) {
      }
    }
    catch (Exception e) {
      LOGGER.error(Utils.getStackTraceAsString(e));
      try {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
      catch (IOException e1) {
        LOGGER.error(Utils.getStackTraceAsString(e1));
      }
    }
    finally {
      try {
        if (in != null) {
          in.close();
        }
      }
      catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
      try {
        if (out != null) {
          out.close();
        }
      }
      catch (IOException e) {
        LOGGER.error(Utils.getStackTraceAsString(e));
      }
    }
  }

}
