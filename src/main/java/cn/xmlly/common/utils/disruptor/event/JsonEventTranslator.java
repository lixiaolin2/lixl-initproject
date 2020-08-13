package cn.xmlly.common.utils.disruptor.event;

import com.alibaba.fastjson.JSONObject;
import com.lmax.disruptor.EventTranslatorOneArg;

public class JsonEventTranslator implements EventTranslatorOneArg<JsonEvent, JSONObject> {
    @Override
    public void translateTo(JsonEvent event, long sequence, JSONObject arg0) {
        event.setJsonObject(arg0);
    }
}