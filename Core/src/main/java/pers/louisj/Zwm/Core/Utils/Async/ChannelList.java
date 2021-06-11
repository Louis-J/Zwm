package pers.louisj.Zwm.Core.Utils.Async;

import java.util.ArrayList;

public class ChannelList<T> extends ArrayList<Channel<T>> {
    public void put(T message) {
        for (var e : this)
            e.put(message);
    }
}