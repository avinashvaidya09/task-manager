package com.sap.cap.taskmanager.handlers;

import java.security.SecureRandom;

import com.sap.cap.taskmanager.util.TaskManagerUtil;
import com.sap.cds.services.cds.CqnService;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cds.gen.adminservice.User;
import cds.gen.adminservice.User_;

@Component
@ServiceName("AdminService")
public class UserService implements EventHandler{
    
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String PASSWORD_PREFIX = "Welcome";

    @Before(event = CqnService.EVENT_CREATE , entity = User_.CDS_NAME)
    public void onCreate(User userData) {

        String password = PASSWORD_PREFIX + String.valueOf(TaskManagerUtil.generateRandomNumber()) ;

        userData.setPassword(password);

        logger.info("Updated default password for {}", userData.getFirstName());
    }
}
