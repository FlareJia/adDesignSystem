package com.wlarein.ad.service;

import com.wlarein.ad.exception.AdException;
import com.wlarein.ad.vo.CreateUserRequest;
import com.wlarein.ad.vo.CreateUserResponse;

public interface IUserService {

    /**
     * <h2>创建用户</h2>
     * */
    CreateUserResponse createUser(CreateUserRequest request)
            throws AdException;
}