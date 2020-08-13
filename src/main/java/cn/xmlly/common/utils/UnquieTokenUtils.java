package cn.xmlly.common.utils;

import cn.xmlly.common.exception.RespException;
import cn.xmlly.common.utils.redis.utils.LockUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 防止表单重复提交用的
 *
 * @author dell
 */
@Slf4j
public class UnquieTokenUtils {

    public static String tokenName = "unquieToken";

    /**
     * 唯一值，用来防重复
     *
     * @param unquieKey
     */
    public static  void checkToken(String unquieKey) {
        checkToken(unquieKey, 5, "操作太频繁！请稍后再试！");
    }

    /**
     * 唯一值，用来防重复
     *
     * @param unquieKey
     */
    public static  void checkToken(String unquieKey, int outTime) {
        checkToken(unquieKey, outTime, "操作太频繁！请稍后再试！");
    }

    /**
     * 唯一值，用来防重复
     *
     * @param unquieKey
     */
    public static  void checkToken(String unquieKey, int outTime, String msg) {
        String url = ControllerUtils.getRequest().getRequestURI();
        String key = tokenName + ":" + Md5Utils.getMd5(url + ":" + unquieKey);
        log.info("【重复提交key={}】", key);
        try {
            if(!LockUtil.tryLock(url,1,outTime, TimeUnit.SECONDS)){
                throw new RespException(msg);
            }
        } catch (RespException e) {
            throw new RespException(msg);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
