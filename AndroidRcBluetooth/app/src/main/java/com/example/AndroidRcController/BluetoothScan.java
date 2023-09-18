package com.example.AndroidRcController;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class BluetoothScan extends ListFragment {

    //private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothAdapter bluetoothAdapter ;
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public BluetoothScan(Context context, BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;

    }



    public List<BluetoothDevice> scanForDevice()
    {
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        List<ScanFilter> filters = new ArrayList<>(); // add to list of filters to apply to scan.


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        else
            bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
        return null;
    }

    public boolean stopScan()
    {
        if(bluetoothAdapter.isEnabled() )
            bluetoothLeScanner.stopScan(scanCallback);
        return true;
    }

    public ScanCallback getScanCallback()
    {
        return scanCallback;
    }

    public boolean scanning()
    {
        return mScanning;
    }
}
