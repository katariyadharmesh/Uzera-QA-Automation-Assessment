
package com.uzera.qa.tests;

import com.uzera.qa.model.InvitationResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FreePlanInvitationTest extends BaseTest
{

    @Test
    public void verifyFirstInvitationForFreePlan() {

        Response response =
                client.sendInvitation("user1@example.com");

        Assert.assertEquals(
                response.getStatusCode(), 201);

        InvitationResponse invitation =
                response.as(InvitationResponse.class);

        // Step 3: Verify response using POJO
        Assert.assertEquals(invitation.getEmail(), "user1@example.com");

        Assert.assertTrue(invitation.getId() > 0);

        Assert.assertEquals(invitation.getStatus(), "PENDING");

        // Step 4: Verify invitation is stored
        Response listResponse = client.getInvitations();

        Assert.assertEquals(
                listResponse.getStatusCode(), 200);

        Assert.assertEquals(
                listResponse.jsonPath().getList("$").size(),
                1);
    }

    @Test
    public void verifySecondInvitationForFreePlan() {

        Response first =
                client.sendInvitation("user1@example.com");

        Response second =
                client.sendInvitation("user2@example.com");

        Assert.assertEquals(first.getStatusCode(), 201);
        Assert.assertEquals(second.getStatusCode(), 201);

        Assert.assertEquals(
                second.jsonPath().getString("email"),
                "user2@example.com");

        Assert.assertEquals(
                second.jsonPath().getString("status"),
                "PENDING");

        Response listResponse = client.getInvitations();

        Assert.assertEquals(listResponse.getStatusCode(), 200);

        Assert.assertEquals(
                listResponse.jsonPath().getList("$").size(),
                2);
    }

    @Test
    public void verifyThirdInvitationRejectedForFreePlan() {

        System.out.println("\n=================================");
        System.out.println("TEST: Free Plan - Third Invitation");
        System.out.println("=================================");

        System.out.println("\nStep 1: Sending first invitation");

        Response first =
                client.sendInvitation("user1@example.com");

        System.out.println("HTTP Status: " + first.getStatusCode());
        System.out.println("Response: " + first.asPrettyString());

        Assert.assertEquals(first.getStatusCode(), 201);


        System.out.println("\nStep 2: Sending second invitation");

        Response second =
                client.sendInvitation("user2@example.com");

        System.out.println("HTTP Status: " + second.getStatusCode());
        System.out.println("Response: " + second.asPrettyString());

        Assert.assertEquals(second.getStatusCode(), 201);


        System.out.println("\nStep 3: Attempting third invitation");

        Response third =
                client.sendInvitation("user3@example.com");

        System.out.println("HTTP Status: " + third.getStatusCode());
        System.out.println("Response: " + third.asPrettyString());

        Assert.assertEquals(third.getStatusCode(), 403);

        Assert.assertEquals(
                third.jsonPath().getString("error"),
                "INVITATION_LIMIT_REACHED");


        System.out.println("\nStep 4: Checking stored invitations");

        Response listResponse = client.getInvitations();

        System.out.println("HTTP Status: " + listResponse.getStatusCode());
        System.out.println("Stored Invitations:");
        System.out.println(listResponse.asPrettyString());

        Assert.assertEquals(listResponse.getStatusCode(), 200);

        int count = listResponse.jsonPath().getList("$").size();

        System.out.println("Total Invitations: " + count);

        Assert.assertEquals(count, 2);

        System.out.println("\nTEST PASSED: Third invitation rejected");
    }

}
