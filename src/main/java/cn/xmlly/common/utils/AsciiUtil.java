package cn.xmlly.common.utils;

import cn.xmlly.common.exception.RespException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * ASCII码工具类
 *
 * @author lixiaolin
 * @date 2020/7/20 17:03
 */
@Slf4j
public class AsciiUtil {


    /**
     * Map集合按照ASCII码从小到大（字典序）排序
     *
     * @param map
     * @return
     */
    public static String MapToAsciiString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            throw new RespException("请求参数不能为空!");
        }
        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>((Collection<? extends Map.Entry<String, String>>) map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            System.out.println(infoIds);
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    Object val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }
            }
            result = sb.toString();
            if (!StringUtils.isBlank(result)) {
                return result.substring(0, result.length() - 1);
            }
        } catch (Exception e) {
            log.error("MapToAsciiString error = {}", e.getMessage());
            return null;
        }
        return result;
    }


    public static void main(String[] args) {
        Map<String, String> map = new HashMap();
        map.put("appid", "wxd930ea5d5a258f4f");
        map.put("mch_id", "10000100");
        map.put("device_info", "1000");
        map.put("body", "test");
        map.put("nonce_str", "ibuaiVcKdpRxkhJA");
        String string = MapToAsciiString(map);
        System.out.println(string);
    }

}
