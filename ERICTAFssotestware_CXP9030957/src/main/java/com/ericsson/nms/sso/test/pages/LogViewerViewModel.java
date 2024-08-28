package com.ericsson.nms.sso.test.pages;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

/**
 * This class maps the elements of the log viewer page in the browser to TAF UI
 * components, which we could for interaction during the test cases execution.
 *
 */
public class LogViewerViewModel extends GenericViewModel {

    @UiComponentMapping(".eaContainer-LogoutButton-link")
    public Button logoutLink;

    @UiComponentMapping(".eaLogViewer")
    public UiComponent logViewerHolder;

    @UiComponentMapping(".eaLogViewer-wTable-table")
    public UiComponent table;

    public void clickLogout() {
        logoutLink.click();
    }

}
