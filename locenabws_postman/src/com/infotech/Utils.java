package com.infotech;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.sql.Timestamp;

import java.text.Normalizer;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;

public class Utils {
    private static final String paramFile   = "locenabws_postman";
    private static Properties paramValues   = null;
    
    public Utils() {
        
    }
    
    public static String getParameter(String key) {
        if( paramValues == null ) paramValues = loadParams(paramFile);
        if( paramValues == null ) return null;
        
        return( paramValues.getProperty(key) );
    } 
    
    public static String convToEnglish(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        
        for( int i = 0; i < txt.length(); i++ ) {
         char ch = txt.charAt(i);
         switch( ch ) {
         case 0x11E : ch = 'G'; break;
         case 0x11F : ch = 'g'; break;
         case 0x15E : ch = 'S'; break;
         case 0x15F : ch = 's'; break;
         case 0x130 : ch = 'I'; break;
         case 0x131 : ch = 'i'; break;
         }
         s += ch;
        }
        return( s );
    }
    
    public static Properties loadParams(String file) {
        Properties prop = null;
        
        try {
          prop = new Properties();
          ResourceBundle bundle = ResourceBundle.getBundle(file);
          Enumeration enu = bundle.getKeys();
          String key  = null;
          while( enu.hasMoreElements() ) {
            key = (String)enu.nextElement();
            prop.put(key, bundle.getObject(key));
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
          return null;
        }
        return( prop );
    } 
    
    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
      StringBuilder result = new StringBuilder();
      boolean first = true;
    
      for (NameValuePair pair : params)
      {
          if (first)
              first = false;
          else
              result.append("&");
    
          result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
      }
    
      return result.toString();
    }
    
    public static String encodeUTF8(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        for( int i = 0; i < txt.length(); i++ ) {
         int ch = txt.charAt(i);
         switch( ch ) {
         case 0xDE  : s += "&#350;"; break;
         case 0x15E : s += "&#350;"; break;
         case 0xFE  : s += "&#351;"; break;
         case 0x15F : s += "&#351;"; break;
         case 0xD0  : s += "&#286;"; break;
         case 0x11E : s += "&#286;"; break;
         case 0xF0  : s += "&#287;"; break;
         case 0x11F : s += "&#287;"; break;
         case 0xDD  : s += "&#304;"; break;
         case 0x130 : s += "&#304;"; break;
         case 0xFD  : s += "&#305;"; break;
         case 0x131 : s += "&#305;"; break;
         default :
           s += (char)ch;
           break;
         } 
        }
        return s;
    } 
    
    public static void showText(String text) {
        System.out.println(text);
        Utils.logInfo(text);
        return;
    } 
    
    public static void showError(String text) {
        System.err.println(text);
        Utils.logInfo(text);
        return;
    }
    
    public static String convToAscii(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        for( int i = 0; i < txt.length(); i++ ) {
          char ch = txt.charAt(i);
          switch( ch ) {
          case 0x07E : s += "~~"; break;
          case 0x020 : s += "~b"; break;
          case 0x0C7 : s += "~C"; break;
          case 0x0E7 : s += "~c"; break;
          case 0x11E : s += "~G"; break;
          case 0x11F : s += "~g"; break;
          case 0x0D0 : s += "~G"; break;
          case 0x0F0 : s += "~g"; break;
          case 0x15E : s += "~S"; break;
          case 0x15F : s += "~s"; break;
          case 0x0DE : s += "~S"; break;
          case 0x0FE : s += "~s"; break;
          case 0x0EE : s += "~s"; break;
          case 0x130 : s += "~I"; break;
          case 0x131 : s += "~i"; break;
          case 0x0DD : s += "~I"; break;
          case 0x0FD : s += "~i"; break;
          case 0x0DC : s += "~U"; break;
          case 0x0FC : s += "~u"; break;
          case 0x0D6 : s += "~O"; break;
          case 0x0F6 : s += "~o"; break;
          default :
            s += ch;
            break;
          } 
        }
        return( s );
    } 
    
