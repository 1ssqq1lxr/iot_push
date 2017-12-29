package com.lxr.iot.exception;

/**
 * 连接异常
 *
 * @author lxr
 * @create 2017-11-23 14:34
 **/
public class ConnectionException extends  RuntimeException {

    public ConnectionException(String message) {
        super(message);
    }
}
