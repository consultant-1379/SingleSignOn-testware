/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.sso.test.cases;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.OpenIDMOperatorImpl;
import com.ericsson.nms.sso.test.operators.SSOGenericOperator;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This class provides the test case setup required for all the other test case
 * classes, if a new UI test case is added, then it should extend this class for
 * providing the initial set up and points the browser to null after the test
 * case.
 *
 */
public class SSOUITestCaseHelper extends TorTestCaseHelper {

    @Inject
    OpenIDMOperatorImpl openIDMOperator;
    private final ENMUser ssoTestUser = new ENMUser();
    private static final Logger log = LoggerFactory.getLogger(SSOUITestCaseHelper.class);

    @BeforeSuite
    public void createENMUser() {
        try {
            //This user is referenced by launcher.login.username
            //which is defined in src/main/resources/taf_properties/env.properties file
            ssoTestUser.setUsername(SSOUtils.getLoginUserName());
            ssoTestUser.setFirstName("john");
            ssoTestUser.setLastName("Doe");
            ssoTestUser.setEmail("johndoe@ericsson.com");
            ssoTestUser.setEnabled(true);
            ssoTestUser.setPassword(SSOUtils.getLoginPassword());

            log.info("connecting OpenIDM...");
            openIDMOperator.connect("administrator", "TestPassw0rd");
            log.info("creating a user " + SSOUtils.getLoginUserName());
            openIDMOperator.createUser(ssoTestUser);
            log.info("assigning a user to ADMINISTRATOR role");
            openIDMOperator.assignUsersToRole("ADMINISTRATOR", ssoTestUser.getUsername());
            List<String> requestLog = openIDMOperator.getRequestLog();
            for (String logEntry : requestLog) {
                log.info("Result from OpenIDMOperator: " + logEntry);
            }
        } catch (final Exception e) {
            log.error("failed to create user {} with error: {}", SSOUtils.getLoginUserName(), e.getMessage(), e);
        }
    }

    @BeforeMethod
    public void beforeTest() {
        Browser browser = SSOUtils.getBrowserInstance();
        new SSOGenericOperator(browser);
    }

    @AfterMethod
    public void afterTest() {
        SSOGenericOperator.cleanBrowser();
    }

    @AfterSuite
    public void deleteENMUser() {
        try {
            openIDMOperator.deleteUser(ssoTestUser.getUsername());
        } catch (final Exception e) {
            log.error("failed to clean up user {} with error: {} ", SSOUtils.getLoginUserName(), e.getMessage());
        }
    }

}
