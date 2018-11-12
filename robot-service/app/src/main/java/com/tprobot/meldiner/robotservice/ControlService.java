package com.tprobot.meldiner.robotservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;

/**
 * Created by meldinr on 10/13/17.
 */

public class ControlService extends Service {
    private PubNubWrapper pubnub;
    private ArduinoWrapper arduino;
    private SocketServer wsServer;

    public ControlService(Context applicationContext) {
        super();
    }

    public ControlService() {
    }

    @Override
    public void onCreate() {

    }

    public String getIPAddress()
    {
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.getWifiState() == WIFI_STATE_ENABLED) {
            int ip = wifiManager.getConnectionInfo().getIpAddress();
            return Formatter.formatIpAddress(ip);
        }

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.arduino = new ArduinoWrapper(getApplicationContext());

        final String PUBNUB_SUB_KEY = getString(R.string.pubnub_sub_key);
        final String PUBNUB_PUB_KEY = getString(R.string.pubnub_pub_key);
        pubnub = new PubNubWrapper(PUBNUB_SUB_KEY, PUBNUB_PUB_KEY);
        pubnub.startListening(arduino);

        String ipAddress = getIPAddress();
        InetSocketAddress inetSockAddress = new InetSocketAddress(ipAddress, 38301);
        this.wsServer = new SocketServer(inetSockAddress, arduino);
        this.wsServer.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        pubnub.stopListening();
        Intent broadcastIntent = new Intent("com.tprobot.meldinr.robotservice.ControlService.Restart");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class ControlServiceStartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(ControlService.class.getSimpleName(), "Robot Control Service is starting...");
            Intent startServiceIntent = new Intent(context, ControlService.class);
            context.startService(startServiceIntent);
        }
    }

    public static class UsbDeviceAttachedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(ControlService.class.getSimpleName(), "USB device attached.");
            new ArduinoWrapper(context).requestPermission();
        }
    }

    public static class UsbDeviceDetachedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(ControlService.class.getSimpleName(), "USB device detached.");
        }
    }
}
