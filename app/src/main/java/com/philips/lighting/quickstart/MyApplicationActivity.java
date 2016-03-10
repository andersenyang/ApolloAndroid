package com.philips.lighting.quickstart;

import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.quickstart.HueAPIHelper;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.  
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MyApplicationActivity extends Activity {
//    private PHHueSDK phHueSDK;
//    private static final int MAX_HUE=65535;
    private HueAPIHelper hueHelper;
    public static final String TAG = "Imperium";

    private BLEService mService;
    private TextView textView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
//        phHueSDK = PHHueSDK.create();
        hueHelper = new HueAPIHelper();

        Button hueButton, brightnessButton, connectHueButton;
        hueButton = (Button) findViewById(R.id.hueButton);
        hueButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hueHelper.changeLightColour();
            }

        });

        brightnessButton = (Button) findViewById(R.id.brightnessButton);
        brightnessButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hueHelper.changeBrightness(true);
            }

        });

        connectHueButton = (Button) findViewById(R.id.connectHueButton);
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
        Log.d("Imperium", "onCreate");
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(gestureUpdated, new IntentFilter("NEW_GESTURE"));
        registerReceiver(drawCoordinates, new IntentFilter("COORDINATES"));
    }

//    public void randomLights() {
//        PHBridge bridge = phHueSDK.getSelectedBridge();
//
//        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
//        Random rand = new Random();
//
//        for (PHLight light : allLights) {
//            PHLightState lightState = new PHLightState();
//            lightState.setHue(rand.nextInt(MAX_HUE));
//            // To validate your lightstate is valid (before sending to the bridge) you can use:
//            // String validState = lightState.validateState();
//            bridge.updateLightState(light, lightState, listener);
//            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
//        }
//    }
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
            textView.setText(String.valueOf(gesture));

        }
    };

    private BroadcastReceiver drawCoordinates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String coordinate_string = intent.getExtras().getString("Key");
            Log.d(TAG, coordinate_string);
        }
    };


    @Override
    protected void onDestroy() {
        hueHelper.close();
        super.onDestroy();

        unbindService(mServiceConnection);
        mService = null;
    }
}
