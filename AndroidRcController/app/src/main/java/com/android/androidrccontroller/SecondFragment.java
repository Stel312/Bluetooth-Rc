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
    private BluetoothDevice device;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic servo;
    private BluetoothGattCharacteristic motor;
    InputManager inputManager;
    InputDevice gamepad;

    private DecimalFormat df = new DecimalFormat("#.##");

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED)
                gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            gattService = gatt.getService(UUIDStrings.Service_UUID);
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

    public void onKeydown(int keyCode, KeyEvent event){
    }

    @SuppressLint("MissingPermission")
    public void onGenericMotionEvent(@NonNull MotionEvent event) {
        int source = event.getSource();

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
            if(throttle > 0.1f)
                intMotor = throttle;
            else if (brake > 0.1f)
                intMotor = -brake;
            else
                intMotor = 0;
            String s  = df.format(intMotor * Integer.parseInt(binding.MotorScaling.getText().toString()));
            s =s + df.format(90 + x * Integer.parseInt(binding.steerScaling.getText().toString()));

            /*JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("motor", df.format(intMotor * Integer.parseInt(binding.MotorScaling.getText().toString())));
                jsonObject.put("servo", df.format(90 + x * Integer.parseInt(binding.steerScaling.getText().toString())));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }*/

            bluetoothGatt.writeCharacteristic(servo, s.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            /*try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            bluetoothGatt.writeCharacteristic(motor,
                    df.format(intMotor * Integer.parseInt(binding.MotorScaling.getText().toString())).getBytes(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);*/
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
        container.removeAllViews();
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        inputManager = (InputManager) getActivity().getSystemService(Context.INPUT_SERVICE);

        int[] deviceIds = inputManager.getInputDeviceIds();

        for (int deviceId : deviceIds) {
            gamepad = inputManager.getInputDevice(deviceId);
            int sources = gamepad.getSources();

            if ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
                    (sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            }
        }


        this.device = getArguments().getParcelable("device", BluetoothDevice.class);
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        this.bluetoothGatt = this.device.connectGatt(getContext(), true, bluetoothGattCallback);
        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity a = (MainActivity) getActivity();
        a.getCurrentFragment();
        binding.buttonSecond.setOnClickListener(view1 -> {
            if(bluetoothGatt != null)
                bluetoothGatt.disconnect();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}