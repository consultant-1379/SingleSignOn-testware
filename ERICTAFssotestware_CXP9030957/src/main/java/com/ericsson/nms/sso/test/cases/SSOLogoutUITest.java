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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.nms.sso.test.operators.SSOLoginUIOperator;
import com.ericsson.nms.sso.test.utils.SSOTAFConstants;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This class tests the various test cases that are possible on logout from
 * launcher and logviewer page.
 *
 */
public class SSOLogoutUITest extends SSOUITestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<SSOLoginUIOperator> ssoLoginProvider;

    private final Logger log = LoggerFactory.getLogger(SSOLogoutUITest.class);

    /**
     * @DESCRIPTION Verify that by logging out of one web OSS application that
     *              the user is logged out of all the applications that he/she
     *              was logged in to.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_1")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldLogoutOfAllAppsOnceLoggedoutInOneApp() {
        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Verify user is logged into Launcher");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());
        //open the log viewer
        operator.openLogViewer(true);
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());
        //log out here
        log.info("Logout of application");
        operator.logoutFromLogViewer();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
        //try to access the launcher page, it should redirect to login
        log.info("Refresh to see if logged in.");
        operator.switchToLauncher();
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     * @DESCRIPTION Verify that after logging out the additional web
     *              applications in other windows or tabs remain as is until.
     *              user interaction.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_4")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldStayInCurrentAppUntilUserInteractionWhenLoggedoutInOtherApp() {

        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Verify user is logged into Launcher");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());
        //open the log viewer
        operator.openLogViewer(true);
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());
        //log out here
        log.info("Logout of application");
        operator.logoutFromLogViewer();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
        //switch to launcher
        operator.switchToLauncher();
        //try to access the launcher page with refresh, it should redirect to login
        log.info("Refresh to see if logged in.");
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     * @DESCRIPTION Verify that by logging out of one web OSS application, for
     *              example in log viewer, then the user cannot go back to log
     *              viewer on clicking back button
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_5")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageOnBackButtonAfterLogout() {
        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        //logout from the launcher home page
        log.info("Logout of application");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
        //click back button
        operator.navigateBack();
        //refresh the page
        log.info("Refresh to see if logged in.");
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Verify that user is brought to Launcher Login Page.");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     * @DESCRIPTION Verify that If a user tries to navigate to Launcher Page
     *              e.g. Categories after logging out, that they will be brought
     *              to the login screen with an error message indicating:
     *              "Please log in in order to access this page".
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_6")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowLoginWhenLaunchingAfterLogout() {
        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        //logout from the launcher home page
        log.info("Logout of application");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

}
