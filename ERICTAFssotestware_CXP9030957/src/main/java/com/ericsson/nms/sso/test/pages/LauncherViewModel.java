package com.ericsson.nms.sso.test.pages;

import com.ericsson.cifwk.taf.ui.core.*;
import com.ericsson.cifwk.taf.ui.sdk.Button;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;

/**
 * This class maps the elements of the Launcher page in the browser to TAF UI
 * components, which we could for interaction during the test cases execution.
 *
 */
public class LauncherViewModel extends GenericViewModel {

    @UiComponentMapping(".eaContainer-LogoutButton-link")
    public Button logoutLink;

    @UiComponentMapping(selectorType = SelectorType.XPATH, selector = "/html/body/div[2]/div/div[2]/div[3]/div/div[2]/div/div[2]/div[2]/div[2]/div/a[3]")
    public Button logViewerLink;

    @UiComponentMapping(".eaLauncher")
    public UiComponent launcherHolder;

    public void clickLogout() {
        logoutLink.click();
    }

    public void clickLogViewer() {
        logViewerLink.click();
    }

}
