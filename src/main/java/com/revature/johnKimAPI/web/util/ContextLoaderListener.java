package com.revature.johnKimAPI.web.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.revature.johnKimAPI.repositories.SchoolRepository;
import com.revature.johnKimAPI.service.ValidationService;
import com.revature.johnKimAPI.util.GetMongoClient;

import com.revature.johnKimAPI.web.filters.AuthFilter;
import com.revature.johnKimAPI.web.servlet.*;
import com.revature.johnKimAPI.web.util.security.JwtConfig;
import com.revature.johnKimAPI.web.util.security.TokenGenerator;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.EnumSet;

public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        MongoClient mongoClient = GetMongoClient.generate().getConnection();
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        JwtConfig jwtConfig = new JwtConfig();
        TokenGenerator generator = new TokenGenerator(jwtConfig);
        SchoolRepository userRepo = new SchoolRepository(mongoClient);
        ValidationService userService = new ValidationService(userRepo);

        // TODO: Add all of your servlets to here!
        FacServlet facServlet = new FacServlet(userService, mapper, generator);
        HealthCheckServlet health = new HealthCheckServlet();
        AuthServlet authServlet = new AuthServlet(userService, mapper, generator);
        CourseServlet courseServlet = new CourseServlet(userService, mapper);
        RegistrationServlet registrationServlet = new RegistrationServlet(userService, mapper);
        EnrollServlet enrollServlet = new EnrollServlet(userService, mapper);

        AuthFilter authFilter = new AuthFilter(jwtConfig);

        ServletContext servletContext = sce.getServletContext();
        servletContext.addFilter("AuthFilter", authFilter).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        servletContext.addServlet("FacServlet", facServlet).addMapping("/facLogin/*");
        servletContext.addServlet("HealthCheckServlet", health).addMapping("/health/*");
        servletContext.addServlet("AuthServlet", authServlet).addMapping("/auth/*");
        servletContext.addServlet("CourseServlet", courseServlet).addMapping("/course/*");
        servletContext.addServlet("RegistrationServlet", registrationServlet).addMapping("/register/*");
        servletContext.addServlet("EnrollServlet", enrollServlet).addMapping("/enroll/*");

        configureLogback(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        GetMongoClient.generate().cleanUp();
    }

    private void configureLogback(ServletContext servletContext) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator logbackConfig = new JoranConfigurator();
        logbackConfig.setContext(loggerContext);
        loggerContext.reset();

        String logbackConfigFilePath = servletContext.getRealPath("") + File.separator + servletContext.getInitParameter("logback-config");

        try {
            logbackConfig.doConfigure(logbackConfigFilePath);
        } catch (JoranException e) {
            e.printStackTrace();
            System.out.println("An unexpected exception occurred. Unable to configure Logback.");
        }

    }

}