package meldiner.tprobot;

import android.util.Log;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;

/**
 * Created by meldinr on 7/16/16.
 */
public class MessageStreamWrapper {
    private final String SUBSCRIBE_KEY = BuildConfig.SUBSCRIBE_KEY;
    private final String PUBLISH_KEY = BuildConfig.PUBLISH_KEY;
    private final String SECRET_KEY = BuildConfig.SECRET_KEY;
    private final String CHANNEL = "roomba";

    private PubNub pubnub = null;
    private IMessageCallback callback = null;

    public MessageStreamWrapper() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBLISH_KEY);
        pnConfiguration.setSecretKey(SECRET_KEY);

        pubnub = new PubNub(pnConfiguration);

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                Log.d("STREAM", "STATUS: " + status.getStatusCode());
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                Log.d("STREAM", "MESSAGE: " + message.getMessage().textValue());

                if (callback != null) {
                    callback.send(message.getMessage().textValue());
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                Log.d("STREAM", "PRESESNCE: " + presence.getEvent());
            }
        });

        pubnub.subscribe()
                .channels(Arrays.asList(CHANNEL)) // subscribe to channels
                .execute();
    }

    public void setListener(IMessageCallback callback) {
        this.callback = callback;
    }
}
