package com.ericsson.nms.sso.performance.test.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.launcher.LauncherOperator;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.OpenIDMOperatorImpl;
import com.ericsson.nms.sso.performance.test.utils.SsoPerformanceTestConstants;
import com.google.inject.Inject;

public class ScenarioTestSteps extends TorTestCaseHelper implements TestCase {

    private Logger logger = Logger.getLogger(ScenarioTestSteps.class);

    private static AtomicInteger indexOfUserToLogIn = new AtomicInteger(0);
    private static AtomicInteger indexOfUserToBeAssign = new AtomicInteger(0);

    private static List<ENMUser> listOfCreatedUsers = new ArrayList<ENMUser>();
    private static List<ENMUser> listOfLoggedInUsers = new ArrayList<ENMUser>();
    
    private static Map<ENMUser, HttpTool> mapOfLoggedInUsers = new HashMap<ENMUser, HttpTool>();

    private static int userIndex = 0;

    @Inject
    OpenIDMOperatorImpl openIDMOperator;

    @Inject
    LauncherOperator launcherOperator;

    @TestStep(id = "initConnection")
    public void initConnection() {
        openIDMOperator.connect(SsoPerformanceTestConstants.IDM_CONNECTION_USERNAME, SsoPerformanceTestConstants.IDM_CONNECTION_PASSWORD);
    }

    @TestStep(id = "createUsers")
    public void createUsers(@Input("numberOfUsersPerThread") final int numberOfUsersPerThread) {
        for(int i = 0; i < numberOfUsersPerThread; i++) {
            ENMUser user = createEnmUser();
            listOfCreatedUsers.add(user);
            openIDMOperator.createUser(user);
            logger.info("Created user: " + user.getUsername());
        }
    }

    @TestStep(id = "loginUsers")
    public void loginUsers(@Input("numberOfUsersPerThread") final int numberOfUsersPerThread) {
        ThreadLocal<ENMUser> userToLogIn = new ThreadLocal<ENMUser>();
        for (int i = 0; i < numberOfUsersPerThread; i++) {
            synchronized (listOfLoggedInUsers) {
                userToLogIn.set(listOfCreatedUsers.get(indexOfUserToLogIn.getAndIncrement()));
            }
            login(userToLogIn.get());
        }
    }

    @TestStep(id = "logoutUsers")
    public void logoutUsers(@Input("numberOfUsersPerThread") final int numberOfUsersPerThread) {
        ThreadLocal<ENMUser> userToLogOut = new ThreadLocal<ENMUser>();
    	for (int i = 0; i < numberOfUsersPerThread; i++) {
            synchronized (listOfLoggedInUsers) {
                userToLogOut.set(listOfLoggedInUsers.get(listOfLoggedInUsers.size() - 1));
                listOfLoggedInUsers.remove(listOfLoggedInUsers.size() - 1);
                indexOfUserToLogIn.getAndDecrement();
            }
            logoutUser(userToLogOut.get());
        }
    }

    @TestStep(id = "deleteAllUsers")
    public void deleteAllUsers(@Input("numberOfUsersPerThread") final int numberOfUsersPerThread) {
        String userName;
        for (int i = 0; i < numberOfUsersPerThread; i++) {
            synchronized (listOfCreatedUsers) {
                userName = listOfCreatedUsers.remove(listOfCreatedUsers.size() - 1).getUsername();
            }
            openIDMOperator.deleteUser(userName);
            logger.info("Deleted user: " + userName);
        }
    }

    @TestStep(id = "assignUsersToRole")
    public void assignUsersToRole(@Input("numberOfUsersPerThread") final int numberOfUsersPerThread) {
    	ThreadLocal<ENMUser> userToBeAssigned = new ThreadLocal<ENMUser>();
        for (int i = 0; i < numberOfUsersPerThread; i++) {
            synchronized (listOfCreatedUsers) {
                userToBeAssigned.set(listOfCreatedUsers.get(indexOfUserToBeAssign.getAndIncrement()));
            }
            openIDMOperator.assignUsersToRole("OPERATOR", userToBeAssigned.get().getUsername());
            logger.info("Assigning user " + userToBeAssigned.get().getUsername() + " to ENM Operator role.");
        }
    }

    private void login(ENMUser user) {
        Host host = HostConfigurator.getApache();

        HttpTool httpTool = HttpToolBuilder.newBuilder(host)
                .useHttpsIfProvided(true).trustSslCertificates(true)
                .followRedirect(false).build();

        HttpResponse response = httpTool.request()
                .body("IDToken1", user.getUsername())
                .body("IDToken2", user.getPassword())
                .post("/login");

        String authErrorCode = response.getHeaders().get("X-AuthErrorCode");

        if (SsoPerformanceTestConstants.VALID_LOGIN.equals(authErrorCode) && launcherOperator.checkIfLoggedIn(httpTool)) {
            synchronized(mapOfLoggedInUsers) {
                mapOfLoggedInUsers.put(user, httpTool);
                listOfLoggedInUsers.add(user);
            }
            logger.info("Logged in user: " + user.getUsername());
        } else {
            logger.error("Failed to login user: " + user.getUsername());
        }

        logger.debug("login. X-AuthErrorCode:{} " + authErrorCode);
    }

    private void logoutUser(ENMUser enmUser) {
        HttpTool httpTool;
        synchronized (mapOfLoggedInUsers) {
            httpTool = mapOfLoggedInUsers.get(enmUser);
        }

        if (launcherOperator.checkIfLoggedIn(httpTool)) {
            HttpResponse response = httpTool.request().get("/logout");

            if (response.getResponseCode().getCode() != HttpStatus.OK.getCode()) {
                logger.error("Failed to logout user " + enmUser.getUsername() + response.getStatusLine());
            } else {
                synchronized (mapOfLoggedInUsers) {
                    mapOfLoggedInUsers.remove(enmUser);
                }
                logger.info("Logged out user: " + enmUser.getUsername());
            }
        }
    }

    private synchronized ENMUser createEnmUser() {
        ENMUser user = new ENMUser();
        user.setUsername("johndoe" + userIndex);
        user.setFirstName("John" + userIndex);
        user.setLastName("Doe" + userIndex);
        user.setEmail("johndoe" + userIndex + "@ericsson.com");
        user.setEnabled(true);
        user.setPassword("Password" + userIndex);
        userIndex++;
        return user;
    }
}
