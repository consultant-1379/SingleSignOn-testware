package com.ericsson.nms.sso.performance.test.flows;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.nms.sso.performance.test.steps.ScenarioTestSteps;
import org.apache.log4j.Logger;

public class SsoPerformanceFlows {

    @Inject
    private ScenarioTestSteps scenarioTestSteps;

    private Logger logger = Logger.getLogger(ScenarioTestSteps.class);

    private static int numOfUsers;
    private static int numberOfThreads = 100;

    public TestStepFlow createUsers(final int numberOfUsers) {
        if (numberOfUsers < numberOfThreads) {
            numberOfThreads = numberOfUsers;
        }

        logger.info("Creating " + numberOfUsers + " users using " + numberOfThreads + " threads.");

        SsoPerformanceFlows.numOfUsers = numberOfUsers;
        int numberOfUsersPerThread = numberOfUsers / numberOfThreads;

        return flow("Create users")
                .addTestStep(annotatedMethod(scenarioTestSteps, "createUsers")
                    .withParameter("numberOfUsersPerThread", numberOfUsersPerThread))
                .withVusers(numberOfThreads)
                .build();
    }

    public TestStepFlow loginUsers(final int numberOfUsers) {
        logger.info("Logging in first " + numberOfUsers + " users using " + numberOfThreads + " threads.");

        int numberOfUsersPerThread = numberOfUsers / numberOfThreads;

        return flow("Login users")
                .addTestStep(annotatedMethod(scenarioTestSteps, "loginUsers")
                        .withParameter("numberOfUsersPerThread", numberOfUsersPerThread))
                .withVusers(numberOfThreads)
                .build();
    }

    public TestStepFlow logoutUsers(final int numberOfUsers) {
        logger.info("Logging out last " + numberOfUsers + " users using " + numberOfThreads + " threads.");

        int numberOfUsersPerThread = numberOfUsers / numberOfThreads;

        return flow("Logout users")
                .addTestStep(annotatedMethod(scenarioTestSteps, "logoutUsers")
                        .withParameter("numberOfUsersPerThread", numberOfUsersPerThread))
                .withVusers(numberOfThreads)
                .build();
    }

    public TestStepFlow deleteAllUsers() {
        int numberOfUsersPerThread = numOfUsers / numberOfThreads;

        return flow("Delete all users")
                .addTestStep(annotatedMethod(scenarioTestSteps, "deleteAllUsers")
                        .withParameter("numberOfUsersPerThread", numberOfUsersPerThread))
                .withVusers(numberOfThreads)
                .build();
    }

    public TestStepFlow assignUsersToRole(final int numberOfUsers) {
        int numberOfUsersPerThread = numberOfUsers / numberOfThreads;

        return flow("Assign users to role")
                .addTestStep(annotatedMethod(scenarioTestSteps, "assignUsersToRole")
                        .withParameter("numberOfUsersPerThread", numberOfUsersPerThread))
                .withVusers(numberOfThreads)
                .build();
    }

    public TestStepFlow initConnection() {
        return flow("initializing connection")
                .addTestStep(annotatedMethod(scenarioTestSteps, "initConnection"))
                .build();
    }
}
