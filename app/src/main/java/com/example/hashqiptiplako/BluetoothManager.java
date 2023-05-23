package com.example.hashqiptiplako;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver discoveryReceiver;
    private boolean isDiscovering = false;
    private BluetoothConnectionListener connectionListener;



    public BluetoothManager(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setConnectionListener(BluetoothConnectionListener listener) {
        connectionListener = listener;
    }

    public boolean isBluetoothSupported() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void requestEnableBluetooth() {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ((Activity) context).startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    public void startDiscovery() {
        if (isDiscovering) {
            return;
        }

        int permissionCheck = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            return;
        }

        discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (connectionListener != null) {
                        connectionListener.onDeviceDiscovered(device);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(discoveryReceiver, filter);

        isDiscovering = bluetoothAdapter.startDiscovery();
        if (!isDiscovering) {
            Log.e(TAG, "Failed to start device discovery");
        }
    }

    public void stopDiscovery() {
        if (isDiscovering) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.cancelDiscovery();
            context.unregisterReceiver(discoveryReceiver);
            isDiscovering = false;
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        ConnectThread connectThread = new ConnectThread(device);
        connectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice device;
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
        }

        public void run() {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                if (connectionListener != null) {
                    connectionListener.onDeviceConnected(socket);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to connect to device: " + e.getMessage());
                if (connectionListener != null) {
                    connectionListener.onConnectionFailed();
                }
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Failed to close socket: " + closeException.getMessage());
                }
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close socket: " + e.getMessage());
            }
        }
    }

    public interface BluetoothConnectionListener {
        void onDeviceDiscovered(BluetoothDevice device);

        void onDeviceConnected(BluetoothSocket socket);

        void onConnectionFailed();
    }
}
