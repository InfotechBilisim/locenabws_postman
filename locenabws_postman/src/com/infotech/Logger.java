package com.infotech;

import java.io.*;

public class Logger extends Object {
  public static final int  LOG_SERVER_NOT_FOUND  = 11;
  public static final int  LOG_SERVER_LIST_EMPTY = 12;
  public static final int  LOG_EXCEPTION         = 13;
  public static final int  LOG_ABRUPT            = 14;
  public static final int  LOG_DEBUG             = 15;

  public Logger() {
  }

  public synchronized static void logInfo(int  logCode, String exp, String extra) {
    String fullPath = Utils.getParameter("logfileprefix") + "_" + Utils.getCurrentDate() + ".log";
    try {
      FileWriter out = new FileWriter(fullPath, true);
      String txt = exp;
      //String txt = "" + logCode + " " + exp + " " + extra;
      out.write(txt + "\r\n");
      out.close();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }

    return;
  } // logInfo()

  public synchronized static void showText(String txt) {
    //System.out.println(txt);
    logInfo(LOG_DEBUG, txt, "");
    return;
  } // showText()

}


