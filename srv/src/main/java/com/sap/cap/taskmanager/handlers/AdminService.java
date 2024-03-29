package com.sap.cap.taskmanager.handlers;

import java.util.Objects;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.sap.cap.taskmanager.util.TaskManagerUtil;
import com.sap.cds.services.cds.CqnService;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.messaging.MessagingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import cds.gen.adminservice.User;
import cds.gen.adminservice.User_;

@Component
@ServiceName("AdminService")
public class AdminService implements EventHandler {
    
    Logger logger = LoggerFactory.getLogger(AdminService.class);

    private static final String WELCOME_STRING = "Welcome! Start to manager your tasks efficiently";

    private static final String PASSWORD_PREFIX = "capm";

    @Autowired
    @Qualifier("taskmanager-events")
    MessagingService messagingService;
    

    @Before(event = CqnService.EVENT_CREATE , entity = User_.CDS_NAME)
    public void beforeCreate(User userData) {

        String otp = PASSWORD_PREFIX + String.valueOf(TaskManagerUtil.generateRandomNumber()) ;

        userData.setOtp(otp);

        logger.info("Generated default otp for {}", userData.getFirstName());

    }

    @After(event = CqnService.EVENT_CREATE , entity = User_.CDS_NAME)
    public void afterCreate(User userData) {
        
        JSONObject payload = new JSONObject();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("message", WELCOME_STRING);
        jsonObject.put("fromPhone", "+15165186002");
        if(Objects.nonNull(userData.getPhone()) && userData.getPhone().startsWith("+1")){
            jsonObject.put("toPhone",userData.getPhone());
        } else {
            jsonObject.put("toPhone","+1" + userData.getPhone());
        }
        

        payload.put("data", jsonObject);

        logger.info("Sending message to the topic in SAP Event Mesh");

        messagingService.emit("sap/taskmanager-events/event-mesh/user-registration-topic", payload);

    }
    
}
