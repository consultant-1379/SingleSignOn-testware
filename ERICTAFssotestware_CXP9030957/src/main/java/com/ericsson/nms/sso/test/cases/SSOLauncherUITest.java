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
import com.ericsson.nms.sso.test.operators.SSOGenericOperator;
import com.ericsson.nms.sso.test.operators.SSOLoginUIOperator;
import com.ericsson.nms.sso.test.utils.SSOTAFConstants;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This class tests the use cases of logout from the launcher page.
 *
 */

public class SSOLauncherUITest extends SSOUITestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<SSOLoginUIOperator> ssoLoginProvider;

    private final Logger log = LoggerFactory.getLogger(SSOLauncherUITest.class);

    /**
     *
     * @DESCRIPTION Verify that the user is logged out and redirected to the
     *              login page after confirming the logout operation.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_3")
    @Test(groups = { "regression", "RFA" })
    @Context(context = { Context.UI })
    public void shouldGoToLoginOnLogoutOK() {
        log.info("Visit the Tor Launcher URI");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Click the logout button");
        operator.logoutFromLauncher();
        log.info("Confirm the logout action");
        operator.confirmOK();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLoginViewModel().loginHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Check if the page title is login page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherLoginTitle());
    }

    /**
     *
     * @DESCRIPTION Verify that the user stays logged in and on the Launcher
     *              page after cancelling the logout operation.
     * @PRE Get TOR Web Application URL from env.properties file
     * @PRIORITY HIGH
     */
    @TestId(id = "TORFTUISSO-176_Func_7")
    @Test(groups = { "regression" })
    @Context(context = { Context.UI })
    public void shouldStayInLauncherOnLogoutCancel() {
        log.info("Visit the Tor Launcher URI, login successfully then click logout, then cofirn the pop put with Cancel in shouldStayInLauncherOnLogoutCancel()");
        SSOLoginUIOperator operator = ssoLoginProvider.provide(SSOLoginUIOperator.class);
        log.info("Enter the username and password");
        operator.login(SSOUtils.getLoginUserName(), SSOUtils.getLoginPassword(), true);
        log.info("Click the logout button");
        operator.logoutFromLauncher();
        log.info("Cancel the logout action");
        operator.confirmCancel();
        operator.refreshCurrentBrowser();
        operator.browserTab.waitUntilComponentIsDisplayed(operator.getLauncherViewModel().launcherHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        log.info("Check if the page title is launcher page");
        assertEquals(operator.getPageTitle(), SSOUtils.getLauncherTitle());
        operator.logoutFromLauncher();
        operator.confirmOK();
    }

}
