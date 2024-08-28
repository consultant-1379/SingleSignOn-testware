package com.ericsson.nms.sso.performance.test.scenario;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.ExecutionMode;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.nms.security.OpenIDMOperatorImpl;
import com.ericsson.nms.sso.performance.test.flows.SsoPerformanceFlows;
import com.google.inject.Inject;

public class SsoPerformanceTest extends TorTestCaseHelper implements
		TestCase {

    private Logger logger = Logger.getLogger(SsoPerformanceTest.class);

    @Inject
    OpenIDMOperatorImpl openIDMOperator;

    @Inject
    private SsoPerformanceFlows ssoAuthPerformanceFlows;

    // Number of users have to be even and numberOfUsers%100 have to be 0 (zero)
    private int numberOfUsers = 800;
    
    @Test
    public void setUp() {
        logger.info("Initializing connection with app and creating test users.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.initConnection())
                .addFlow(ssoAuthPerformanceFlows.createUsers(numberOfUsers))
                .withExecutionMode(ExecutionMode.SEQUENTIAL)
                .build();
        executeTestScenario(scenario);
    }

    @Test
    @TestId(id = "TORF-", title = "Login 2000 users parallel")
    public void login2000Users() {
        logger.info("Logging in test users.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.loginUsers(numberOfUsers))
                .build();
        executeTestScenario(scenario);
    }

    @Test
    @TestId(id = "TORF-", title = "Logout 2000 users ")
    public void logout2000Users() {
        logger.info("Logging out test users.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.logoutUsers(numberOfUsers))
                .build();
        executeTestScenario(scenario);
    }

    @Test
    @TestId(id = "TORF-", title = "Login 1000 users - preparing to next step")
    public void login1000UsersAsPreparingToNextTestStep() {
        logger.info("Preparing to next test by login users to be logged out.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.loginUsers(numberOfUsers/2))
                .build();
        executeTestScenario(scenario);
    }

    @Test
    @TestId(id = "TORF-", title = "Assigning users to role")
    public void assignUsersToRole() {
        logger.info("Assigning users to role.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.assignUsersToRole(numberOfUsers))
                .addFlow(ssoAuthPerformanceFlows.initConnection())
                .withExecutionMode(ExecutionMode.SEQUENTIAL)
                .build();
        executeTestScenario(scenario);
    }

    @Test
    @TestId(id = "TORF-", title = "Login 1000 users and logout 1000 in parallel")
    public void loginLogoutUsersParallel() {
        logger.info("Logging in and logging out users in parallel.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.loginUsers(numberOfUsers/2))
                .addFlow(ssoAuthPerformanceFlows.logoutUsers(numberOfUsers/2))
                .withExecutionMode(ExecutionMode.PARALLEL)
                .build();
        executeTestScenario(scenario);
    }

    @AfterTest
    public void tearDown() {
        logger.info("Deleting test users.");
        final TestScenario scenario = scenario()
                .addFlow(ssoAuthPerformanceFlows.initConnection())
                .addFlow(ssoAuthPerformanceFlows.deleteAllUsers())
                .build();
        executeTestScenario(scenario);
    }

    private void executeTestScenario(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
