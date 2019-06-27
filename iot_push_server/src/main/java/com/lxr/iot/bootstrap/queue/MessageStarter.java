package com.lxr.iot.bootstrap.queue;

import com.lmax.disruptor.RingBuffer;

public interface MessageStarter<T> {

    RingBuffer<T> getRingBuffer();

    void shutdown();

}
