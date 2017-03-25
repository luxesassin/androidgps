package com.example.aingaran.test1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    EditText frequencyNumberEditText;
    Spinner frequencyTypeSpinner;
    String frequencyTypeInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //default frequencyNumber
        frequencyNumberEditText = (EditText) findViewById(R.id.frequencyNumberEditText);

        //default frequencyType
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

        @Override
        public void run(){
            try
            {
                String ClientString;

                // Connect to the server
                System.out.println ("Connecting to " + IP + " on port " + PORT);
                //Toast.makeText(this, "Connecting to " + IP+ " on port " + PORT, Toast.LENGTH_LONG).show();




                Socket client = new Socket (IP, PORT);
                System.out.println ("Successful connection to: " + client.getRemoteSocketAddress());
                //Toast.makeText(this, "Successful connection to: " + client.getRemoteSocketAddress(), Toast.LENGTH_LONG).show();

                // Get console input
                //BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
                //ClientString = "hello server!";
                String jsonMsg = new JSONObject()
                        .put("macId", 1)
                        .put("uName", "aing")
                        .put("timeInterval", 1)
                        .put("latitude", 1)
                        .put("longitude", 2).toString();


                // Send client string to server
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream (outToServer);
                //out.writeUTF(ClientString + client.getLocalSocketAddress());
                out.writeUTF(jsonMsg+ client.getLocalSocketAddress());
                InputStream inFromServer = client.getInputStream();

                // Get the echo from server
                DataInputStream in = new DataInputStream (inFromServer);
                System.out.println("Server Echo: " + in.readUTF());
                //Toast.makeText(this, "Server Echo: " + in.readUTF(), Toast.LENGTH_LONG).show();

                client.close();
            }

            catch(IOException e)
            {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
