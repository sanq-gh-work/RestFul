package com.sanq.example.RestFul;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Launcher extends Activity {
    private static Launcher instance;
    private String url = "http://androidexample.com/media/webservice/JsonReturn.php";

    public Launcher() {
        super();
        this.instance = this;
    }

    public static Launcher getInstance() {
        return instance;
    }
    public static Context getContext() {
        return getInstance().getApplicationContext();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button cmdGetServerData = (Button) findViewById(R.id.cmdGetServerData);
        cmdGetServerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongOperation().execute(url);
            }
        });


    }


    public class LongOperation extends AsyncTask<String, Void, Void> {

        private final HttpClient client = new DefaultHttpClient();
        private String content;
        private String error = null;
        private ProgressDialog dialog = new ProgressDialog(Launcher.this);
        TextView txtOutput = (TextView) findViewById(R.id.txtOutput);
        TextView txtJsonParsed = (TextView) findViewById(R.id.txtJsonParsed);
        EditText txtServerText = (EditText) findViewById(R.id.txtServerText);
        int sizeData = 0;
        String data = "";

                //("MyLog", "");



        @Override
        protected void onPreExecute() {

           // Log.d("MyLog", "ok 0");


            dialog.setMessage("Please wait...");
            dialog.show();

            try {
                data += "&" + URLEncoder.encode("data", "UTF-8") + "=" + txtServerText.getText();
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }


        @Override
        protected Void doInBackground(String... urls) {
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);

                URLConnection con = url.openConnection();
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(data);
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while( (line = reader.readLine()) != null ){
                    sb.append(line + " ");
                }
                content = sb.toString();

            } catch (Exception ex) {
                error = ex.getMessage();
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {/*NOP*/}
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused){
            dialog.dismiss();

            if (error != null){
                txtOutput.setText("Output : " + error);
            }else {
                txtOutput.setText(content);
            }

            /****************** Start Parse Response JSON Data *************/

            String outData = "";
            JSONObject jsonResponse;

            try {
                jsonResponse = new JSONObject(content);
                JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                int lengthJsonArr = jsonMainNode.length();

                for (int i = 0;i<lengthJsonArr;i++){
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    String name = jsonChildNode.optString("name");
                    String number = jsonChildNode.optString("number");
                    String date_added = jsonChildNode.optString("date_added");

                    outData +="Name                : " + name
                            + "Number              : " + number
                            + "Time                : " + date_added
                            + "--------------------------------------------------------------";

                    txtJsonParsed.setText(outData);
                }
            }
            catch(JSONException ex){
                ex.printStackTrace();
            }
        }

    }


}
