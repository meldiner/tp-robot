package com.tprobot.meldiner.robotservice;

import android.content.Context;
import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

/**
 * Created by meldinr on 10/14/17.
 */

public class PubNubWrapper {
    private final String ROBOT_CONTROL_CHANNEL = "robotControl";
    private final String ACK_CHANNEL = "acks";

    private PubNub pubnub;

    private class Listener extends SubscribeCallback {
        private IMessageHandler handler;

        public Listener(IMessageHandler handler) {
            this.handler = handler;
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                // This event happens when radio / connectivity is lost
            }
            else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                // Connect event. You can do stuff like publish, and know you'll get it.
                // Or just use the connected event to confirm you are subscribed for
                // UI / internal notifications, etc
                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
                    sendAck("Robot Control Service Connected");
                }
            }
            else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                // Happens as part of our regular operation. This event happens when
                // radio / connectivity is lost, then regained.
            }
            else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                // Handle messsage decryption error. Probably client configured to
                // encrypt messages and on live data feed it received plain text.
            }
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {

            if (handler != null
                && message != null
                && message.getMessage() != null) {
                String msg = message.getMessage().toString().replace("\"", "");
                handler.handle(msg);
                Log.i("MESSAGE", msg);
            }
            else {
                Log.i("MESSAGE", "Couldn't parse message");
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }
    }
    private Listener currentListener = null;

    public PubNubWrapper(String subKey, String pubKey){
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(subKey);
        pnConfiguration.setPublishKey(pubKey);

        pubnub = new PubNub(pnConfiguration);

        Log.i("INIT", "Robot Contol Service initialized");
    }

    public void startListening(IMessageHandler handler) {
        currentListener = new Listener(handler);
        pubnub.addListener(currentListener);
        pubnub.subscribe().channels(Arrays.asList(ROBOT_CONTROL_CHANNEL)).execute();
    }

    public void stopListening() {
        pubnub.removeListener(currentListener);
        currentListener = null;
    }

    public void sendAck(String message) {
        pubnub.publish().channel(ACK_CHANNEL).message(message).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {
                    // Message successfully published to specified channel.
                }
                // Request processing failed.
                else {
                    // Handle message publish error. Check 'category' property to find out possible issue
                    // because of which request did fail.
                    //
                    // Request can be resent using: [status retry];
                }
            }
        });
    }
}
