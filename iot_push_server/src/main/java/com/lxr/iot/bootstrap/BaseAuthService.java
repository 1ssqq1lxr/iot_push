package com.lxr.iot.bootstrap;

/**
 * 权限校验
 *
 * @author lxr
 * @create 2018-02-09 14:34
 **/
public interface BaseAuthService {

    boolean  authorized(String username,String password);

}
