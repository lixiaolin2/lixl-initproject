package cn.xmlly.common.utils;

import cn.xmlly.common.exception.RespException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * 读取配置工具类
 *
 * @author: lixingjia
 * @version: 1.0
 * @date: 2018/8/24 9:10
 */
@Slf4j
@Configuration
@ConditionalOnMissingClass
public class YamlConfigurerUtil {

    @Autowired
    private Environment environment;

    public static Environment env;


    @PostConstruct
    public void init() {
        env = environment;
    }

    public static String getStrYmlVal(String key) {
        //yml没有配置对应的key返回null，如果有key没有值返回""
        String value = env.getProperty(key);

        if (StringUtils.isEmpty(value)) {
            throw new RespException("参数" + key + "为空，请检查yml配置文件");
        }
        return value;
    }

    public static Integer getIntegerYmlVal(String key) {
        String value = getStrYmlVal(key);
        if (StringUtils.isBlank(value)){
            throw new RespException("参数" + key + "为空，请检查yml配置文件");
        }
        return Integer.valueOf(value);
    }

    public static String getStrYmlVal(String key, String defaultVal) {
        return env.getProperty(key, defaultVal);
    }

    public static Long getLongYmlVal(String key) {
        String value = getStrYmlVal(key);
        if (StringUtils.isBlank(value)){
            throw new RespException("参数" + key + "为空，请检查yml配置文件");
        }
        return Long.valueOf(value);
    }

    public static Integer getIntegerYmlVal(String key, Integer defaultVal) {
        String value = env.getProperty(key);
        return StringUtils.isBlank(value) ? defaultVal : Integer.valueOf(value);
    }

    public static Boolean getBoolYmaVal(String key, String defaultVal) {

        return Boolean.valueOf(env.getProperty(key, defaultVal));
    }

    public static Boolean getBoolYmaVal(String key) {
        if (StringUtils.isEmpty(env.getProperty(key))) {
            log.error("请检查配置文件中是否存在{}配置项!", key);
            throw new RespException("请检查配置中是否存在" + key + "配置项！");
        }
        return Boolean.valueOf(env.getProperty(key));
    }

    /**
     * 获取当前服务器是否为私有云
     * @return
     */
    public static Boolean getIsPrivateServerFlag(){
        return YamlConfigurerUtil.getBoolYmaVal("isPrivate");
    }

    /**
     * 获取配置文件中是否配置了websocket模块
     * @return TRUE:FALSE，TRUE则往websocket推送消息
     */
    public static Boolean getOpenWebSocketModuleFlag(){
        return YamlConfigurerUtil.getBoolYmaVal("openWebsocketModules");
    }


}
