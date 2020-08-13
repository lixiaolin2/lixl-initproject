package cn.xmlly.common.utils.disruptor.event;

import com.lmax.disruptor.EventFactory;

public class JsonEventFactory implements EventFactory<JsonEvent> {
    @Override
    public JsonEvent newInstance() {
        return new JsonEvent();
    }
}