package cn.xmlly.common.utils.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;


/**
 * redis工具类
 *
 * @author : caigq
 * @version : 1.0
 * @date : 2017/5/9 9:51
 */
@Slf4j
@Component("redisClient")
public class RedisClient {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Environment environment;

    /**
     * 正则匹配获取key
     *
     * @param pattern 正则表达式
     * @return
     */
    public Set<String> keys(String pattern) {
        Boolean redisClusterFlag = StringUtils.isEmpty(environment.getProperty(pattern)) ? null : Boolean.valueOf(environment.getProperty(pattern));
        if (redisClusterFlag == null) {
            redisClusterFlag = false;
        }
        if (redisClusterFlag) {
            return keysCluster(pattern);
        } else {
            Set<String> set = new HashSet<>();
            for (Object object : redisTemplate.keys(pattern)) {
                if (object instanceof String) {
                    set.add((String) object);
                }
            }
            return set;
        }
    }

    public Set<String> keysCluster(String pattern) {
        Set<String> result = new HashSet<>();
        Set<String> nodesSet = getClusterNodes();
        if (nodesSet == null && nodesSet.size() == 0) {
            return result;
        }
        return (Set<String>) this.redisTemplate.execute((RedisConnection connection) -> {
            Iterator<String> iterator = nodesSet.iterator();
            while (iterator.hasNext()) {
                byte[] args1 = pattern.getBytes();
                byte[] args2 = iterator.next().getBytes();
                ArrayList object = (ArrayList) connection.execute("keys", args1, args2);
                Iterator<byte[]> it = object.iterator();
                while (it.hasNext()) {
                    String keyName = new String(it.next());
                    result.add(keyName);
                }
            }
            return result;
        });
    }

    public Set<String> getClusterNodes() {
        return (Set<String>) this.redisTemplate.execute((RedisConnection connection) -> {
            Set<String> nodes = new HashSet<>();
            byte[] args1 = "nodes".getBytes();
            byte[] object = (byte[]) connection.execute("cluster", args1);
            if (object == null) {
                return null;
            }
            String string = new String(object);
            if (org.apache.commons.lang3.StringUtils.isEmpty(string)) {
                return null;
            }
            String[] nodeInfoArray = string.split("\n");
            for (int i = 0; i < nodeInfoArray.length; i++) {
                //只获取master节点
                if (nodeInfoArray[i].contains("master")) {
                    String[] nodeInfo = nodeInfoArray[i].split(" ");
                    String nodeName = nodeInfo[0];
                    nodes.add(nodeName);
                }
            }
            return nodes;
        });
    }

    public Map<String, Map> getRecentContacts(Set<String> uins) {
        Boolean redisClusterFlag = true;
        if (redisClusterFlag) {
            Map<String, Map> result = new HashMap<>();
            Set<String> nodesSet = getClusterNodes();
            if (nodesSet == null && nodesSet.size() == 0) {
                return result;
            }
            return (Map<String, Map>) redisTemplate.execute((RedisConnection connection) -> {
                Map<String, Map> recentContacts = new HashMap<>();
                try {
                    for (String uin : uins) {
                        if (org.apache.commons.lang3.StringUtils.isEmpty(uin)) {
                            continue;
                        }
                        String redisKey = uin + "RedisKeyConstants";
                        //扫描messageRecord:*
                        Set<String> messageRecord = new HashSet<>();
                        Iterator<String> iterator = nodesSet.iterator();
                        while (iterator.hasNext()) {
                            byte[] args1 = redisKey.getBytes();
                            byte[] args2 = iterator.next().getBytes();
                            ArrayList object = (ArrayList) connection.execute("keys", args1, args2);
                            Iterator<byte[]> it = object.iterator();
                            while (it.hasNext()) {
                                redisTemplate.delete(it.next());
                            }
                        }

                        if (messageRecord == null && messageRecord.size() == 0) {
                            continue;
                        }
                        //如果有扫描结果则做处理
                        Map<String, Object> userNames = new HashMap<>();
                        //遍历每一个key
                        for (String element : messageRecord) {
                            if (org.apache.commons.lang3.StringUtils.isNotEmpty(element)) {
                                redisTemplate.opsForZSet().remove(element);
                                Set<String> messageSet = redisTemplate.opsForZSet().reverseRange(element, 0, 0);
                                String[] elements = element.split(":");
                                //数组中第四个元素为好友的UserName
                                String userName = elements[3];
                                String lastContent = "";
                                if (messageSet != null && messageSet.size() > 0) {
                                    lastContent = messageSet.iterator().next();
                                }

                                userNames.put(userName, JSONObject.parseObject(lastContent));
                            }
                        }
                        recentContacts.put(uin, userNames);
                    }
                    return recentContacts;
                } catch (Exception e) {
                    log.error("【[redisClient]获取最近联系人异常,异常原因={}】", e.getMessage());
                }
                return new HashMap<>();
            });
        } else {
            return (Map<String, Map>) redisTemplate.execute((RedisConnection connection) -> {
                Map<String, Map> recentContacts = new HashMap<>();
                Cursor<byte[]> cursor = null;

                try {

                    for (String uin : uins) {
                        if (org.apache.commons.lang3.StringUtils.isEmpty(uin)) {
                            continue;
                        }
                        String redisKey = uin + "RedisKeyConstants";
                        //扫描messageRecord:*
                        cursor = connection.scan(ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(redisKey).build());
                        if (cursor == null) {
                            continue;
                        }
                        //如果有扫描结果则做处理
                        Map<String, Object> userNames = new HashMap<>();
                        //遍历每一个key
                        while (cursor.hasNext()) {
                            String element = new String(cursor.next());
                            if (org.apache.commons.lang3.StringUtils.isNotEmpty(element)) {
                                Set<String> messageSet = redisTemplate.opsForZSet().reverseRange(element, 0, 0);
                                String[] elements = element.split(":");
                                //数组中第四个元素为好友的UserName
                                String userName = elements[3];
                                String lastContent = "";
                                if (messageSet != null && messageSet.size() > 0) {
                                    lastContent = messageSet.iterator().next();
                                }
                                userNames.put(userName, JSONObject.parseObject(lastContent));
                            }
                        }
                        recentContacts.put(uin, userNames);
                    }
                    return recentContacts;
                } catch (Exception e) {
                    log.error("【[redisClient]获取最近联系人异常,异常原因={}】", e.getMessage());
                }
                return new HashMap<>();
            });
        }
    }

    public Map hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    public JSONObject getWeChatUserByUin(String uin) {

        String key = "_wechatUser:" + uin;
        String wechatUserJson = (String) redisTemplate.opsForValue().get(key);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(wechatUserJson)) {
            return JSON.parseObject(wechatUserJson);
        }
        return null;
    }

    public JSONObject getWeChatContactByUinAndUserName(String uin, String weChatUsername, String userName) {
        //"2368771701:wechatContact:wxid_15s48dvw9f7s22";

        String key = uin + ":wechatContact:" + weChatUsername;
        String contactStr = (String) redisTemplate.opsForHash().get(key, userName);
        if (contactStr != null) {
            return JSON.parseObject(contactStr);
        }
        return null;
    }

    public Object hget(String key, String file) {
        return redisTemplate.opsForHash().get(key, file);
    }

    public void hset(String key, String file, Object content) {
        redisTemplate.opsForHash().put(key, file, content);
    }

    public void increment(String key, String file) {
        redisTemplate.opsForHash().increment(key, file, 1);
    }

    public void del(String key) {
        redisTemplate.delete(key);
    }
}
