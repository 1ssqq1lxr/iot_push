package com.lxr.iot;

import com.lxr.iot.bootstrap.BaseAuthService;
import org.springframework.stereotype.Service;

/**
 * @author lxr
 * @create 2018-02-09 14:58
 **/

@Service
public class DefaultAutoService implements BaseAuthService{
    @Override
    public boolean authorized(String username, String password) {
        return true;
    }
}
