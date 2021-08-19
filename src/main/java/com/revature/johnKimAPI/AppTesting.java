package com.revature.johnKimAPI;

import com.revature.johnKimAPI.util.AppState;

/**
 * The purpose of this application is to allow users to create and log in with student accounts, where they may
 * enroll in courses, view open courses, or drop courses they no longer wish to attend. Faculty members (input
 * through the database via JSON by an Administrator) are capable of creating new courses, updating an old course
 * and deleting courses they no longer wish to teach. They can view their own courses, but cannot alter or delete
 * the courses created by other mentors.
 */

public class AppTesting {

    public static void main(String[] args) {
        AppState app = new AppState();
        app.startApp();
    }
}