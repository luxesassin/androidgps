package com.example.aingaran.test1;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/*------------------------------------------------------------------------------------------------
-- SOURCE FILE: MainActivity.java
--
-- PROGRAM:     COMP4985_Assignment_3 - Android GPS
--
-- FUNCTIONS:
--
-- DATE:        March 24, 2017
--
-- DESIGNER:    Aing Ragunathan and Michael Goll
--
-- PROGRAMMER:  Aing Ragunathan and Michael Goll
--
-- NOTES:
--------------------------------------------------------------------------------------------------*/


public class MainActivity extends AppCompatActivity {
    TextView macInput;
    EditText frequencyNumberEditText;
    Spinner frequencyTypeSpinner;
    String frequencyTypeInput;

    /*-----------------------------------------------------------------------------------------------
    -- FUNCTION:   onCreate
    --
    -- DATE:       March 24, 2017
    --
    -- DESIGNER:   Aing Ragunathan
    --
    -- PROGRAMMER: Aing Ragunathan
    --
    -- INTERFACE:  onCreate(Bundle savedInstanceState)
    --
    -- PARAMETER:  Bundle savedInstanceState
    --
    -- RETURNS:    void
    --
    -- NOTES:
    ----------------------------------------------------------------------------------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,new
                String[]{Manifest.permission.ACCESS_WIFI_STATE },1);

        ActivityCompat.requestPermissions(this,new
                String[]{Manifest.permission.ACCESS_FINE_LOCATION },1);
                //String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);



        //set the mac address
        macInput = (TextView) findViewById(R.id.macInput);
        macInput.setText(getMacAddr());

        //set default frequencyNumber
        frequencyNumberEditText = (EditText) findViewById(R.id.frequencyNumberEditText);

        //set default frequencyType
        frequencyTypeInput = "hour";
        frequencyTypeSpinner = (Spinner) findViewById(R.id.frequencyTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        frequencyTypeSpinner.setAdapter(adapter);
        frequencyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(),parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                frequencyTypeInput = parent.getItemAtPosition(position).toString();
                //Toast.makeText(PlanTrip.this, frequencyNumberInput + " FREQU", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*-----------------------------------------------------------------------------------------------
    -- FUNCTION:
    --
    -- DATE:       March 24, 2017
    --
    -- DESIGNER:   Aing Ragunathan
    --
    -- PROGRAMMER: Aing Ragunathan
    --
    -- INTERFACE:
    --
    -- PARAMETER:
    --
    -- RETURNS:    void
    --
    -- NOTES:
    ----------------------------------------------------------------------------------------------- */
    public void ConnectButton(final View view){
        int frequency  = Integer.parseInt(frequencyNumberEditText.getText().toString());

        switch (frequencyTypeInput){
            //check for minutes
                //frequency*60
            //check for hours
                //frequency*3600
            //days
                //frequency*86400
        }


        new Thread(new ClientThread(frequency)).start(); //client thread
    }


    class ClientThread implements Runnable{

        //int PORT = 27156;     //ngnix server
        int PORT = 7424;
        String IP = "159.203.12.137";  //ngnix server
        //String IP = "192.168.1.4";  //xps 13 on aing's LAN

        public ClientThread(int frequency){
            //send packet every [frequency] seconds
        }

        /*-----------------------------------------------------------------------------------------------
        -- FUNCTION:
        --
        -- DATE:       March 24, 2017
        --
        -- DESIGNER:   Aing Ragunathan
        --
        -- PROGRAMMER: Aing Ragunathan
        --
        -- INTERFACE:
        --
        -- PARAMETER:
        --
        -- RETURNS:    void
        --
        -- NOTES:
        ----------------------------------------------------------------------------------------------- */
        @Override
        public void run(){
            try
            {
                double longitude = 0;
                double latitude = 0;


                // Connect to the server
                System.out.println ("Connecting to " + IP + " on port " + PORT);

                Socket client = new Socket (IP, PORT);  //create a socket for sending
                System.out.println ("Successful connection to: " + client.getRemoteSocketAddress());


                int permissionCheck;
                permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION );

                if(permissionCheck == PERMISSION_GRANTED) {
                    LocationManager lm = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }
                }
                else{
                    longitude = 46.1;
                    latitude = 46.1;
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();


                // Get console input
                //BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
                //ClientString = "hello server!";
                JSONObject jsonObject = new JSONObject()
                        .put("mac", "1")
                        .put("username", "aing")
                        .put("latitude", latitude)
                        .put("longitude", longitude)
                        .put("time", dateFormat.format(date));//dateFormat.format(date));
                String jsonMsg = jsonObject.toString();
                Log.d("JSON output: ", jsonMsg);



                // Send client string to server
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream (outToServer);
                //out.writeUTF(ClientString + client.getLocalSocketAddress());
                out.writeBytes(jsonMsg);//, 0, jsonMsg.length());//+ client.getLocalSocketAddress());
                InputStream inFromServer = client.getInputStream();

                client.close(); //close the connection after object is sent
            }

            catch(IOException e)
            {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
