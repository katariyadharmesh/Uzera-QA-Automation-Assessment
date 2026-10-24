
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriptionChangeTest extends BaseTest {

    @Test
    public void verifyFreeToPremiumUpgrade() {

        System.out.println("\nTEST: Free to Premium Upgrade");

        // Step 1: Create two invitations on Free plan
        Response first =
                client.sendInvitation("user1@example.com");

        Response second =
                client.sendInvitation("user2@example.com");

        Assert.assertEquals(first.getStatusCode(), 201);
        Assert.assertEquals(second.getStatusCode(), 201);

        // Step 2: Verify third invitation is blocked
        Response third =
                client.sendInvitation("user3@example.com");

        Assert.assertEquals(third.getStatusCode(), 403);

        System.out.println("Free plan limit enforced");

        // Step 3: Upgrade to Premium
        server.changePlan("PREMIUM");

        // Step 4: Retry third invitation
        Response retry =
                client.sendInvitation("user3@example.com");

        Assert.assertEquals(retry.getStatusCode(), 201);

        System.out.println("Third invitation accepted after upgrade");

        // Step 5: Verify all invitations exist
        Response list = client.getInvitations();

        Assert.assertEquals(list.getStatusCode(), 200);

        Assert.assertEquals(
                list.jsonPath().getList("$").size(), 3);

        System.out.println("Final invitation count: 3");
        System.out.println("TEST PASSED: Free to Premium upgrade");
    }

    @Test
    public void verifyPremiumToFreeDowngrade() {

        System.out.println("\nTEST: Premium to Free Downgrade");

        // Step 1: Start with Premium subscription
        server.changePlan("PREMIUM");

        // Step 2: Create three invitations
        for (int i = 1; i <= 3; i++) {

            Response response = client.sendInvitation(
                    "premiumuser" + i + "@example.com");

            Assert.assertEquals(response.getStatusCode(), 201);
        }

        System.out.println("Three invitations created on Premium");

        // Step 3: Downgrade to Free
        server.changePlan("FREE");

        // Step 4: Try inviting another member
        Response fourth =
                client.sendInvitation("user4@example.com");

        Assert.assertEquals(fourth.getStatusCode(), 403);

        Assert.assertEquals(
                fourth.jsonPath().getString("error"),
                "INVITATION_LIMIT_REACHED");

        System.out.println("Fourth invitation correctly rejected");

        // Step 5: Verify existing invitations remain
        Response list = client.getInvitations();

        Assert.assertEquals(list.getStatusCode(), 200);

        int count = list.jsonPath().getList("$").size();

        Assert.assertEquals(count, 3);

        System.out.println("Existing invitations preserved: " + count);
        System.out.println("TEST PASSED: Premium to Free downgrade");
    }
}
