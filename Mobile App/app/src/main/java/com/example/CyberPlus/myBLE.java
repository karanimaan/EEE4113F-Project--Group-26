package com.example.CyberPlus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;

public class myBLE {
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner blescanner = adapter.getBluetoothLeScanner();

    /*if (blescanner != null) {
        blescanner.startScan();
        Log.d(TAG, "scan started");
    }  else {
        Log.e(TAG, "could not get scanner object");
    }*/
}