    public static String convToTurkish(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        
        for( int i = 0; i < txt.length(); i++ ) {
          char ch = txt.charAt(i);
          if( ch == '~' ) {
            i++;
            ch = txt.charAt(i);
            switch( ch ) {
            case '~' : ch = '~'; break;
            case 'b' : ch = ' '; break;
            case 'C' : ch = (char)0x0C7; break;
            case 'c' : ch = (char)0x0E7; break;
            case 'G' : ch = (char)0x11E; break;
            case 'g' : ch = (char)0x11F; break;
            case 'S' : ch = (char)0x15E; break;
            case 's' : ch = (char)0x15F; break;
            case 'I' : ch = (char)0x130; break;
            case 'i' : ch = (char)0x131; break;
            case 'U' : ch = (char)0x0DC; break;
            case 'u' : ch = (char)0x0FC; break;
            case 'O' : ch = (char)0x0D6; break;
            case 'o' : ch = (char)0x0F6; break;
            } 
          } 
          s += ch;
        } 
        return( s );
    } 
    
    public static String convUtf8ToTurkish(String txt) {
         if( txt == null ) return null;
        
         String s = "";
        
         for( int i = 0; i < txt.length(); i++ ) {
           int ch = txt.charAt(i);
           switch( ch ) {
           case 0xDE : ch = 0x15E; break;
           case 0xFE : ch = 0x15F; break;
           case 0xEE : ch = 0x15F; break;
           case 0xDD : ch = 0x130; break;
           case 0xFD : ch = 0x131; break;
           case 0xD0 : ch = 0x11E; break;
           case 0xF0 : ch = 0x11F; break;
           case 0xC3 :
             i++;
             ch = txt.charAt(i);
             switch( ch ) {
             case 0x0087 : ch = 0x0C7; break;
             case 0x00A7 : ch = 0x0E7; break;
             case 0x009C : ch = 0x0DC; break;
             case 0x00BC : ch = 0x0FC; break;
             case 0x0096 : ch = 0x0D6; break;
             case 0x00B6 : ch = 0x0F6; break;
             case 0x2021 : ch = 0x0C7; break;
             case 0x2013 : ch = 0x0D6; break;
             case 0x0153 : ch = 0x0DC; break;
             } 
             break;
           case 0xC4 :
             i++;
             ch = txt.charAt(i);
             switch( ch ) {
             case 0x009E : ch = 0x11E; break;
             case 0x009F : ch = 0x11F; break;
             case 0x00B0 : ch = 0x130; break;
             case 0x00B1 : ch = 0x131; break;
             case 0x0178 : ch = 0x11F; break;
             } 
             break;
           case 0xC5 :
             i++;
             ch = txt.charAt(i);
             switch( ch ) {
             case 0x009E : ch = 0x15E; break;
             case 0x009F : ch = 0x15F; break;
             case 0x017E : ch = 0x15E; break;
             case 0x0178 : ch = 0x15F; break;
             }
             break;
           default :
             break;
           }
           s += (char)ch;
         } 
         return( s );
    } 
    
    public static String encodeEscape(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        for( int i = 0; i < txt.length(); i++ ) {
          int ch = txt.charAt(i);
          switch( ch ) {
          case 0xDE  : s += "%u015E"; break;
          case 0x15E : s += "%u015E"; break;
          case 0xFE  : s += "%u015F"; break;
          case 0x15F : s += "%u015F"; break;
          case 0xD0  : s += "%u011E"; break;
          case 0x11E : s += "%u011E"; break;
          case 0xF0  : s += "%u011F"; break;
          case 0x11F : s += "%u011F"; break;
          case 0xDD  : s += "%u0130"; break;
          case 0x130 : s += "%u0130"; break;
          case 0xFD  : s += "%u0131"; break;
          case 0x131 : s += "%u0131"; break;
          default :
            s += (char)ch;
            break;
          } 
        }
        return s;
    } 
    
