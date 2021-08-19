package com.revature.johnKimAPI.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.revature.johnKimAPI.repositories.SchoolRepository;
import com.revature.johnKimAPI.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The beating heart of the application. So long as appRunning is true,
 * the application will continue trying to render. This is where the Graceful shutdown occurs.
 */

public class AppState {

    // This should be the only instantiation needed of a Buffered Reader, which will be injected to each Page.
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    // This is AppState, the constructor for this class.
    public AppState() {

        // make the app components (certain dependencies to get injected)
        SchoolRepository groupRepo = new SchoolRepository();
        ValidationService checker = new ValidationService(groupRepo);

    }

    Logger logger = LoggerFactory.getLogger(AppState.class);
    public void startApp() {

    }

    // the application shutdown method, safely (and effectively) closes the app.
    public static void closeApp() { }
}
