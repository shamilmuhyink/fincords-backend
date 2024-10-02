package com.fincords.service;

import com.fincords.model.AppUser;
import com.fincords.requestDTO.UserRequest;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    public void createAppUser(UserRequest userRequest) {
        AppUser user = new AppUser();
    }
}
