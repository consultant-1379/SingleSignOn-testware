/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.sso.test.utils;

import java.util.Map;
import java.util.Random;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.ui.Browser;
import com.ericsson.cifwk.taf.ui.BrowserSetup;
import com.ericsson.cifwk.taf.ui.BrowserType;
import com.ericsson.cifwk.taf.ui.UI;

/**
 * This class has all the re-usable methods which are used by various test
 * cases.
 *
 */
public class SSOUtils {

    private static final Logger log = LoggerFactory.getLogger(SSOUtils.class);
    private static BrowserType browserType;
    private static Host adminUserHost;
    private static Host launcherHost = HostConfigurator.getApache();
    private static String launcherLoginTitle = (String) DataHandler.getAttribute("launcher.login.title");
    private static String webUri = (String) DataHandler.getAttribute("launcher.web.uri");
    private static String noCredentialsError = (String) DataHandler.getAttribute("launcher.login.nocredentialserror");
    private static String noUserNameError = (String) DataHandler.getAttribute("launcher.login.nousernameerror");
    private static String noPasswordError = (String) DataHandler.getAttribute("launcher.login.nopassworderror");
    private static String launcherTitle = (String) DataHandler.getAttribute("launcher.launcher.title");
    private static String logViewerTitle = (String) DataHandler.getAttribute("launcher.logviewer.title");
    private static String loginUserName = (String) DataHandler.getAttribute("launcher.login.username");
    private static String loginPassword = (String) DataHandler.getAttribute("launcher.login.password");
    private static String loginError = (String) DataHandler.getAttribute("launcher.login.error");

    /**
     * get the ENM TOR launcher URL
     *
     * @param ports
     * @return
     */
    public static String getWebAppUrl(Object ports, String portType) {

        log.debug("getWebAppUrl: called");
        if (launcherHost == null) {
            log.error("getWebAppUrl: launcherHost is null.");
            throw new IllegalArgumentException("launcher not defined in host.properties file");
        }

        Map<Ports, String> portMap = launcherHost.getPort();
        log.debug("getWebAppUrl: uri is {}", webUri);

        String launcherPort = portMap.get(ports);
        if (launcherPort == null) {
            throw new IllegalArgumentException(portType + " port not defined for host 'launcher'");
        }

        log.debug("getWebAppUrl: launcherPort is {}", launcherPort);
        log.debug("getWebAppUrl: launcherHost.getIp() is {}", launcherHost.getIp());

        String url = String.format("https://%s:%s%s", launcherHost.getIp(), launcherPort, webUri);

        log.debug("getWebAppUrl: url is {}", url);
        return url;

    }

    public static String getWebAppUrlHttp() {
        return getWebAppUrl(Ports.HTTP, Ports.HTTP.name());
    }

    public static String getWebAppUrlHttps() {
        return getWebAppUrl(Ports.HTTPS, Ports.HTTPS.name());
    }

    /**
     * Generating a random string for a fake username/password
     * 
     * @return
     */
    public static String generateString() {
        Random rng = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    public static Host getAdminUserHost(Host host) {
        if (adminUserHost == null) {
            adminUserHost = new Host();
            adminUserHost.setIp(host.getIp());
            adminUserHost.setUser(host.getUser(UserType.ADMIN));
            adminUserHost.setPass(host.getPass(UserType.ADMIN));
        }
        return adminUserHost;
    }

    public static String getLauncherLoginTitle() {
        return launcherLoginTitle;
    }

    public static String getLauncherTitle() {
        return launcherTitle;
    }

    public static String getLogViewerTitle() {
        return logViewerTitle;
    }

    public static String getLoginUserName() {
        return loginUserName;
    }

    public static String getLoginPassword() {
        return loginPassword;
    }

    public static String getLoginErrorMesage() {
        return loginError;
    }

    public static Browser getBrowserInstance() {

        Browser browser = UI.newBrowser(BrowserType.FIREFOX);
        return browser;
    }

    public static String getNoLoginCredentialsErrorMesage() {
        return noCredentialsError;
    }

    public static String getNoUsernameErrorMesage() {
        return noUserNameError;
    }

    public static String getNoPasswordErrorMesage() {
        return noPasswordError;
    }
}