    public static String decodeEscape(String txt) {
        if( txt == null ) return null;
        
        String s = "";
        for( int i = 0; i < txt.length(); i++ ) {
          int ch = txt.charAt(i);
          if( ch != '%' ) {
            s += (char)ch;
            continue;
          }
        
          if( i + 3 > txt.length() ) {
            s += (char)ch;
            continue;
          }
        
          String chk = txt.substring(i, i + 3);
          if( chk.equals("%C7") ) { ch = 0x0C7; i += 2; }
          else
          if( chk.equals("%E7") ) { ch = 0x0E7; i += 2; }
          else
          if( chk.equals("%DC") ) { ch = 0x0DC; i += 2; }
          else
          if( chk.equals("%FC") ) { ch = 0x0FC; i += 2; }
          else
          if( chk.equals("%D6") ) { ch = 0x0D6; i += 2; }
          else
          if( chk.equals("%F6") ) { ch = 0x0F6; i += 2; }
          else {
            if( i + 4 > txt.length() ) {
              s += (char)ch;
              continue;
            }
        
            chk = txt.substring(i, i + 4);
            if( chk.equals("%xDD") ) { ch = 0x130; i += 3; }
            else {
              if( i + 6 > txt.length() ) {
                s += (char)ch;
                continue;
              }
        
              chk = txt.substring(i, i + 6);
              if( chk.equals("%u015E") ) { ch = 0x15E; i += 5; }
              else
              if( chk.equals("%u015F") ) { ch = 0x15F; i += 5; }
              else
              if( chk.equals("%u011E") ) { ch = 0x11E; i += 5; }
              else
              if( chk.equals("%u011F") ) { ch = 0x11F; i += 5; }
              else
              if( chk.equals("%u0130") ) { ch = 0x130; i += 5; }
              else
              if( chk.equals("%u0131") ) { ch = 0x131; i += 5; }
              else
                ;
        
            }
          } 
        
          s += (char)ch;
        }
        return s;
    }
    
    public static double calcDistance(double x1, double y1, double x2, double y2) {
        double dx, dy, dist;
        
        dx = (x1 - x2);
        dy = (y1 - y2);
        dist = Math.sqrt(dx * dx + dy * dy);
        return( dist );
    }
    
    public static boolean isCoorsEqual(double x1, double y1, double x2, double y2) {
        double dx, dy, dist;
        
        dx = (x1 - x2);
        dy = (y1 - y2);
        dist = Math.sqrt(dx * dx + dy * dy);
        if( dist < 0.00001 ) return true;
        
        return false;
    } 
    
    public static String formatNumber(int  num, int  len) {
        String s = "";
        for( int i = 0; i < len; i++ ) s += "0";
        s += num;
        s = s.substring(s.length() - len);
        return( s );
    } 
    
    public static String formatNumber(long  num, int  len) {
        String s = "";
        for( int i = 0; i < len; i++ ) s += "0";
        s += num;
        s = s.substring(s.length() - len);
        return( s );
    } 
    
    public static String formatNumber(double num, int  totlen, int len) {
        String n = "";
        if( num >= 1 )
          n += num;
        else {
          num += 1;
          n += num;
          n = "0" + n.substring(1);
        }
        String s = "";
        for( int i = 0; i < totlen - len; i++ ) s += "0";
        s += n;
        for( int i = 0; i < len; i++ ) s += "0";
        int  pos = s.indexOf('.');
        s = s.substring(pos - (totlen - 1 - len), pos + 1 + len);
        return( s );
    } 
    
    public static String formatNumeric(double num, int len) {
        String s = "";
        String f = "0.";
        for( int i = 0; i < len; i++ ) {
          s += "0";
          f += "0";
        }
        f += "5";
        num += Double.parseDouble(f);
        s = num + s;
        int  pos = s.indexOf('.');
        s = s.substring(0, pos + 1 + len);
        return( s );
    } 
    
