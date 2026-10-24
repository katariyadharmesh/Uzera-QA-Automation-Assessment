
package com.uzera.qa.tests;

import com.uzera.qa.client.InvitationClient;
import com.uzera.qa.mock.MockInvitationServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected MockInvitationServer server;
    protected InvitationClient client;

    protected String getInitialPlan() {
        return "FREE";
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {

        server = new MockInvitationServer();

        server.reset(getInitialPlan());

        server.start();

        String baseUrl =
                "http://127.0.0.1:" + server.getPort();

        client = new InvitationClient(baseUrl);

        System.out.println(
                "\n[SETUP] Mock server started on port "
                        + server.getPort());

        System.out.println(
                "[SETUP] Subscription plan: "
                        + getInitialPlan());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (server != null) {
            server.stop();
            server = null;
        }

        client = null;
        System.out.println("[CLEANUP] Mock server stopped");
    }
}
