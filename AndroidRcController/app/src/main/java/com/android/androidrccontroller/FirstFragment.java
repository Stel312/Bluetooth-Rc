package com.android.androidrccontroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.android.androidrccontroller.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    private final List<String> deviceListName = new ArrayList<>();
    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private NavController navController;
    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(!deviceList.contains(result.getDevice()))
            {
                deviceList.add(result.getDevice());
                if(result.getDevice().getName() == null)
                    arrayAdapter.add(result.getDevice().getAddress());
                else
                    arrayAdapter.add(result.getDevice().getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };


    public FirstFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView arrayView;
        MainActivity a = (MainActivity) getActivity();
        a.getCurrentFragment();
        if(bluetoothAdapter == null) {
            BluetoothManager bm = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bm.getAdapter();
        }
            this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        if (getContext() != null)
        {
            arrayAdapter = new ArrayAdapter(getContext(), R.layout.textlayer, R.id.device, deviceListName);
            arrayView  = binding.bluetoothList;
            arrayView.setAdapter(arrayAdapter);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHandler = new Handler();
        navController = Navigation.findNavController(view);
        binding.scan.setOnClickListener(clickView -> {
            if (!this.scanning()) {
                deviceList.clear();
                arrayAdapter.clear();
                this.scanForDevice();
            }
        });
        binding.bluetoothList.setOnItemClickListener((parent, view1, position, id) -> {
            BluetoothDevice bluetoothDevice =  deviceList.get(position);
            Bundle b = new Bundle();
            b.putParcelable("device", bluetoothDevice);

            navController.navigate(R.id.action_FirstFragment_to_SecondFragment, b);
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(scanning())
            bluetoothLeScanner.stopScan(scanCallback);

    }


    @SuppressLint("MissingPermission")
    public void scanForDevice()
    {
        List<ScanFilter> filters = new ArrayList<>(); // add to list of filters to apply to scan.
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        if (bluetoothAdapter.isEnabled()) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallback);

            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    public boolean scanning()
    {
        return mScanning;
    }
}