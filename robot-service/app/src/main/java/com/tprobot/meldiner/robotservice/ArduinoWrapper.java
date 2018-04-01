package com.tprobot.meldiner.robotservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by meldinr on 10/17/17.
 */

public class ArduinoWrapper implements IMessageHandler{
    private final int ARDUINO_VENDOR_ID = 0x2341;
    private static final String USB_PERMISSION_ACTION = "com.tprobot.meldiner.robotservice.USB_PERMISSION";

    private Context context;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
//    private UsbSerialInterface.UsbReadCallback readCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
//        @Override
//        public void onReceivedData(byte[] arg0) {
//        String data = null;
//        try {
//            data = new String(arg0, "UTF-8");
//            Log.d("SERIAL", data);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        }
//    };
    private UsbDevice findArduino() {
        UsbManager usbManager = (UsbManager)context.getSystemService(context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        UsbDevice device = null;

        while (deviceIterator.hasNext()) {
            UsbDevice currentDevice = deviceIterator.next();

            if (currentDevice.getVendorId() == ARDUINO_VENDOR_ID) {
                device = currentDevice;
                break;
            }
        }

        return device;
    }

    public ArduinoWrapper(Context context) {
        this.context = context;
    }

    public void requestPermission() {
        UsbManager usbManager = (UsbManager)context.getSystemService(context.USB_SERVICE);

        if (device == null) {
            device = findArduino();
        }

        if (device == null) {
            Log.d("ARDUINO", "Couldn't find Arduino");
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(USB_PERMISSION_ACTION), 0);
        usbManager.requestPermission(device, permissionIntent);
    }

    private boolean connect() {
        UsbManager usbManager = (UsbManager)context.getSystemService(context.USB_SERVICE);

        if (device == null) {
            device = findArduino();
        }

        if (device == null) {
            Log.d("ARDUINO", "Couldn't find Arduino");
            return false;
        }

        UsbDeviceConnection connection = usbManager.openDevice(device);;
        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
                serialPort.setBaudRate(115200);
                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                //serialPort.read(readCallback);
                Log.d("SERIAL", "Serial Connection Opened!");

            } else {
                Log.d("SERIAL", "PORT NOT OPEN");
            }
        } else {
            Log.d("SERIAL", "PORT IS NULL");
        }

        return (serialPort != null);
    }

    @Override
    public void handle(String msg) {
        if (serialPort == null) {
            connect();
        }

        if (serialPort == null) {
            Log.d("ARDUINO", "Couldn't connext to Arduino");
            return;
        }

        serialPort.write(msg.getBytes());
    }
}
