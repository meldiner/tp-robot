package com.tprobot.meldiner.robotservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by meldinr on 10/13/17.
 */

public class ControlService extends Service {
    private PubNubWrapper pubnub;
    private ArduinoWrapper arduino;

    public ControlService(Context applicationContext) {
        super();
    }

    public ControlService() {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        final String PUBNUB_SUB_KEY = getString(R.string.pubnub_sub_key);
        final String PUBNUB_PUB_KEY = getString(R.string.pubnub_pub_key);

        arduino = new ArduinoWrapper(getApplicationContext());
        pubnub = new PubNubWrapper(PUBNUB_SUB_KEY, PUBNUB_PUB_KEY);
        pubnub.startListening(arduino);

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
