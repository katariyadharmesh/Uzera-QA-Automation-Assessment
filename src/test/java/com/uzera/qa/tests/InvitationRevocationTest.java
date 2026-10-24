
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvitationRevocationTest extends BaseTest {


    @Test
    public void verifyRevocationReleasesFreePlanSlot() {

        System.out.println("\nTEST: Invitation Revocation");

        // Step 1: Create two invitations
        Response first =
                client.sendInvitation("user1@example.com");

        Response second =
                client.sendInvitation("user2@example.com");

        Assert.assertEquals(first.getStatusCode(), 201);
        Assert.assertEquals(second.getStatusCode(), 201);

        int invitationId = first.jsonPath().getInt("id");

        System.out.println("Created invitations: 2");
        System.out.println("First invitation ID: " + invitationId);

        // Step 2: Verify third invitation is rejected
        Response third =
                client.sendInvitation("user3@example.com");

        Assert.assertEquals(third.getStatusCode(), 403);

        System.out.println("Third invitation rejected: 403");

        // Step 3: Revoke first invitation
        Response revoke =
                client.revokeInvitation(invitationId);

        Assert.assertEquals(revoke.getStatusCode(), 200);

        Assert.assertEquals(
                revoke.jsonPath().getString("message"),
                "Invitation revoked");

        System.out.println("Invitation revoked successfully");

        // Step 4: Verify only one invitation remains
        Response afterRevoke = client.getInvitations();

        Assert.assertEquals(afterRevoke.getStatusCode(), 200);

        Assert.assertEquals(
                afterRevoke.jsonPath().getList("$").size(), 1);

        // Step 5: Retry third invitation
        Response retry =
                client.sendInvitation("user3@example.com");

        Assert.assertEquals(retry.getStatusCode(), 201);

        System.out.println("Third invitation now accepted: 201");

        // Step 6: Verify final invitation count
        Response finalResponse = client.getInvitations();

        int finalCount =
                finalResponse.jsonPath().getList("$").size();

        Assert.assertEquals(finalCount, 2);

        System.out.println("Final invitation count: " + finalCount);
        System.out.println("TEST PASSED: Revocation releases slot");
    }

}
