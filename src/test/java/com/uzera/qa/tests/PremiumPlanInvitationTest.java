
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;

import org.testng.annotations.Test;

public class PremiumPlanInvitationTest extends BaseTest
{
    @Override
    protected String getInitialPlan() {
        return "PREMIUM";
    }

    @Test
    public void verifyMultipleInvitationsForPremiumPlan() {

        System.out.println("\n=================================");
        System.out.println("TEST: Premium Plan - Multiple Invites");
        System.out.println("=================================");

        // Send five invitations
        for (int i = 1; i <= 5; i++) {

            String email = "premiumuser" + i + "@example.com";

            Response response = client.sendInvitation(email);

            System.out.println("\nInvitation " + i);
            System.out.println("Email: " + email);
            System.out.println("HTTP Status: " + response.getStatusCode());

            Assert.assertEquals(
                    response.getStatusCode(), 201,
                    "Invitation " + i + " should succeed");

            Assert.assertEquals(
                    response.jsonPath().getString("email"),
                    email);

            Assert.assertEquals(
                    response.jsonPath().getString("status"),
                    "PENDING");
        }

        // Verify all invitations were saved
        Response listResponse = client.getInvitations();

        Assert.assertEquals(listResponse.getStatusCode(), 200);

        int totalInvitations =
                listResponse.jsonPath().getList("$").size();

        System.out.println("\nTotal Invitations: "
                + totalInvitations);

        Assert.assertEquals(totalInvitations, 5);

        System.out.println(
                "\nTEST PASSED: Premium plan accepted all 5 invitations");
    }
}
