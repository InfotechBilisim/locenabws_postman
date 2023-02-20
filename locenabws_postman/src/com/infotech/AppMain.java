package com.infotech;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class AppMain extends TimerTask {
    
    private final static String CORPORATION         = "INFOTECH Bilisim ve Iletisim Teknolojileri San. ve Tic. A.S.";
    private final static String APP_NAME            = "locenabws_postman";
    private final static String APP_VERSION         = "v1.0 20230220";
    private final static int REGISTER_SUCCESSFUL    = 0;
    private final static String newLine             = "\r\n";
    
    private static int REQUEST_SEND_TIME            = 0;
    private static int REQUEST_METHOD               = 0;
    private static int process_rec_count            = 0;    
    
    private static String LOCENAB_WS_ENDPOINT       = "";
    private static String LOCATIONBOXKEY            = "";
    private static String register_serviceId        = "";
    private static String register_ticket           = "";
    private static int register_resultcode          = -1;
    private static String xmlInput                  = "";
    
    private static Map<String, String> register_datas = new HashMap<String, String>();
       
    public void run() {

        String serviceInfo_address      = "";
        String serviceInfo_lattitude    = "";
        String serviceInfo_longitude    = "";
        String serviceInfo_receipt      = "";
        String serviceInfo_time         = "";
    
        Connection cnn = DbConn.getPooledConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sqlStr = "";
        String sData = "";

        try {
            Utils.showText(APP_NAME + " Started.");
            Utils.showText("Start @" + Utils.getCurrentDateTime() + "");
            String json_data = "";
            String sCode_data = "";
            String xml_data = "";
            int process_rowno = 0;

            try {
                sqlStr = "SELECT REQUEST_ROWNO, SERVICE_CODE, REQUEST_JSON, REQUEST_XML FROM ARCELIK_ARON_REQUESTS WHERE PROCESS_STATUS = 0 ORDER BY REQUEST_ROWNO";
                pstmt = cnn.prepareStatement(sqlStr);
                pstmt.clearParameters();
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    Utils.showText("");
                    Utils.showText("Operation:register()");
                    if (registerAndGetTicket()){
                        Iterator<String> keysIterator = register_datas.keySet().iterator();
                        while (keysIterator.hasNext()) {
                            String currentKey = keysIterator.next();
                            String currentValue = register_datas.get(currentKey);
                            if (currentKey.equals("resultCode") && currentValue.length() > 0) register_resultcode = Integer.parseInt(currentValue);
                            if (currentKey.equals("ticket") && currentValue.length() > 0) register_ticket = currentValue.toString();
                            if (currentKey.equals("serviceId") && currentValue.length() > 0) register_serviceId = currentValue.toString();
                        } // while of keysIterator 
                        
                        if (register_resultcode == REGISTER_SUCCESSFUL)  {
                            Utils.showText("register successful with ticket :" + register_ticket + "");
                            sCode_data = (rset.getString("SERVICE_CODE") != null ? rset.getString("SERVICE_CODE") : "");
                            json_data = (rset.getString("REQUEST_JSON") != null ? rset.getString("REQUEST_JSON") : "");
                            xml_data = (rset.getString("REQUEST_XML") != null ? rset.getString("REQUEST_XML") : "");
                            process_rowno = rset.getInt("REQUEST_ROWNO");
                            
                            String xmlSubInfo = "";
                            int infoCnt = 0;
                            
                            Utils.showText("");
                            Utils.showText("Operation:getMobileServiceOperationInfo()");
                            Utils.showText("REQUEST_ROWNO:" + process_rowno + " is processing.");
                            if (REQUEST_METHOD == 1) {      // JSON Access ...
                                JsonReader jr = Json.createReader(new StringReader(json_data));
                                JsonArray trips = jr.readArray();
                                for (JsonObject trip : trips.getValuesAs(JsonObject.class)) {
                                    infoCnt++;
                                    serviceInfo_lattitude = trip.getJsonNumber("lattitude").bigDecimalValue().toString();
                                    serviceInfo_address = "addrr" + infoCnt;
                                    serviceInfo_longitude = trip.getJsonNumber("longitude").bigDecimalValue().toString();
                                    serviceInfo_receipt = trip.getJsonString("receipt").toString().replaceAll("\"", "");
                                    serviceInfo_time = trip.getJsonString("time").toString().replaceAll("\"", "");

                                    xmlSubInfo = xmlSubInfo + "                 <serviceInfoProxy>\n";
                                    xmlSubInfo = xmlSubInfo + "                     <address>" + serviceInfo_address + "</address>\n";
                                    xmlSubInfo = xmlSubInfo + "                     <lattitude>" + serviceInfo_lattitude + "</lattitude>\n";
                                    xmlSubInfo = xmlSubInfo + "                     <longitude>" + serviceInfo_longitude + "</longitude>\n";
                                    xmlSubInfo = xmlSubInfo + "                     <receipt>" + serviceInfo_receipt + "</receipt>\n";
                                    xmlSubInfo = xmlSubInfo + "                     <time>" + serviceInfo_time + "</time>\n";
                                    xmlSubInfo = xmlSubInfo + "                 </serviceInfoProxy>\n";
                                    // xml olusturma
                                    xmlInput = "";
                                    xmlInput = xmlInput + "        <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:view=\"http://LocationEnablerWebServices/view/\">\n";
                                    xmlInput = xmlInput + "            <soapenv:Header/>\n";
                                    xmlInput = xmlInput + "            <soapenv:Body>\n";
                                    xmlInput = xmlInput + "               <view:getMobileServiceOperationInfo>\n";
                                    xmlInput = xmlInput + "                 <sessionTicketProxy>\n";
                                    xmlInput = xmlInput + "                     <resultCode>" + register_resultcode + "</resultCode>\n";
                                    xmlInput = xmlInput + "                     <serviceId>" + register_serviceId + "</serviceId>\n";
                                    xmlInput = xmlInput + "                     <ticket>" + register_ticket + "</ticket>\n";
                                    xmlInput = xmlInput + "                 </sessionTicketProxy>\n";
                                    xmlInput = xmlInput + "                 <mobileAlias>1</mobileAlias>\n";
                                    xmlInput = xmlInput + xmlSubInfo;
                                    xmlInput = xmlInput + "                 <locationboxKey>" + LOCATIONBOXKEY + "</locationboxKey>\n";
                                    xmlInput = xmlInput + "                 <serviceCode>" + sCode_data + "</serviceCode>\n";
                                    xmlInput = xmlInput + "                 </view:getMobileServiceOperationInfo>\n";
                                    xmlInput = xmlInput + "            </soapenv:Body>\n";
                                    xmlInput = xmlInput + "        </soapenv:Envelope>\n";                                
                                }                            
                            } else if (REQUEST_METHOD == 2) {   // XML Access ...
                                String s_process_xml = Utils.convUtf8ToTurkish(Utils.findAndReplaceTicketNodeData(xml_data, "ticket", register_ticket)).replace("'", "");
                                xmlInput = s_process_xml;
                            }

                            sData = xmlInput;
                            if (startRequested(process_rowno) == 1)  {
                               Utils.showText("getMobileServiceOperationInfo XML:" + newLine + xmlInput);
                               send_Request(xmlInput, process_rowno);
                               process_rec_count += 1;
                               if (REQUEST_SEND_TIME > 0) {
                                    Utils.showText("Sleep time between requests (" + REQUEST_SEND_TIME + " ms.)");
                                    Thread.sleep(REQUEST_SEND_TIME);                        
                               }
                            } 
                        } else {
                            Utils.showText("register failed, please check your register request.");
                            Utils.showText("Response data is:");
                            Utils.showText(" resultCode:" + register_resultcode);
                            Utils.showText(" ticket:" + register_ticket);
                            Utils.showText(" serviceId:" + register_serviceId);
                            Utils.showText("***************************************************");
                        }
                    }
                }
                pstmt.close();
            } catch (Exception ex) {
                Utils.showError("run() : " + ex.fillInStackTrace());
                Utils.showError("Sql :" + sqlStr);
                Utils.showError("XMLData :" + sData);
                ex.printStackTrace();
            } finally {
                closeConnection(cnn, pstmt, null);
            }
            // summaray
            Utils.showText("Process total request count :" + process_rec_count);
            Utils.showText(APP_NAME + " is Finish.");
            Utils.showText("Finish @" + Utils.getCurrentDateTime() + "");
            Utils.showText("*********************************************");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String version = System.getProperty("java.version");
            Utils.showText("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Utils.showText("    " + CORPORATION);
            Utils.showText("    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Utils.showText("    * Computer Name              :" + getComputerName());
            Utils.showText("    * Application Name     :" + APP_NAME);
            Utils.showText("    * Application Version  :" + APP_VERSION);
            Utils.showText("    * Java Version               :" + version);
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            df.setTimeZone(TimeZone.getTimeZone("Etc/GMT-3"));
            Utils.showText("    * Current Time         :" + df.format(date));
            Utils.showText("    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            
            input = new FileInputStream("locenabws_postman.properties");
            prop.load(input);
            LOCENAB_WS_ENDPOINT = Utils.getParameter("locationEnabler_endpoint");
            LOCATIONBOXKEY = Utils.getParameter("locationboxKey");
            // requestSendTime
            REQUEST_SEND_TIME = Integer.parseInt(Utils.getParameter("requestSendTime"));
            REQUEST_METHOD = Integer.parseInt(Utils.getParameter("requestMethod"));

            Utils.showText("    * Java Version         :" + version);
            Utils.showText("    * WS EndPoint          :" + LOCENAB_WS_ENDPOINT);
            Utils.showText("    * REQUEST_METHOD       :" + (REQUEST_METHOD == 1 ? "(JSON)" : "(XML)") );
            Utils.showText("    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"); 
            startTask();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.showError("Exception main: " + ex.fillInStackTrace());

        } finally {
            try {
                if (input != null)
                    input.close();
                input = null;
            } catch (Exception e) {
                ;
            }
        }
    } // main()

    public static void startTask() {
        try {
            AppMain task = new AppMain();
            
            Timer timer = new Timer();
            Calendar calendar = Calendar.getInstance();
            int second = calendar.get(Calendar.SECOND);
            calendar.set(Calendar.SECOND, second + 1);
            Date time = calendar.getTime();
            timer.schedule(task,time);
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.showError("startTask: " + ex.fillInStackTrace());
        }
    } // startTask()
    
    private static void send_Request(String sendXml, int rowno) {
        String responseString = "";
        String outputString = "";
        
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(LOCENAB_WS_ENDPOINT).openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            String xmlInput = sendXml;
            byte[] buffer = new byte[xmlInput.length()];
            buffer = xmlInput.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();
            String SOAPAction = "getMobileServiceOperationInfo";
            urlConnection.setRequestProperty("Content-Length", String.valueOf(b.length));
            urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            urlConnection.setRequestProperty("SOAPAction", SOAPAction);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(b);
            out.close();

            Utils.showText("sending request to LocationEnabler_WS.");
            long startTime = System.nanoTime();
            // Read the response.
            InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
            BufferedReader in = new BufferedReader(isr);
            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
            long endTime = System.nanoTime();
            long durationInNano = (endTime - startTime);
            long durationInMillis = TimeUnit.NANOSECONDS.toMillis(durationInNano);
            Utils.showText("response received from LocationEnabler_WS in "+ durationInMillis + " ms.");             

            try {
                XmlParser xmlParser = new XmlParser(0);
                String res_pathid = xmlParser.getNodeDataInAll(outputString, "pathId");
                String res_rotadistance = xmlParser.getNodeDataInAll(outputString, "rotaDistance");
                String res_status = xmlParser.getNodeDataInAll(outputString, "status");
                String res_description = xmlParser.getNodeDataInAll(outputString, "description");
                String res_validdistance = xmlParser.getNodeDataInAll(outputString, "validDistance");
                int _pathid = Integer.parseInt(res_pathid);
                int _status = Integer.parseInt(res_status);
                double _rotadistance = Double.parseDouble(res_rotadistance);
                double _validdistance = Double.parseDouble(res_validdistance);
                
                if ( updateRequested(rowno, _pathid, _status, res_description, _validdistance, _rotadistance) == 1 )  {
                    Utils.showText("Response Result:");
                    Utils.showText("*********************************************");
                    Utils.showText(" Status          :" + _status);
                    Utils.showText(" Desciption      :" + res_description);
                    Utils.showText(" PathId          :" + _pathid);
                    Utils.showText(" Valid Distance  :" + _validdistance + " m.");
                    Utils.showText(" Route Distance  :" + _rotadistance + " m.");
                    Utils.showText("*********************************************");
                    Utils.showText("REQUEST_ROWNO: " + rowno + " is processing completed.");
                }
            } catch (Exception localException) {
                Utils.showError("EXP: " + localException.getMessage());
                localException.printStackTrace();
            }        
            urlConnection.disconnect();
        } catch (Exception ex) {
            Utils.showError("send_Request() EXP: " + ex.fillInStackTrace());
            ex.printStackTrace();
        }
    }  // send_Request
    
    private static boolean registerAndGetTicket() {
        /* rigister to web service and get valid ticket */
        boolean register_result = false;
        final String xmlRegisterData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:view=\"http://LocationEnablerWebServices/view/\"><soapenv:Header/><soapenv:Body><view:register><serviceId>A1BE40AD-1D81-42ca-A0A7-221329914CDC</serviceId><password>196974FE-16E2-4f20-A1A3-EF102E900294</password></view:register></soapenv:Body></soapenv:Envelope>";
        String responseString       = "";
        String outputString         = "";
        String ticket_value         = "";
        String service_id           = "";
        
        try {
            register_datas.clear();
            Utils.showText("Register XML:" + newLine + xmlRegisterData);
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(LOCENAB_WS_ENDPOINT).openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buffer = new byte[xmlRegisterData.length()];
            buffer = xmlRegisterData.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();
            String SOAPAction = "register";
            urlConnection.setRequestProperty("Content-Length", String.valueOf(b.length));
            urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            urlConnection.setRequestProperty("SOAPAction", SOAPAction);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream out = urlConnection.getOutputStream();
            out.write(b);
            out.close();

            // Read the response.
            InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
            BufferedReader in = new BufferedReader(isr);
            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }

            XmlParser xmlParser = new XmlParser(0);
            
            String resCode = xmlParser.getNodeDataInAll(outputString, "resultCode");
            register_resultcode = Integer.parseInt(resCode);
            if (register_resultcode == REGISTER_SUCCESSFUL) {
                ticket_value = xmlParser.getNodeDataInAll(outputString, "ticket");
                service_id = xmlParser.getNodeDataInAll(outputString, "serviceId");
                register_datas.put("resultCode", String.valueOf(register_resultcode));
                register_datas.put("ticket", ticket_value);
                register_datas.put("serviceId", service_id);
                register_result = true;
            }                
            urlConnection.disconnect();
        } catch (Exception ex) {
            Utils.showError("registerAndGetTicket() EXP: " + ex.getMessage());
            ex.printStackTrace();
        }
        return register_result;
    }  // registerAndGetTicket()

    private static int updateRequested(int rowno, int pathId, int status, String desc, double validDis, double rotaDis) {
        Connection cnn = DbConn.getPooledConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sqlStr = "";
        String sData = "";
        int res = 0;
        
        try {
            sqlStr = "UPDATE ARCELIK_ARON_REQUESTS SET RES_PATHID = ?, RES_ROTADISTANCE = ?, RES_STATUS = ? , RES_DESCRIPTION = ? ,";
            sqlStr += "RES_VALIDDISTANCE = ?, PROCESS_STATUS = 1 , PROCESS_FINISH_TIMESTAMP = SYSTIMESTAMP WHERE REQUEST_ROWNO=?";
            pstmt = cnn.prepareStatement(sqlStr);
            pstmt.clearParameters();
            int colNo = 1;
            pstmt.setInt(colNo++, pathId);
            pstmt.setDouble(colNo++, rotaDis);
            pstmt.setInt(colNo++, status);
            pstmt.setString(colNo++, desc);
            pstmt.setDouble(colNo++, validDis);
            pstmt.setInt(colNo++, rowno);
            res = pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception ex) {
            Utils.showError("Exception : updateRequested() " + ex.fillInStackTrace());
            Utils.showError("Sql:" + sqlStr);
            Utils.showError("Data:" + sData);
            ex.printStackTrace();

        } finally {
            // closeConnection(cnn, pstmt, null);
            return res;
        }     
    }  // updateRequested()() 
    
    private static int startRequested(int rowno) {
        Connection cnn = DbConn.getPooledConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sqlStr = "";
        String sData = "";
        int res = 0;
        
        try {
            sqlStr = "UPDATE ARCELIK_ARON_REQUESTS SET PROCESS_START_TIMESTAMP = SYSTIMESTAMP WHERE REQUEST_ROWNO=?";
            pstmt = cnn.prepareStatement(sqlStr);
            pstmt.clearParameters();
            int colNo = 1;
            pstmt.setInt(colNo++, rowno);
            res = pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception ex) {
            Utils.showError("Exception : startRequested() : " + ex.fillInStackTrace());
            Utils.showError("Sql:" + sqlStr);
            Utils.showError("Data:" + sData);
            ex.printStackTrace();

        } finally {
            // closeConnection(cnn, pstmt, null);
            return res;
        }     
    }  // updateRequested()() 
    
    private static void closeConnection(Connection cnn, PreparedStatement pstmt, ResultSet rset) {
        if (cnn != null) {
          try {
            cnn.commit();
            cnn.close();
          } catch (Exception e) {
            ;
          }
        }
        if (pstmt != null) {
          try {
            pstmt.close();
          } catch (Exception e) {
            ;
          }
        }
        if (rset != null) {
          try {
            rset.close();
          } catch (Exception e) {
            ;
          }
        }
    } // closeConnection()
    
    private static String getComputerName() {
        String _computerName = "";
        String _userName = "-";

        Map<String, String> env = System.getenv();
        if (env.containsKey("USERNAME")) {
            _userName = env.get("USERNAME");
        } 
        
        if (env.containsKey("COMPUTERNAME")) {
            _computerName = env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            _computerName = env.get("HOSTNAME");
        } else {
            _computerName = "Unknown Computer";
        }
        
        return _computerName + " (" + _userName + ")";
    }    
        
}
