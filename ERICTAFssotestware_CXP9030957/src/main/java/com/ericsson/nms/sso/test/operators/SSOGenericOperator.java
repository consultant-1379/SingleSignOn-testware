/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.sso.test.operators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserTab;
import com.ericsson.cifwk.taf.ui.UI;
import com.ericsson.cifwk.taf.ui.sdk.MessageBox;
import com.ericsson.nms.sso.test.pages.LauncherViewModel;
import com.ericsson.nms.sso.test.pages.LogViewerViewModel;
import com.ericsson.nms.sso.test.pages.LoginViewModel;
import com.ericsson.nms.sso.test.utils.SSOTAFConstants;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This operator has all the basic operations required for all the test cases in
 * the SSO TAF UI test cases. All the other UI operators are supposed to extend
 * this class for providing the basic functionality.
 *
 */

public class SSOGenericOperator implements SsoOperator {

    public static Browser browser = null;
    public BrowserTab browserTab;

    private final Logger log = LoggerFactory.getLogger(SSOGenericOperator.class);

    public SSOGenericOperator() {
    }

    public SSOGenericOperator(Browser browser) {
        this.browser = browser;
    }

    /**
     * opens the default page of the TOR ENM application, that is the login
     * page.
     */
    public void openDefaultPage() {
        String url = SSOUtils.getWebAppUrlHttps();
        this.browserTab = browser.open(url);
    }

    public void openDefaultPageWithHttp() {
        String url = SSOUtils.getWebAppUrlHttp();
        this.browserTab = browser.open(url);
    }

    /**
     * login to the application using the username and password
     *
     * @param userName
     * @param password
     * @param wait
     */
    public void login(final String userName, final String password, boolean wait) {
        String url = SSOUtils.getWebAppUrlHttps();
        this.browserTab = browser.open(url);
        getLoginViewModel().getNoticeOkButton().click();
        this.setLogin(userName, password);
        this.submitLogin(wait);
    }

    private void setLogin(final String userName, final String userPassword) {
        LoginViewModel loginModel = getLoginViewModel();
        loginModel.setUserName(userName);
        loginModel.setUserPassword(userPassword);
    }

    private void submitLogin(boolean wait) {
        getLoginViewModel().clickSubmit();
        if (wait) {
            browserTab.waitUntilComponentIsDisplayed(getLauncherViewModel().launcherHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        }
    }

    /**
     * Get the error message message in the login page, that pops out during
     * authentication error
     * 
     * @return
     */
    public String getLoginErrorMessage() {
        return getLoginViewModel().getErrorMessage();
    }

    /**
     * confirm the pop up which comes out on click of logout.
     */
    public void confirmOK() {
        MessageBox message = browserTab.getMessageBox();
        message.clickOk();
        this.browserTab.waitUntilComponentIsDisplayed(this.getLoginViewModel().getNoticeOkButton(), SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        getLoginViewModel().getNoticeOkButton().click();
        browserTab.waitUntilComponentIsDisplayed(getLoginViewModel().loginSubmit, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
    }

    /**
     * cancel the pop up which comes out on click of logout.
     */
    public void confirmCancel() {
        browserTab.getMessageBox().clickCancel();
    }

    @Override
    public String getPageTitle() {
        String title = browser.getCurrentWindow().getTitle();
        log.debug("Current page title: {}", title);
        return title;
    }

    /**
     * refresh the current browser tab.
     */
    public void refreshCurrentBrowser() {
        browserTab.refreshPage();
    }

    /**
     * click back button of the browser.
     */
    public void navigateBack() {
        browserTab.back();
    }

    public boolean isHttps() {
        return browserTab.getCurrentUrl().contains(SSOTAFConstants.HTTPS);
    }

    /**
     * clean the browser, includes logging out of TOR launcher and closes all
     * the browser tabs and points the browser instance to null.
     *
     */
    public static void cleanBrowser() {

        if (null != browser && !browser.isClosed()) {
            browser.close();
        }
        browser = null;

    }

    /**
     * Switch the browser tab to the launcher
     */
    public void switchToLauncher() {

        List<BrowserTab> listAllBrowsers = browser.getAllOpenWindows();
        if (null != listAllBrowsers && listAllBrowsers.size() < 2) {
            log.warn("Did not find 2 browsers so don't know which one to switch to. Found: " + listAllBrowsers.size() + " browser");
            return;
        }
        for (BrowserTab tab : listAllBrowsers) {
            String tabTitle = tab.getCurrentUrl();
            if (tabTitle.contains(SSOTAFConstants.LAUNCHER)) {
                this.browserTab = tab;
                browser.switchWindow(tab);
            }
        }

    }

    /**
     * Switch the browser tab to an app other than the launcher
     */
    public void switchToOtherApp() {

        List<BrowserTab> listAllBrowsers = browser.getAllOpenWindows();
        if (null != listAllBrowsers && listAllBrowsers.size() < 2) {
            log.warn("Did not find 2 browsers so don't know which one to switch to. Found: " + listAllBrowsers.size() + " browser");
            return;
        }
        for (BrowserTab tab : listAllBrowsers) {
            String tabTitle = tab.getCurrentUrl();
            if (!tabTitle.contains(SSOTAFConstants.LAUNCHER)) {
                this.browserTab = tab;
                browser.switchWindow(tab);
            }
        }

    }

    public LoginViewModel getLoginViewModel() {
        return browserTab.getView(LoginViewModel.class);
    }

    public LauncherViewModel getLauncherViewModel() {
        return browserTab.getView(LauncherViewModel.class);
    }

}
