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
 * This class tests the various test cases that are possible in one of the
 * applications, that is logviewer.
 *
 */
public class SSOLogViewerUITest extends SSOUITestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<SSOLoginUIOperator> ssoLoginProvider;

    private final Logger log = LoggerFactory.getLogger(SSOLogViewerUITest.class);

    /**
     * @DESCRIPTION Verify that when a user launches LogViewer with login then
     *              log viewer screen is presented and the user can interact
     *              with the application.
     * @PRIORITY HIGH
     * @PRE Get TOR Web Application URL from env.properties file
     */
    @TestId(id = "TORFTUISSO-60_Func_1")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldGoToLogViewerAccessingLogViewerWithLogin() {
        log.info("Visit the LogViewer web page after login from Tor Launcher URI in shouldGoToLogViewerAccessingLogViewerWithLogin()");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Open the log viewer application");
        operator.openLogViewer(true);
        log.info("Verify that LogViewer is launched");
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());
        log.info("Verify URL is using secure https protocol");
        assertTrue(operator.isHttps());
        operator.logoutFromLauncher();
        operator.confirmOK();
    }

    /**
     * @DESCRIPTION Verify that when the user logs out of Launcher that they
     *              will be brought to login screen the next time they interact
     *              with the LogViewer
     * @PRIORITY HIGH
     * @PRE Get TOR Web Application URL from env.properties file
     */
    @TestId(id = "TORFTUISSO-60_Func_2")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldRedirectToLoginInLogViewerOnLogoutInLauncher() {

        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Verify user is logged into Launcher");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());

        //open the log viewer
        log.info("Open the log viewer application");
        operator.openLogViewer(true);
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());

        //log out in launcher
        log.info("Logout of application");
        operator.switchToLauncher();
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLauncherViewModel().logoutLink, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Click logout");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());

        //try to access the log viewer page, it should redirect to login, switch to log viewer app
        operator.switchToOtherApp();
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Refresh to see if logged in.");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());

    }

    /**
     * @DESCRIPTION Verify that when the user logs out of or exits LogViewer
     *              that they are also logged out of Launcher the next time the
     *              user interacts with Launcher.
     * @PRIORITY HIGH
     * @PRE Get TOR Web Application URL from env.properties file
     */
    @TestId(id = "TORFTUISSO-60_Func_3")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldRedirectToLoginInLauncherOnLogoutInLogviewer() {

        log.info("Launch the application");
        //login first which opens the launcher
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Verify user is logged into Launcher");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());

        //open the log viewer
        log.info("Open the log viewer application");
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
        operator.switchToLauncher();
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Refresh to see if logged in.");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());

    }

    /**
     * @DESCRIPTION Verify that when a user logs out form the logviewer then
     *              login screen is presented
     * @PRIORITY HIGH
     * @PRE Get TOR Web Application URL from env.properties file
     */
    @TestId(id = "TORFTUISSO-60_Func_7")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldGoToLoginAfterLogoutFromLogViewer() {
        log.info("Visit the LogViewer web page after login from Tor Launcher URI and then click logout in LogViewer and then OK in shouldGoToLogViewerAccessingLogViewerWithLogin()");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Open the log viewer application");
        operator.openLogViewer(true);
        log.info("verify that the login is successful and the user is in log viewer page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());
        log.info("Click logout");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     * @DESCRIPTION Verify that when a user clicks on logout but then clicks
     *              cancel on the pop up, then the user should stay in the
     *              logviewer page
     * @PRIORITY HIGH
     * @PRE Get TOR Web Application URL from env.properties file
     */
    @TestId(id = "TORFTUISSO-60_Func_8")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldStayInLogviewerOnLogoutCancel() {
        log.info("Launch the application");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Launch the logviewer");
        operator.openLogViewer(true);
        log.info("verify that the login is successful, and log viewer is opened");
        assertEquals("Log Out", operator.getLogViewerViewModel().logoutLink.getText());
        log.info("Click logout");
        operator.logoutFromLauncher();
        operator.confirmCancel();
        operator.refreshCurrentBrowser();
        operator.browserTab
                .waitUntilComponentIsDisplayed(operator.getLogViewerViewModel().logViewerHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Verify user is logged into Logviewer");
        assertEquals(operator.getPageTitle(), SSOUtils.getLogViewerTitle());
        log.info("Click logout in launcher");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
    }
}
