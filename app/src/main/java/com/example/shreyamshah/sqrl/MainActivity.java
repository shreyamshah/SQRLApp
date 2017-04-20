package com.example.shreyamshah.sqrl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.*;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

import static com.example.shreyamshah.sqrl.R.id.scan;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView uname;
    TextView uemail;
   private static User u;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        uname=(TextView)header.findViewById(R.id.textView1);
        uemail=(TextView)header.findViewById(R.id.textView2);
        preferences=getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if(preferences.contains("name"))
        {
        u=new User(preferences.getString("name",null),preferences.getString("email",null),preferences.getString("no",null));
            Toast.makeText(getApplicationContext(),"Welcome Back",Toast.LENGTH_SHORT).show();
            uname.setText(u.name);
            uemail.setText(u.email);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == scan) {
                Intent bar = new Intent(MainActivity.this, Scan.class);
                startActivityForResult(bar, 1001);

            // Handle the camera action
        } else if (id == R.id.login) {
                Intent bar = new Intent(MainActivity.this, Login.class);
                startActivityForResult(bar, 1004);


        } else if (id == R.id.setting) {

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == 1001) {
            if(resultCode != RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Setting Variables",Toast.LENGTH_SHORT).show();
                u=new User(data.getStringExtra("fname").concat(" ").concat(data.getStringExtra("lname")),data.getStringExtra("email"),data.getStringExtra("no"));
                Toast.makeText(getApplicationContext(),"Hi "+u.name,Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("name",u.name);
                editor.putString("email",u.email);
                editor.putString("no",u.no);
                editor.apply();
                editor.commit();
                Toast.makeText(getApplicationContext(),"Commited",Toast.LENGTH_SHORT).show();
                uemail.setText(u.email);
                uname.setText(u.name);

            }
        }

    }
}