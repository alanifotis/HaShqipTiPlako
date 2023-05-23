package com.example.hashqiptiplako;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BluetoothManager.BluetoothConnectionListener {
    private static final String TAG = "MainActivity";
    private BluetoothManager bluetoothManager;
    private ArrayAdapter<BluetoothDevice> deviceListAdapter;

    private static final int PERMISSION_REQUEST_LOCATION = 2;

    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b5 = findViewById(R.id.b5);
        Button b6 = findViewById(R.id.b6);
        Button b7 = findViewById(R.id.b7);
        Button b8 = findViewById(R.id.b8);
        GameOnActivity game = new GameOnActivity();

        new PushTheButton(this, game, b5, b6, b7, b8);

        // Initialize BluetoothManager
        bluetoothManager = new BluetoothManager(this);
        bluetoothManager.setConnectionListener(this);
        // Inside your onCreate() or initialization method
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }


    public void pairWithDevice(View view) {
        // Check if Bluetooth is supported and enabled
        if (bluetoothManager.isBluetoothSupported()) {
            if (!bluetoothManager.isBluetoothEnabled()) {
                // Bluetooth is not enabled, request enable
                bluetoothManager.requestEnableBluetooth();
            } else {
                // Check if location permission is granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request them
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_LOCATION);
                } else {
                    // Permissions are granted, start device scanning
                    BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
                    bluetoothScanner.startScan(null, new ScanSettings.Builder().build(), scanCallback);
                }
            }
        } else {
            // Bluetooth is not supported on this device
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
        }
    }


    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // Handle scanned device here
            BluetoothDevice device = result.getDevice();
            // Extract device information
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions
                // and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions,
                // int[] grantResults) to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();

            // Do something with the device information
            Log.d(TAG, "Scanned Device: " + deviceName + " (" + deviceAddress + ")");
        }


        @Override
        public void onScanFailed(int errorCode) {
            // Handle scan failure here
            Log.e(TAG, "Scan failed with error code: " + errorCode);
        }
    };




    public void showDeviceListDialog(List<BluetoothDevice> deviceList) {
        // Create the list of devices dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Device");

        // Inflate the layout for each device item
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.layout_device_list_item, null);
        builder.setView(dialogView);



        // Create an ArrayAdapter to display the list of devices
        deviceListAdapter = new ArrayAdapter<>(this, R.layout.layout_device_list_item, deviceList);
        builder.setAdapter(deviceListAdapter, (dialog, which) -> {
            // Get the selected device
            BluetoothDevice selectedDevice = deviceListAdapter.getItem(which);
            if (selectedDevice != null) {
                // Connect to the selected device
                bluetoothManager.connectToDevice(selectedDevice);
            }
        });

        // Create and show the dialog
        AlertDialog deviceListDialog = builder.create();
        deviceListDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop discovery and unregister receiver to avoid memory leaks
        bluetoothManager.stopDiscovery();
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        // Handle discovered device
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        } else {
            // Permission is granted, connect to the discovered device
            bluetoothManager.connectToDevice(device);
            // Permission is granted, add the discovered device to the list
            showDeviceListDialog(Collections.singletonList(device));
        }
    }


    @Override
    public void onDeviceConnected(BluetoothSocket socket) {
        // Handle device connected
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "Device connected: " + socket.getRemoteDevice().getName());
        // You can start the game or perform any other necessary actions here
    }

    @Override
    public void onConnectionFailed() {
        // Handle connection failed
        Log.e(TAG, "Failed to connect to device");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // Location and Bluetooth permissions granted, start device discovery
                bluetoothManager.startDiscovery();
            } else {
                // Permissions denied, show a message or handle it accordingly
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }






}