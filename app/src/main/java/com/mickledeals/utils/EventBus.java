package com.mickledeals.utils;

import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nicky on 10/27/2015.
 */
public class EventBus {
    public static final int EVENT_ADD_SAVE = 1;
    public static final int EVENT_REMOVE_SAVE = 2;
    public static final int EVENT_REDEEM = 3;
    public static final int EVENT_PURCHASE = 4;

    public interface EventListener {
        void onEventUpdate(int event, Bundle data);
    }

    private static EventBus sInstance;
    private Set<EventListener> mListeners = new HashSet<EventListener>();

    public static EventBus getInstance() {
        if (sInstance == null) {
            sInstance = new EventBus();
        }
        return sInstance;
    }

    public void registerListener(EventListener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        mListeners.remove(listener);
    }

    public void sendEvent(int event, Bundle data) {
        for (EventListener listener : mListeners) {
            listener.onEventUpdate(event, data);
        }
    }

}
