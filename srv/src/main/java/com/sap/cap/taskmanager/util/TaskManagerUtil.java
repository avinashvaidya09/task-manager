package com.sap.cap.taskmanager.util;

import java.security.SecureRandom;

public class TaskManagerUtil {

    public static int generateRandomNumber() {
        SecureRandom random = new SecureRandom();
        int randomNumber = (int) random.nextInt(99999);
        return randomNumber;
    }
}
