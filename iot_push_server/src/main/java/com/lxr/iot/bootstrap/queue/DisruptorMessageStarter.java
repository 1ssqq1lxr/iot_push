package com.lxr.iot.bootstrap.queue;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;


@Component
public class DisruptorMessageStarter implements  MessageStarter<MessageEvent> , DisposableBean {


    private  Disruptor<MessageEvent> disruptor = new Disruptor<>(MessageEvent::new,
            1024, DaemonThreadFactory.INSTANCE, ProducerType.MULTI,
            new BlockingWaitStrategy());

    private final EventHandler<MessageEvent> eventHandler;

    public DisruptorMessageStarter(EventHandler<MessageEvent> eventHandler) {
        this.eventHandler = eventHandler;
        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
    }


    @Override
    public RingBuffer<MessageEvent> getRingBuffer() {
        return disruptor.getRingBuffer();
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
    }


    @Override
    public void destroy() throws Exception {
        shutdown();
    }
}