    public static boolean isNumber(String txt) {
        if( txt == null || txt.length() <= 0 ) return false;
        
        for( int i = 0; i < txt.length(); i++ )
          if( !Character.isDigit(txt.charAt(i)) ) return false;
        
        return true;
    }
    
    public static String padLeftString(String txt, int len, char ch) {
        if( txt == null ) txt = ""; 
        String s = "";
        for( int i = 0; i < len; i++ ) s += String.valueOf(ch);
        s += txt;
        s = s.substring(s.length() - len);
        return( s );
    } 
    
    public static String padRightString(String txt, int len, char ch) {
        if( txt == null ) txt = ""; 
        String s = txt;
        for( int i = 0; i < len - txt.length(); i++ ) s += String.valueOf(ch);
        return( s );
    }
    
    public static String getCurrentDate() {
        Calendar c = new GregorianCalendar();
        String dt = formatNumber(c.get(Calendar.YEAR), 4) + "-" +
                    formatNumber(c.get(Calendar.MONTH) + 1, 2) + "-" +
                    formatNumber(c.get(Calendar.DAY_OF_MONTH), 2);
        return dt;
    } 
    public static String getCurrentDateNextYear() {
        Calendar c = new GregorianCalendar();
        String dt = formatNumber(c.get(Calendar.YEAR) + 1, 4) + "-" +
                    formatNumber(c.get(Calendar.MONTH) + 1, 2) + "-" +
                    formatNumber(c.get(Calendar.DAY_OF_MONTH), 2);
        return dt;
    } 
    
