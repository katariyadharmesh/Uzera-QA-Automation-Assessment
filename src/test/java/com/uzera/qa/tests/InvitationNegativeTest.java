
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvitationNegativeTest extends BaseTest {

    @Test
    public void verifyNonexistentInvitationCannotBeRevoked() {

        System.out.println("\nTEST: Nonexistent Invitation");

        Response response = client.revokeInvitation(9999);

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 404);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "INVITATION_NOT_FOUND");

        System.out.println("TEST PASSED: Nonexistent invitation");
    }

    @Test
    public void verifyInvalidInvitationIdRejected() {

        System.out.println("\nTEST: Invalid Invitation ID");

        Response response =
                client.revokeInvitationWithInvalidId("abc");

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 400);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "INVALID_INVITATION_ID");

        System.out.println("TEST PASSED: Invalid invitation ID");
    }

    @Test
    public void verifyMalformedJsonRejected() {

        System.out.println("\nTEST: Malformed JSON");

        Response response = client.sendMalformedJson();

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 400);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "INVALID_JSON");

        System.out.println("TEST PASSED: Malformed JSON");
    }

    @Test
    public void verifyUnsupportedHttpMethodRejected() {

        System.out.println("\nTEST: Unsupported HTTP Method");

        Response response = client.updateInvitationUsingPut();

        System.out.println(response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 405);
        Assert.assertEquals(
                response.jsonPath().getString("error"),
                "METHOD_NOT_ALLOWED");

        System.out.println("TEST PASSED: Unsupported HTTP method");
    }
}
