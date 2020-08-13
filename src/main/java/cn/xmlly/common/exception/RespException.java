package cn.xmlly.common.exception;


import cn.xmlly.common.enums.SystemEnum;

import java.text.MessageFormat;


/**
 * 异常信息返回包装
 * 
 * 功能说明:
 *
 * @version V1.0
 * @author qzq
 * @since  JDK1.6
 */
public class RespException extends RuntimeException{
	
	protected int code=400;
	
    public RespException(String message) {
        super(message);
        this.code= SystemEnum.FAIL.getCode();
    }
    public RespException(int code, String message, Object ...  arguments) {
    	super(MessageFormat.format(message,arguments));
    	this.code=code;
    }
    public RespException(String message, Object ...  arguments) {
    	super(MessageFormat.format(message,arguments));
    }

    public RespException(int code, String message) {
    	super(message);
    	this.code=code;
    }
    public RespException(SystemEnum codesEnum) {
    	super(codesEnum.getMsg());
    	this.code=codesEnum.getCode();
    }

    /**
     * 格式化返回信息
     * @param codesEnum
     * @param arguments
     */
    public RespException(SystemEnum codesEnum, Object ...  arguments) {
    	super(MessageFormat.format(codesEnum.getMsg(),arguments));
    	this.code=codesEnum.getCode();
    }
    public int getCode(){
    	return this.code;
    }
    @Override
    public String getMessage() {
    	return super.getMessage();
    }
    
 
}
