
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvitationValidationTest extends BaseTest
{

    @Test
    public void verifyDuplicateInvitationRejected() {

        System.out.println("\nTEST: Duplicate Invitation");

        // Step 1: Send first invitation
        Response first =
                client.sendInvitation("duplicate@example.com");

        System.out.println("First Invite Status: "
                + first.getStatusCode());

        Assert.assertEquals(first.getStatusCode(), 201);

        // Verify first invitation is stored
        int countAfterFirst =
                client.getInvitations()
                        .jsonPath().getList("$").size();

        System.out.println("Count after first invite: "
                + countAfterFirst);

        Assert.assertEquals(countAfterFirst, 1);


        // Step 2: Send same email again
        Response duplicate =
                client.sendInvitation("duplicate@example.com");

        System.out.println("Second Invite Status: "
                + duplicate.getStatusCode());

        System.out.println(duplicate.asPrettyString());

        Assert.assertEquals(duplicate.getStatusCode(), 409);

        Assert.assertEquals(
                duplicate.jsonPath().getString("error"),
                "DUPLICATE_INVITATION");


        // Step 3: Verify duplicate was NOT stored
        Response listResponse = client.getInvitations();

        int finalCount =
                listResponse.jsonPath().getList("$").size();

        System.out.println("Final invitation count: "
                + finalCount);

        Assert.assertEquals(finalCount, 1);

        System.out.println("TEST PASSED: Duplicate was rejected");
    }


    @Test
    public void verifyInvalidEmailRejected() {

        System.out.println("\nTEST: Invalid Email");

        Response response =
                client.sendInvitation("invalid-email");

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 400);

        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "INVALID_EMAIL");

        Assert.assertEquals(
                client.getInvitations()
                        .jsonPath().getList("$").size(),
                0);
    }

    @Test
    public void verifyBlankEmailRejected() {

        System.out.println("\nTEST: Blank Email");

        Response response =
                client.sendInvitation("");

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 400);

        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "INVALID_EMAIL");

        Assert.assertEquals(
                client.getInvitations()
                        .jsonPath().getList("$").size(),
                0);
    }
}
