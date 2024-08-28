/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.sso.test.pages;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.*;

/**
 * This class maps the elements of the Login page in the browser to TAF UI
 * components, which we could for interaction during the test cases execution.
 *
 */
public class LoginViewModel extends GenericViewModel {

    @UiComponentMapping(id = "loginNoticeOk")
    private Button noticeOkButton;

    @UiComponentMapping(".torLogin-Holder-loginUsername")
    public TextBox usernameInputBox;

    @UiComponentMapping(".torLogin-Holder-loginPassword")
    public TextBox passwordInputBox;

    @UiComponentMapping(".torLogin-Holder-formButton")
    public Button loginSubmit;

    @UiComponentMapping(".torLogin-Holder-messagesBox")
    public Label errorMessage;

    @UiComponentMapping(".torLogin-Holder")
    public UiComponent loginHolder;

    public Button getNoticeOkButton() {
        return noticeOkButton;
    }

    public void setUserName(final String userName) {
        usernameInputBox.setText(userName);
    }

    public void setUserPassword(final String userPassword) {
        passwordInputBox.setText(userPassword);
    }

    public void clickSubmit() {
        loginSubmit.click();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }
}
