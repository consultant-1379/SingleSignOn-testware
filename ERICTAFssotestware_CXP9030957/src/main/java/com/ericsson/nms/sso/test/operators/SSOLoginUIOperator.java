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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.UiOperator;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.nms.sso.test.pages.LauncherViewModel;
import com.ericsson.nms.sso.test.pages.LogViewerViewModel;
import com.ericsson.nms.sso.test.utils.SSOTAFConstants;
import com.ericsson.nms.sso.test.utils.SSOUtils;

/**
 * This class extends the basic SSOGenericOperator, and has additional
 * functionality for opening the launcher page.
 */
@Operator(context = Context.UI)
public class SSOLoginUIOperator extends SSOGenericOperator implements UiOperator {

    private final Logger log = LoggerFactory.getLogger(SSOLoginUIOperator.class);
    private static String INIT_URL = null;

    public SSOLoginUIOperator() {
        super();
    }

    /**
     * Open the launcher page, if wait is true then wait until the launcher page
     * is loaded or else return.
     * 
     * @param wait
     */
    public void openLauncher(boolean wait) {
        final String url = getLauncherUrl();
        browserTab = browser.open(url);
        if (wait) {
            log.info("waiting for the launcher page to load");
            browserTab.waitUntilComponentIsDisplayed(getLauncherViewModel().launcherHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        }
    }

    private String getLauncherUrl() {
        String url = SSOUtils.getWebAppUrlHttps();
        INIT_URL = url + SSOTAFConstants.HASH + SSOTAFConstants.LAUNCHER;
        return INIT_URL;
    }

    /**
     * Open the log viewer application, if the wait is true then wait for the
     * logviewer page to load or else no.
     *
     * @param wait
     */
    public void openLogViewer(boolean wait) {
        final String url = getLogViewerUrl();
        browserTab = browser.open(url);
        if (wait) {
            log.info("waiting for the log viewer to load");
            browserTab.waitUntilComponentIsDisplayed(getLogViewerViewModel().logViewerHolder, SSOTAFConstants.COMPONENT_DISPLAY_TIMEOUT);
        }
    }

    /**
     * get the log viewer URL
     *
     * @return
     */
    private String getLogViewerUrl() {
        String url = SSOUtils.getWebAppUrlHttps();
        INIT_URL = url + SSOTAFConstants.HASH + SSOTAFConstants.LOG_VIEWER;
        return INIT_URL;
    }

    /**
     * logout of the application
     */
    public void logoutFromLauncher() {
        this.getLauncherViewModel().clickLogout();
    }

    public void logoutFromLogViewer() {
        this.getLogViewerViewModel().clickLogout();
    }

    public LogViewerViewModel getLogViewerViewModel() {
        return browserTab.getView(LogViewerViewModel.class);
    }

}
