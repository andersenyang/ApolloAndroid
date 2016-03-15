package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.  
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MyApplicationActivity extends Activity {
    //private HueAPIHelper hueHelper;
    private GestureHandler gestureHandler;
    public static final String TAG = "Imperium";

    private BLEService mService;
    private TextView textView;
    private CanvasView canvasView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        gestureHandler = new GestureHandler(this);
        //hueHelper = new HueAPIHelper();

        Button switchDevicesButton, actionOneButton,
                actionTwoButton, connectHueButton;
        switchDevicesButton = (Button) findViewById(R.id.switchDevicesButton);
        actionOneButton = (Button) findViewById(R.id.actionOneButton);
        actionTwoButton = (Button) findViewById(R.id.actionTwoButton);
        connectHueButton = (Button) findViewById(R.id.connectHueButton);

        switchDevicesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(6);
            }

        });

        actionOneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(1);
            }

        });

        actionTwoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureHandler.handleGesture(2);
            }

        });

        connectHueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PHHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.gesture);
        canvasView = (CanvasView) findViewById(R.id.canvasView);

        Log.d("Imperium", "onCreate");
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(gestureUpdated, new IntentFilter("NEW_GESTURE"));
        registerReceiver(drawCoordinates, new IntentFilter("COORDINATES"));
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d("Imperium", "onServiceConnected");
            mService = ((BLEService.LocalBinder) service).getService();
            if (!mService.initialize()) {
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }

    };

    private BroadcastReceiver gestureUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int gesture = intent.getExtras().getInt("Key");
            Log.d(TAG, "GESTURE IS");
            Log.d(TAG, String.valueOf(gesture));
            //textView.setText(String.valueOf(gesture));
            gestureHandler.handleGesture(gesture);
        }
    };

    private BroadcastReceiver drawCoordinates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            canvasView.reset();
            String coordinate_string = intent.getExtras().getString("Key");
            char[] coordinate_array = coordinate_string.toCharArray();
            Log.d(TAG, "ARR LENGTH");
            Log.d(TAG, String.valueOf(coordinate_array.length));
            for (int i = 0; i < coordinate_array.length; i++) {
                if (coordinate_array[i] == '1') {
                    canvasView.setPoint(i);
                }
            }
            Log.d(TAG, coordinate_string);
        }
    };


    @Override
    protected void onDestroy() {
        //hueHelper.close();
        gestureHandler.close();
        super.onDestroy();

        unbindService(mServiceConnection);
        mService = null;
    }
}