    public static String getCurrentTimeStamp() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return( ts.toString() );
    }
    
    public static Timestamp toTimestamp(String datetime) {
        Calendar c = null;
        
        try {
          int year   = Integer.parseInt(datetime.substring(0, 4));
          if( year == 0 ) year = 2000;
          int month  = Integer.parseInt(datetime.substring(5, 7));
          if( month == 0 ) month = 1;
          int day    = Integer.parseInt(datetime.substring(8, 10));
          if( day == 0 ) day = 1;
          int hour   = 0;
          int minute = 0;
          if( datetime.length() >= 16 ) {
            hour   = Integer.parseInt(datetime.substring(11, 13));
            minute = Integer.parseInt(datetime.substring(14, 16));
          }
          int second = 0;
          if( datetime.length() >= 19 ) {
            second = Integer.parseInt(datetime.substring(17, 19));
          }
          c = new GregorianCalendar(year, (month - 1), day, hour, minute, second);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        return( new Timestamp( c.getTime().getTime() ) );
    }
    
    public synchronized static void logInfo(String txt) {
        String fullPath = Utils.getParameter("logfileprefix") + "_" + Utils.getCurrentDate().replaceAll("-", "") + ".log";
        try {
          BufferedWriter out = new BufferedWriter( new FileWriter(fullPath, true) );
          out.write("\"" + getCurrentTimeStamp() + "\",\"" + txt + "\"");
          out.newLine();
          out.close();
        }
        catch( Exception ex ) {
          ex.printStackTrace();
        }
        return;
    } 
    
    public static String toUpperCase(String txt) {
        String s = "";
        for( int i = 0; i < txt.length(); i++ ) {
          int ch = txt.charAt(i);
          switch( ch ) {
          case 0x0C7 : break;
          case 0x0E7 : ch = 0x0C7; break; 
        
          case 0x0DE : ch = 0x15E; break; 
          case 0x0FE : ch = 0x15E; break; 
          case 0x0EE : ch = 0x15E; break; 
          case 0x15E : break;             
          case 0x15F : ch = 0x15E; break; 
        
          case 0x0D0 : ch = 0x11E; break; 
          case 0x0F0 : ch = 0x11E; break; 
          case 0x11E : break;             
          case 0x11F : ch = 0x11E; break; 
        
          case 0x0DD : ch = 0x130; break; 
          case 0x0FD : ch = 0x049; break; 
          case 0x130 : break;             
          case 0x131 : ch = 0x049; break; 
          case 0x069 : ch = 0x130; break; 
        
          case 0x0D6 : break;
          case 0x0F6 : ch = 0x0D6; break; 
        
          case 0x0DC : break;
          case 0x0FC : ch = 0x0DC; break; 
        
          default :
            if( Character.isLowerCase(ch) ) ch = Character.toUpperCase(ch);
            break;
          } 
          s += (char)ch;
        } 
        return s;
    } 
    
    public static String[] splitString(String splitStr, String delim) {
        StringTokenizer toker;
        String[] result;
        int count, i;
        
        toker = new StringTokenizer(splitStr, delim);
        count = toker.countTokens();
        result = new String[count];
        for( i = 0 ; i < count ; ++i ) {
          try {
            result[i] = toker.nextToken();
          }
          catch (NoSuchElementException ex) {
            result = null;
            break;
          }
        
        } 
        
        return result;
    } 
    
    public static Document strToDocument(String xmlData) {
    
        try {
           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();
           InputSource is = new InputSource( new StringReader( xmlData ) );
           Document d = builder.parse( is );
           return d;
        
         }
         catch( Exception ex ) {
           ex.printStackTrace();
         }
         return null;
    }
    
    public static String getCurrentDateTime() {
        Calendar c = new GregorianCalendar();
        String dt;
        
        dt = formatNumber(c.get(Calendar.YEAR), 4) + "-" +
             formatNumber(c.get(Calendar.MONTH) + 1, 2) + "-" +
             formatNumber(c.get(Calendar.DAY_OF_MONTH), 2) + " " +
             formatNumber(c.get(Calendar.HOUR_OF_DAY), 2) + ":" +
             formatNumber(c.get(Calendar.MINUTE), 2) + ":" +
             formatNumber(c.get(Calendar.SECOND), 2);
        return( dt );
    }
    
    public static String formatDateTime(Calendar c) {
        String dt;
        
        dt = formatNumber(c.get(Calendar.YEAR), 4) + "-" +
             formatNumber(c.get(Calendar.MONTH) + 1, 2) + "-" +
             formatNumber(c.get(Calendar.DAY_OF_MONTH), 2) + " " +
             formatNumber(c.get(Calendar.HOUR_OF_DAY), 2) + ":" +
             formatNumber(c.get(Calendar.MINUTE), 2) + ":" +
             formatNumber(c.get(Calendar.SECOND), 2);
        return( dt );
    }
    
    public static String convEnglish(String str) {
      str = Normalizer.normalize(str, Normalizer.Form.NFD);
      str = str.replaceAll("[^\\p{ASCII}]", "");
      str = str.replaceAll("\\p{M}", "");
      return str;
    }
    
    // getDebugMode("debug_mode")
    public static String getDebugMode(String str) {
        String retValue = "false";
        retValue = getParameter(str);
        if( retValue != null && retValue.length() > 0 ) {
            return retValue;
        } else {
            return retValue;
        }
    }
    
    // getDebugMode("debug_mode")
    public static int getCntDay(String str) {
        int retValue = 0;
        retValue = Integer.parseInt(getParameter(str));
        if( retValue > 0 ) {
            return retValue;
        } else {
            return retValue;
        }
    }
    
    public static String getPhoneNumber(String sPhone) {
        // return value is telefon number as (aaa-nnnnnnn)
        String retPhone = "";
        String areaCode = "";
        String phone = "";

        retPhone = sPhone.replaceAll("[^0-9]", "");
        if (retPhone.startsWith("0")) {
            retPhone = retPhone.substring(1, retPhone.length());
        } else if (retPhone.startsWith("90")) {
            retPhone = retPhone.substring(2, retPhone.length());
        }
        
        int phoneLength = retPhone.length();
        
        try {
            if ( phoneLength == 10 ) {
                areaCode = retPhone.substring(0, 3);
                phone = retPhone.substring(3, 10);               
            } else if ( phoneLength > 10 ) {
                retPhone = retPhone.substring(0, 10);
                areaCode = retPhone.substring(0, 3);
                phone = retPhone.substring(3, 10);              
            } else if ( phoneLength < 10 ) {
                if (phoneLength == 7) {
                   areaCode = "";
                   phone = retPhone.substring(0, 7);
                } else if (phoneLength > 7) {
                    areaCode = retPhone.substring(0, 3);
                    phone = retPhone.substring(3, retPhone.length());
                } else if (phoneLength < 7) {
                   areaCode = "";
                   phone = (phoneLength == 0) ? "" : retPhone.substring(3, phoneLength);                          
                }         
            }
        }
        catch (Exception ex) {
          Utils.showError("Beklenmedik Hata ! @getPhoneNumber()");  
          Utils.showError("Beklenmeyen formatta bir telefon numarasi bulundu :" + sPhone);
          ex.printStackTrace();
          return retPhone;
        }
        retPhone = areaCode + phone;
        return retPhone;
    }  // getPhoneNumber()
    
    public static boolean IsValidURL(String urlConn) {
       
        try {
          
          boolean f_debug_mode = Boolean.valueOf(getDebugMode("debug_mode"));
          if (f_debug_mode) {
              Utils.showText("");
              Utils.showText(">> Check URL >> (Url :" + urlConn + ")");
          }
            

            /*
              // Create a trust manager that does not validate certificate chains
              TrustManager[] trustAllCerts = new TrustManager[] { 
                new X509TrustManager() {     
                  public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; } 
                  public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                  public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                } 
              }; 
              
              // Install the all-trusting trust manager
              try {
                SSLContext sc = SSLContext.getInstance("SSL"); 
                sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
              }
              catch (GeneralSecurityException e) {
                ;
              }             
            
            */   

          URL url = new URL(urlConn);
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          // connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:66.0) Gecko/20100101 Firefox/66.0");
          connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
          // connection.setRequestProperty("connection", "close");
          connection.setRequestMethod("GET");

          int resCode = connection.getResponseCode();
          if (resCode == HttpURLConnection.HTTP_OK) {
            if (f_debug_mode) { Utils.showText(">> HTTP connection is valid."); Utils.showText(""); }
            return true;
          } else if (resCode == HttpURLConnection.HTTP_MOVED_PERM) {
              if ( url.getProtocol().equals("http") ) {
                  String nProtocol =  "https";
                  String nHost = url.getHost();
                  String nUrl = nProtocol + nHost;
              }
              Utils.showError("***************************************");
              Utils.showError("*** HTTP connection is Moved Permanently ! check URL and try again later");
              Utils.showError("==> (Url :" + urlConn + ")");
              Utils.showError("*** Response Code :" + connection.getResponseCode());
              Utils.showError("***************************************");
              Thread.sleep(5000);
             

          } else if (resCode != HttpURLConnection.HTTP_OK) {
            // 
            Utils.showError("***************************************");
            Utils.showError("*** HTTP connection is not valid ! check URL and try again later");
            Utils.showError("==> (Url :" + urlConn + ")");
            Utils.showError("*** Response Code :" + connection.getResponseCode());
            Utils.showError("***************************************");
            Thread.sleep(5000);
          }
        } catch (Exception ex) {
          try {
            Utils.showText("HTTP connection problem !");
            Utils.showText("Please check URL and try again (Url :" + urlConn + ").");              
            Utils.showError("***************************************");
            Utils.showText("");  
            Thread.sleep(1000*5);
            // ex.printStackTrace();              
          } catch (Exception e) {
            ;
          }
        }
        return false;
    }   // IsValidURL()
    
    public static String getCurrentDay(int dy) {
        String day = null;
        
        int dayTmp = dy;
        if (dayTmp / 10 < 1) {
            day = "0" + Integer.toString(dayTmp);
        } else {
            day = Integer.toString(dayTmp);
        }               
        return day;
    } // getCurrentDay()

    public static String getCurrentMonth() {
        String month = null;
        
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        currentDate.add(Calendar.DATE, 0);  // 0 yerine i kac gun olursa
        
        int monthDate = currentDate.get(Calendar.MONTH) + 1;
        if (monthDate / 10 < 1) {
            month = 0 + String.valueOf(monthDate);
        } else if (monthDate / 10 >= 1) {
            month = String.valueOf(monthDate);
        }
        return month;
    }   // getCurrentMonth()
            
    public static String getCurrentYear() {
        String year = null;

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        currentDate.add(Calendar.DATE, 0);  // 0 yerine i kac gun olursa
        
        year = String.valueOf(currentDate.get(Calendar.YEAR));
        return year;
    }   // getCurrentYear()
    
    public static boolean coordinateInTurkeyRange(double latitude, double longitude) {
        /* gelen koordinatlar Turkiye sinir icinde mi kontrol ediliyor.
        * Lat (Enlem) 36-42 arasinda (Y)
        * Lon (Boylam 26-45 arasinda (X)
        * */
        boolean retValue = false;
        double maxLat = 42.02683;
        double minLat = 35.9000; // 35.9025 
        double maxLong = 44.5742;
        double minLong = 25.60332;

        if (latitude >= minLat && latitude <= maxLat) {
            if (longitude >= minLong && longitude <= maxLong) {
              retValue = true;
            }
        }
        return retValue;
    } // coordinateInTurkeyRange()
    
    public static String truncateField(String fld,int maxLength) {
        /*
         * Burada alan maksimum uzunluguna gore kesilmesini saglar
         * cunku fazlasi insert ederken hata atiyor.
         */
        String retVal = "";
        
        if (fld.length() > maxLength-1) {
            retVal = fld.substring(0,maxLength-3) + "...";
        } else {
            retVal = fld.trim();
        }
        return retVal;
    }  // truncateField()

    public static String getCurrentMonthOfDay(int dd) {
        String month = null;
        
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        currentDate.add(Calendar.DATE, dd);  // 0 yerine i kac gun olursa
        
        int monthDate = currentDate.get(Calendar.MONTH) + 1;
        if (monthDate / 10 < 1) {
            month = 0 + String.valueOf(monthDate);
        } else if (monthDate / 10 >= 1) {
            month = String.valueOf(monthDate);
        }
        return month;
    }   // getCurrentMonthOfDay()

    public static String getCurrentYearOfDay(int dd) {
        String year = null;

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        currentDate.add(Calendar.DATE, dd);  // 0 yerine i kac gun olursa
        
        year = String.valueOf(currentDate.get(Calendar.YEAR));
        return year;
    }   // getCurrentYearOfDay()
    
    //-----------------------------------------------------------------------------
    
    public static void doSleepMode(int val) {
        try { 
            // showText("<" + getCurrentTimeStamp() + ">");
            Thread.sleep(val); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }         
    }  // doSleepMode()
    
    public static String findAndReplaceTicketNodeData(String data, String nodeName, String ticketValue) {
        
        /* this function find ticket tag in xml data and replace with new ticket 
         * retrun "0" when not found or not access succesfuly */
        String first_text = "";
        String last_text = "";
        
        int posBeg = data.indexOf("<" + nodeName + ">");
        if (posBeg < 0) return "0";

        posBeg += (1 + nodeName.length() + 1);
        int posEnd = data.indexOf("</" + nodeName + ">", posBeg);
        if (posEnd < 0) return "0";

        if (ticketValue.length() < 1) return "0";
        first_text = data.substring(0, posBeg);
        last_text = data.substring(posEnd, data.length());
        return first_text.concat(ticketValue).concat(last_text);
    } // getNodeData()    
}
