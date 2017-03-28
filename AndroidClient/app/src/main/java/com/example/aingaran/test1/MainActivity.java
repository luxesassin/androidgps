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
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/*------------------------------------------------------------------------------------------------
-- SOURCE FILE: MainActivity.java
--
-- PROGRAM:     COMP4985_Assignment_3 - Android GPS
--
-- FUNCTIONS:   protected void onCreate(Bundle savedInstanceState)
--              void ConnectButton(final View view)
--              void run()
--              static String getMacAddr()
--
-- DATE:        March 24, 2017
--
-- DESIGNER:    Aing Ragunathan and Michael Goll
--
-- PROGRAMMER:  Aing Ragunathan and Michael Goll
--
-- NOTES:       This class creates the main UI as well as threads for sending data to a specified
--              remote server using the TCP/IP protocol suite.
--              Data sent to the server consists of device's MAC address, user's username, user's
--              current longitude, latitude, and a timestamp.
--              It also sends data periodically depending on the user's preference.
--              Connections can be created over either Wi-Fi or a wireless data service.
--              Collected data is displayed from a webapp which can be found at:
--                  http://159.203.12.137/map.php
--------------------------------------------------------------------------------------------------*/


public class MainActivity extends AppCompatActivity {
    TextView macInput;                  //displays the mac address of the device
    EditText frequencyNumberEditText;   //gets input about the frequency from the user
    Spinner frequencyTypeSpinner;       //gets input about the intervals of frequency from user
    String frequencyTypeInput;          //holder for the interval of frequency
    EditText ipInput;                   //gets input about the server's ip address
    EditText portInput;                 //gets input about the server's port
    EditText usernameInput;             //gets input for the user's username


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
    -- PARAMETER:  Bundle savedInstanceState - the saved instance of this program
    --
    -- RETURNS:    void
    --
    -- NOTES:      This function creates the GUI and permissions for wifi and location
    ----------------------------------------------------------------------------------------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);    //set wifi permissions

        ActivityCompat.requestPermissions(this, new
                String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //set location permissions
        //String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);


        //set the mac address
        macInput = (TextView) findViewById(R.id.macInput);  //get the mac address
        macInput.setText(getMacAddr());                     //set the mac address in GUI

        //setup edit boxes for user input
        frequencyNumberEditText = (EditText) findViewById(R.id.frequencyNumberEditText);
        ipInput = (EditText) findViewById(R.id.ipInput);
        portInput = (EditText) findViewById(R.id.portInput);
        usernameInput = (EditText) findViewById(R.id.usernameInput);

        //setup frequency of sending dat
        frequencyTypeInput = "Minutes"; //set default frequencyType
        //setup spinner object
        frequencyTypeSpinner = (Spinner) findViewById(R.id.frequencyTypeSpinner);
        //add content to spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        frequencyTypeSpinner.setAdapter(adapter);
        frequencyTypeSpinner.setSelection(1);   //set the default value
        frequencyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //frequencyTypeInput = parent.getItemAtPosition(position).toString();
                frequencyTypeInput = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*-----------------------------------------------------------------------------------------------
    -- FUNCTION:   ConnectButton
    --
    -- DATE:       March 24, 2017
    --
    -- DESIGNER:   Michael Goll
    --
    -- PROGRAMMER: Michael Goll
    --
    -- INTERFACE:  ConnectButton(final View view)
    --
    -- PARAMETER:  final View view -
    --
    -- RETURNS:    void
    --
    -- NOTES:      This function reacts to the Connect button being pressed
    --             It sets the frequency of a timer and the timer creates connections to the server
    --             on a schedule.
    ----------------------------------------------------------------------------------------------- */
    public void ConnectButton(final View view) {
        int frequencyInput = Integer.parseInt(frequencyNumberEditText.getText().toString());
        final String ip = ipInput.getText().toString();
        final int port = Integer.parseInt(portInput.getText().toString());
        final String username = usernameInput.getText().toString();
        int frequency = 60000;

        //get the chosen frequency interval from the spinner object
        frequencyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //frequencyTypeInput = parent.getItemAtPosition(position).toString();
                frequencyTypeInput = parent.getItemAtPosition(position).toString();
                frequencyTypeSpinner.setSelection(1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //check the spinner input and set the frequency accordingly
        switch (frequencyTypeInput) {
            //check for seconds
            case "Seconds":
                frequency = 1000 * frequencyInput;
                break;
            //check for minutes
            case "Minutes":
                frequency = 1000 * 60 * frequencyInput;
                break;
            //check for hours
            case "Hours":
                frequency = 1000 * 3600 * frequencyInput;
                break;
            default:
                frequency = 1000 * 60 * frequencyInput;
        }

        //create a timer schedule and make connections periodically
        new Timer().schedule(new TimerTask() {
            public void run() {
                new Thread(new ClientThread(username, ip, port)).start(); //client thread
            }
        }, 1, frequency);

    }


    class ClientThread implements Runnable{
        int PORT = 7424;    //default port number
        String IP = "159.203.12.137";  //default ngnix server IP address
        String username = "";

        public ClientThread(String usernameInput, String ipInput, int portInput){
            IP = ipInput;               //set the IP address of the server
            PORT = portInput;           //set the port number of the server
            username = usernameInput;   //set the username of the user
        }

        /*-----------------------------------------------------------------------------------------------
        -- FUNCTION:   run
        --
        -- DATE:       March 24, 2017
        --
        -- DESIGNER:   Aing Ragunathan
        --
        -- PROGRAMMER: Aing Ragunathan
        --
        -- INTERFACE:  run()
        --
        -- PARAMETER:  none
        --
        -- RETURNS:    void
        --
        -- NOTES:      This function creates a connection to the server and sends JSON objects with
        --             data consisting of the device's mac address, user's username, current lat and
        --             long coordinates.
        ----------------------------------------------------------------------------------------------- */
        @Override
        public void run(){
            try
            {
                double longitude = 0;
                double latitude = 0;

                //create socket to connect to server
                Socket client = new Socket (IP, PORT);  //create a socket for sending

                //check the permissions for location tracking
                int permissionCheck;
                permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION );

                //only provide location if permission was granted
                if(permissionCheck == PERMISSION_GRANTED) {
                    LocationManager lm = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);
                    //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }
                }
                else{
                    longitude = 46.1;
                    latitude = 46.1;
                }

                //get the current timestamp
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();


                //create a JSON object with appropriate data
                JSONObject jsonObject = new JSONObject()
                        .put("mac", getMacAddr())       //mac address of device
                        .put("username", username)      //user's username
                        .put("latitude", latitude)      //current latitude
                        .put("longitude", longitude)    //current longitude
                        .put("time", dateFormat.format(date)); //timestamp
                String jsonMsg = jsonObject.toString(); //convert to string before sending
                Log.d("JSON output: ", jsonMsg);



                // Send client string to server
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream (outToServer);
                //out.writeUTF(ClientString + client.getLocalSocketAddress());
                out.writeBytes(jsonMsg);    //write the bytes to the stream

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

    /*-----------------------------------------------------------------------------------------------
    -- FUNCTION:   getMacAddr
    --
    -- DATE:       March 24, 2017
    --
    -- DESIGNER:   Michael Goll
    --
    -- PROGRAMMER: Michael Goll
    --
    -- INTERFACE:  String getMacAddr()
    --
    -- PARAMETER:  none
    --
    -- RETURNS:    void
    --
    -- NOTES:      This helper function gets the mac address of the device.
    ----------------------------------------------------------------------------------------------- */
    public static String getMacAddr() {
        try {
            //get all the different network interfaces
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            //find the wlan0 wifi interface
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
                return res1.toString(); //mac address in string form
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
