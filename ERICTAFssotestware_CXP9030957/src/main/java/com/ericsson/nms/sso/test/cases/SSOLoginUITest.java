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
import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.ui.core.UiComponentPredicates;
import com.ericsson.nms.sso.test.operators.SSOLoginUIOperator;
import com.ericsson.nms.sso.test.utils.SSOTAFConstants;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This class tests the various test cases that are possible from login page.
 *
 */
public class SSOLoginUITest extends SSOUITestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<SSOLoginUIOperator> ssoLoginProvider;

    private final Logger log = LoggerFactory.getLogger(SSOLoginUITest.class);

    /**
     *
     * @DESCRIPTION Verify that the user is brought to the login screen when
     *              they attempt to login to any OSS web application when not
     *              logged in.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_1")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldGoToLoginPageIfNotLoggedInWhenLogViewerLaunched() {
        log.info("Visit the Tor Log Viewer URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        operator.openLogViewer(false);
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     *
     * @DESCRIPTION Verify that the user is brought to the login screen when
     *              they attempt to use the Launcher when not logged in.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_2")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldGoToLoginPageIfNotLoggedInWhenLauncherPageLaunched() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        operator.openLauncher(false);
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are brought to the login page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     * @DESCRIPTION Verify that the user is brought to the login screen when
     *              they attempt to launch the application without login.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_5")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldGoToLoginPageIfNotLoggedInWhenApplicationLaunched() {
        log.info("Launch the application");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        operator.openDefaultPage();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are brought to the login page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     *
     * @DESCRIPTION Verify that the user can login to Launcher using valid
     *              username and password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_6")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    @TestStep(id = "user_login")
    public void shouldGotoLauncherWithValidCredentails() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Confirm we are brought to the launcher page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login to Launcher using invalid
     *              username and password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_7")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithInvalidUsernameAndPassword() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.generateString(), SSOUtils.generateString(), false);
        log.info("Confirm we are shown with a login error message");
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        assertEquals(SSOUtils.getLoginErrorMesage(), operator.getLoginErrorMessage());
    }

    /**
     * @DESCRIPTION Verify that the user cannot login to Launcher using invalid
     *              username and valid password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_8")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithInvalidUsername() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.generateString(), SSOUtils.getLoginPassword(), false);
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are shown with a login error message");
        assertNotNull(operator.getLoginErrorMessage());
        assertEquals(SSOUtils.getLoginErrorMesage(), operator.getLoginErrorMessage());
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login to Launcher using valid
     *              username and invalid password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_9")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithInvalidPassword() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter username and password");
        operator.login(SSOUtils.getLoginPassword(), SSOUtils.generateString(), false);
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are shown with a login error message");
        assertEquals(SSOUtils.getLoginErrorMesage(), operator.getLoginErrorMessage());
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login to Launcher using blank
     *              username and password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_10")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithEmptyCredentials() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter empty username and password");
        operator.login("", "", false);
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are brought to the login page with no credentials error message");
        assertEquals(operator.getLoginErrorMessage(), SSOUtils.getNoLoginCredentialsErrorMesage());
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login to Launcher using blank
     *              username and valid password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_11")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithEmptyUsernameAndValidPassword() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter empty username and password");
        operator.login("", SSOUtils.getLoginPassword(), false);
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are brought to the login page with no username error message");
        assertEquals(operator.getLoginErrorMessage(), SSOUtils.getNoUsernameErrorMesage());
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login to Launcher using valid
     *              username and blank password.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Func_12")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldShowErrorMessageWithValidUsernameAndEmptyPassword() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter empty username and password");
        operator.login(SSOUtils.getLoginUserName(), "", false);
        operator.browserTab.waitUntil(operator.getLoginViewModel().errorMessage, UiComponentPredicates.HAS_TEXT,
                SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Confirm we are brought to the login page with no password error message");
        assertEquals(operator.getLoginErrorMessage(), SSOUtils.getNoPasswordErrorMesage());
    }

    /**
     *
     * @DESCRIPTION Verify that the user cannot login using http
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-174_Secu_1")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldNotBeLoggedInUsingHttp() {
        log.info("Launch the application");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("open the default page using http");
        operator.openDefaultPageWithHttp();
        log.info("url: " + operator.browserTab.getCurrentUrl() + " title: " + operator.browserTab.getTitle());
        String titlePage = operator.browserTab.getTitle();
        // Different message thrown by Firefox and Chrome when a page cannot be accessed
        // URL in the message is in lowercase but it might erroneously contain uppercase characters in SSOUtils
        assertEquals(SSOTAFConstants.HTTP_PORT_ERROR_PAGE_FIREFOX, titlePage);
        //assertEquals(titlePage, SSOUtils.getWebAppUrlHttp().toLowerCase() + SSOTAFConstants.HTTP_PORT_ERROR_PAGE_CHROME);
    }

}
