package cn.xmlly.common.utils.disruptor.event;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class JsonEvent {
    private JSONObject jsonObject;
}