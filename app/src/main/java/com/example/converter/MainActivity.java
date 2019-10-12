package com.example.converter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase myDB;
    Button ActionopenCurrList;
    private static final String REQUEST_METHOD_GET = "GET";
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBConnections dbConnections = new DBConnections();

        myDB = openOrCreateDatabase("myCurr.db", MODE_PRIVATE, null);
        dbConnections.myDB = myDB;

        //myDB.execSQL(
                //"CREATE TABLE IF NOT EXISTS Currency (ShortName VARCHAR(200), FullName VARCHAR(200))");

        dbConnections.CreateCurrencyTable();
        dbConnections.CreateRatesTable();
        ActionopenCurrList = (Button)findViewById(R.id.OpenCurrList);

        AddButtonListener();

//        try {

         Long currentTime = Calendar.getInstance().getTimeInMillis();
         SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
         String currentDateandTime = sdf.format(currentTime);

            startSendHttpRequestThread("https://www.cbar.az/currencies/" + currentDateandTime + ".xml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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

                            for (int j=0;j<=title.getLength();j++){
                                Element line = (Element) title.item(j);

                                NodeList ChildNodes = line.getChildNodes();

                                Element Nominal = (Element) ChildNodes.item(1);

                                String NominalValue  = Nominal.getChildNodes().item(0).getNodeValue();

                                Element Name = (Element) ChildNodes.item(3);

                                String NameValue  = Name.getChildNodes().item(0).getNodeValue();

                                Element Value = (Element) ChildNodes.item(5);

                                String Curr =  Value.getChildNodes().item(0).getNodeValue();

                                int b =0;
                            }

                        }



                    }



//                     Create input stream reader based on url connection input stream.
//                    isReader = new InputStreamReader(inputStream);
//
//                    // Create buffered reader.
//                    bufReader = new BufferedReader(isReader);
//
//                    // Read line of text from server response.
//                    String line = bufReader.readLine();
//
//                    // Loop while return line is not null.
//                    while(line != null)
//                    {
//                        // Append the text to string buffer.
//                        readTextBuf.append(line);
//
//                        // Continue to read text line.
//                        line = bufReader.readLine();
//                    }

                    // Send message to main thread to update response text in TextView after read all.
//                    Message message = new Message();

                    // Set message type.
//                    message.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
//
//                    // Create a bundle object.
//                    Bundle bundle = new Bundle();
//                    // Put response text in the bundle with the special key.
//                    bundle.putString(KEY_RESPONSE_TEXT, readTextBuf.toString());
//                    // Set bundle data in message.
//                    message.setData(bundle);
//                    // Send message to main thread Handler to process.
//                    uiUpdater.sendMessage(message);
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






    public void AddButtonListener(){
        ActionopenCurrList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".CurrencyList");
                startActivity(intent);
            }
        });
    }

}
