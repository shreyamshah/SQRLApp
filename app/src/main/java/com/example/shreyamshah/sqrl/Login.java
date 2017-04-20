package com.example.shreyamshah.sqrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import static com.example.shreyamshah.sqrl.Login.POST;

public class Login extends AppCompatActivity {

    SurfaceView sv;
    static TextView tv;
    static String s;
    CameraSource cameraSource;
    static SharedPreferences preferences;
    public static String POST(String s){

        tv.setVisibility(View.GONE);
        String result = "";

        try {
            URL url=new URL("http://192.168.1.104//SQRL//sign.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            Uri.Builder builder=new Uri.Builder().appendQueryParameter("no",s)
                    .appendQueryParameter("email",preferences.getString("email",null))
                    .appendQueryParameter("id",preferences.getString("no",null));
            String query=builder.build().getEncodedQuery();
            Log.d("query",query);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( query );
            wr.flush();
            String line=null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                Log.d("Response",line);
                tv.setText(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivityForResult(new Intent(Login.this, Finger.class),1003);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sv=(SurfaceView)findViewById(R.id.surfaceView1);
        tv=(TextView)findViewById(R.id.textView1);
        BarcodeDetector barcodeDetector= new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        if(!barcodeDetector.isOperational())
        {
            Toast.makeText(getApplicationContext(),"Barcode not Working", Toast.LENGTH_LONG).show();
        }
        if(isConnected()){
            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"You are NOT conncted",Toast.LENGTH_SHORT).show();
        }


        cameraSource=new CameraSource.Builder(this,barcodeDetector).setAutoFocusEnabled(true).build();
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(sv.getHolder());
                } catch (SecurityException se)
                {
                    Log.e("SECURITY EXCEPTION",se.getMessage());
                } catch (Exception ex)
                {
                    Log.e("Error",ex.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    tv.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            tv.setText(

                                    barcodes.valueAt(0).displayValue
                            );
                            s= barcodes.valueAt(0).displayValue;
                            tv.setVisibility(View.GONE);
                            cameraSource.release();
                            cameraSource.stop();
                            preferences=getSharedPreferences("prefs", Context.MODE_PRIVATE);
                            new Logon().execute();
                            Toast.makeText(getApplicationContext(),"Executed!!!!",Toast.LENGTH_SHORT).show();
                            if(tv.getText()!=null)
                                Toast.makeText(getApplicationContext(),tv.getText(),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1003) {
            if (resultCode == RESULT_CANCELED) {
                Intent i=new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        }
    }

}
class Logon extends AsyncTask<String, Void, String> {
    @Override
    public void onPreExecute()
    {
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(String result) {
        return;
    }
    @Override
    protected String doInBackground(String... args) {
        Log.d("d","Inside doInBackground");
        return Login.POST(Login.s);
    }}