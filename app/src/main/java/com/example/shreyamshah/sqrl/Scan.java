package com.example.shreyamshah.sqrl;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.*;
import com.google.android.gms.vision.barcode.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import static com.example.shreyamshah.sqrl.Scan.POST;


public class Scan extends Activity {
    SurfaceView sv;
    TextView tv;
   static User user;
    String users[]=new String[4];

    CameraSource cameraSource;
    // check if you are connected or not

    public static String POST(){
        InputStream inputStream = null;
        String result = "";

            try {
                URL url=new URL("http://192.168.43.36//SQRL//insert.php");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Uri.Builder builder=new Uri.Builder().appendQueryParameter("name",user.name)
                        .appendQueryParameter("email",user.email)
                        .appendQueryParameter("no",user.no);
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
                   Log.d("log", sb.append(line + "\n").toString());
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
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
                            String s= barcodes.valueAt(0).displayValue;
                            tv.setVisibility(View.GONE);
                            cameraSource.release();
                            users=s.split("\\r?\\n");
                            startActivityForResult(new Intent(Scan.this, Finger.class),1002);

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode != RESULT_CANCELED) {
                user=new User(users[0]+" "+users[1],users[2],users[3]);
                new Register().execute();
                Toast.makeText(getApplicationContext(),"Executed!!!!",Toast.LENGTH_SHORT).show();
                Intent i=new Intent();
                i.putExtra("fname",users[0]);
                i.putExtra("lname",users[1]);
                i.putExtra("email",users[2]);
                i.putExtra("no",users[3]);
                setResult(RESULT_OK,i);
                Toast.makeText(getApplication(),"Sending Result",Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Intent i=new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }
        }
    }
}
class Register extends AsyncTask<String,Void,String>
{
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
        return POST();
    }
}

