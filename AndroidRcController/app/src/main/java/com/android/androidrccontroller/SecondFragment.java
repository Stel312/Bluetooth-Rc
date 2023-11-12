package com.android.androidrccontroller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.androidrccontroller.databinding.FragmentSecondBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic servo;
    private BluetoothGattCharacteristic motor;

    private final DecimalFormat df = new DecimalFormat("#.##");

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                gatt.setPreferredPhy(BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_OPTION_NO_PREFERRED);
                gatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BluetoothGattService gattService = gatt.getService(UUIDStrings.Service_UUID);
            servo = gattService.getCharacteristic(UUIDStrings.SERVO_UUID);
            motor = gattService.getCharacteristic(UUIDStrings.MOTOR_UUID);

        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    public void onKeydown(int keyCode, KeyEvent event) {
    }

    @SuppressLint("MissingPermission")
    public void onGenericMotionEvent(@NonNull MotionEvent event) {

        if (bluetoothGatt != null && servo != null
                && motor != null) {
            // Handle joystick input
            float rx = event.getAxisValue(MotionEvent.AXIS_Z);
            float ry = event.getAxisValue(MotionEvent.AXIS_RZ);
            float x = event.getAxisValue(MotionEvent.AXIS_X);
            float y = event.getAxisValue(MotionEvent.AXIS_Y);
            float throttle = event.getAxisValue(MotionEvent.AXIS_GAS);
            float brake = event.getAxisValue(MotionEvent.AXIS_BRAKE);
            float intMotor = 0;
            if (throttle > 0.1f)
                intMotor = throttle;
            else if (brake > 0.1f)
                intMotor = -brake;
            else
                intMotor = 0;
            float f  = Float.parseFloat(df.format(intMotor * Integer.parseInt(binding.MotorScaling.getText().toString())));
            float f1  = Float.parseFloat(df.format(90 + x * Integer.parseInt(binding.steerScaling.getText().toString())));

            byte[] b = new byte[]{(byte) f, (byte) f1};
            bluetoothGatt.writeCharacteristic(servo, b, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            binding.LX.setText("LX: " + x);
            binding.LY.setText("LY: " + y);
            binding.RX.setText("RX: " + rx);
            binding.RY.setText("RY: " + ry);
            binding.Throttle.setText("Throttle: " + throttle);
            binding.Brake.setText("Brake: " + brake);


            // Implement your joystick handling logic here
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, Bundle savedInstanceState) {
        BluetoothDevice device;
        container.removeAllViews();
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        device = getArguments().getParcelable("device", BluetoothDevice.class);
        ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.BLUETOOTH_CONNECT);
        this.bluetoothGatt = device.connectGatt(getContext(), true, bluetoothGattCallback);
        bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        bluetoothGatt.requestMtu(50);

        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity a = (MainActivity) getActivity();
        assert a != null;
        a.getCurrentFragment();
        binding.buttonSecond.setOnClickListener(view1 -> {
            if (bluetoothGatt != null )
                bluetoothGatt.disconnect();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (bluetoothGatt != null)
            bluetoothGatt.disconnect();
    }

}