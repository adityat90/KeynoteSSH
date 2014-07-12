package com.addz.keynotessh.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.io.InputStream;


public class MainActivity extends Activity {

    SSHManager instance;

    boolean isConnected = false;

    EditText ipEditText, usernameEditText, passwordEditText;

    Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = (EditText) findViewById(R.id.ipEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        connectButton = (Button) findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setEnabled(false);
                if(ipEditText.getText().length() > 0 && usernameEditText.getText().length() > 0 && passwordEditText.getText().length() > 0) {
                    new Connection().execute(usernameEditText.getText().toString(), passwordEditText.getText().toString(),ipEditText.getText().toString());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isConnected) {
            instance.close();
        }
    }

    class SendCommand extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            if(isConnected) {
                String result = instance.sendCommand(strings[0]);

                Log.e("RESULT", result);
            }
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            Log.e("LOL", "Sending prev command");
            new SendCommand().execute("osascript -e 'tell application \"Keynote\" to show previous'");
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            Log.e("LOL", "Sending next command");
            new SendCommand().execute("osascript -e 'tell application \"Keynote\" to show next'");
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    class Connection extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... datas) {

            String userName = datas[0];
            String password = datas[1];
            String connectionIP = datas[2];

            instance = new SSHManager(userName, password, connectionIP, "");
            String errorMessage = instance.connect();

            isConnected = true;
            if(errorMessage != null)
            {
                Log.e("ERROR MESSAGE", errorMessage);
                isConnected = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ipEditText.setEnabled(false);
            usernameEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            new SendCommand().execute("ls");
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
