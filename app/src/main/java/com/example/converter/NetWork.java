package com.example.converter;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NetWork {

    TimeAndDate timeAndDate = new TimeAndDate();

    ArrayList<Currency>  LoadedCurr = new ArrayList<Currency>();

    private static final String REQUEST_METHOD_GET = "GET";
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    public  void UpdateCurrency(){
        Long currentTime = timeAndDate.getCurrentDate();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(currentTime);

        startSendHttpRequestThread("https://www.cbar.az/currencies/" + currentDateandTime + ".xml");


//        LoadData();

    }




    /* Start a thread to send http request to web server use HttpURLConnection object. */
    private void startSendHttpRequestThread(final String reqUrl)
    {
        Thread sendHttpRequestThread = new Thread()
        {
            @Override
            public void run() {
                // Maintain http url connection.
                HttpURLConnection httpConn = null;

                // Read text input stream.
                InputStreamReader isReader = null;

                // Read text into buffer.
                BufferedReader bufReader = null;

                // Save server response text.
                StringBuffer readTextBuf = new StringBuffer();






                try {
                    // Create a URL object use page url.
                    URL url = new URL(reqUrl);

                    // Open http connection to web server.
                    httpConn = (HttpURLConnection)url.openConnection();

                    // Set http request method to get.
                    httpConn.setRequestMethod(REQUEST_METHOD_GET);

                    // Set connection timeout and read timeout value.
                    httpConn.setConnectTimeout(10000);
                    httpConn.setReadTimeout(10000);

                    // Get input stream from web url connection.
                    InputStream inputStream = httpConn.getInputStream();


                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(httpConn.getInputStream());
//
                    NodeList nodes = doc.getElementsByTagName("ValType");
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element = (Element) nodes.item(i);
                        NodeList title = element.getElementsByTagName("Valute");

                        String Type = element.getAttribute("Type");

                        if (Type.toString().equals("Xarici valyutalar") ){

                            for (int j=0;j<title.getLength();j++){
                                Element line = (Element) title.item(j);

                                NodeList ChildNodes = line.getChildNodes();

                                String Code = line.getAttribute("Code");

                                Element Nominal = (Element) ChildNodes.item(1);

                                String NominalValue  = Nominal.getChildNodes().item(0).getNodeValue();

                                Element Name = (Element) ChildNodes.item(3);

                                String NameValue  = Name.getChildNodes().item(0).getNodeValue();

                                Element Value = (Element) ChildNodes.item(5);

                                String Curr =  Value.getChildNodes().item(0).getNodeValue();


                                LoadedCurr.add(new Currency(Code,NameValue,Double.parseDouble(Curr),Double.parseDouble(NominalValue)));



                                //LoadData(Code,NameValue,Curr,currentTime);
                            }

                        }



                    }



//
                }catch(MalformedURLException ex)
                {
                    Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                }catch(IOException ex)
                {
                    Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bufReader != null) {
                            bufReader.close();
                            bufReader = null;
                        }

                        if (isReader != null) {
                            isReader.close();
                            isReader = null;
                        }

                        if (httpConn != null) {
                            httpConn.disconnect();
                            httpConn = null;
                        }
                    }catch (IOException ex)
                    {
                        Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                    }
                }
            }
        };
        // Start the child thread to request web page.
        sendHttpRequestThread.start();
    }

    public void LoadData(){

        Long currentTime = timeAndDate.getStartOfTheDay(timeAndDate.getCurrentDate());

        Currency Curr = new Currency();

        LoadedCurr.add(new Currency("AZN","Azərbaycan manatı",1,1));

        for (int i = 0; i <= LoadedCurr.size()-1;i++) {

            Currency E = LoadedCurr.get(i);

            boolean DataExists = Curr.CheckDataExistence(E.getShortName());

            if (!DataExists){
                Curr.AddDataToDataBase(E.getShortName(), E.getFullName());
            }

            DataExists = Curr.CheckRatesDataExistence(E.getShortName(),Double.toString(E.getRate()),currentTime);


            if (!DataExists){
                Curr.AddRatesDataToDataBase(E.getShortName(),Double.toString(E.getRate()),currentTime,Double.toString(E.getNominal()));
            }
        }



    }

}
