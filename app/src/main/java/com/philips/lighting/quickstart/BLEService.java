package com.philips.lighting.quickstart;

/**
 * Created by supreet on 2016-03-05.
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

import java.util.UUID;

public class BLEService extends Service {

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothAddress;
    private GestureHandler gestureHandler;
    private Context appContext;

    private String TAG = "Imperium";
    private BLEService self = this;

    private String SERVICE_UUID = "17AFB91F-1C12-4854-9FFA-9363F530A436";
    private String CHARACTERISTIC_UUID = "DE3D85EB-1602-4967-8EA2-0F59B27690CD";
    private String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public BLEService() {
    }

    @Override
    public void onDestroy() {
        gestureHandler.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // @Override
    public IBinder unBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize(Context c) {
        gestureHandler = GestureHandler.getInstance(c);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            return true;  // temporarily disable bluetooth requirement
        }
        scanner.startScan(mScanCallback);

        Intent i = new Intent("NEW_GESTURE");
        i.putExtra("Key", 0);

        sendBroadcast(i);

        //Log.d(TAG, "Scan Started");

        HueHelper helper = new HueHelper();

        return true;
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result.getDevice() != null) {
                BluetoothDevice device = result.getDevice();
                if (device.getName() == null) { return; }
                if (device.getName().equals("buckle")) {    //TODO: Hardcoded name, change logic
                    Log.d(TAG, "Buckle found");
                    connect(device.getAddress());
                }
            }
        }
    };

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothAddress != null && address.equals(mBluetoothAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            //Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothAddress = address;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                       mBluetoothGatt.discoverServices());
                //mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    //Log.d(TAG, service.getUuid().toString());
                    if (service.getUuid().toString().equals(UUID.fromString(SERVICE_UUID).toString())) {
                        Log.d(TAG, "Service found");
                        for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
                            Log.d(TAG, c.getUuid().toString());
                        }

                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                        if (characteristic != null) {
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(DESCRIPTOR_UUID));
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            Log.d(TAG, String.valueOf(gatt.readCharacteristic(characteristic)));
                        }
                    }
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            byte[] arr = characteristic.getValue();
            int arr_length = arr.length;

            if (arr_length == 4) {
                // This means a gesture was sent
                int gesture = Integer.valueOf(arr[0]);
                Intent i = new Intent("NEW_GESTURE");
                i.putExtra("Key", gesture);

                sendBroadcast(i);
                gestureHandler.handleGesture(gesture);
                return;
            } else {

                StringBuilder builder = new StringBuilder();
                for (byte b : arr) {
                    String str = Integer.toBinaryString((b & 0xFF));
                    while (str.length() < 8) {
                        String tmp = "0";
                        tmp += str;
                        str = tmp;
                    }
                    str = new StringBuilder(str).reverse().toString();
                    builder.append(str);
                }
                String s = builder.toString();
                Intent i = new Intent("COORDINATES");
                i.putExtra("Key", s);
                sendBroadcast(i);
            }

        }
    };



}
