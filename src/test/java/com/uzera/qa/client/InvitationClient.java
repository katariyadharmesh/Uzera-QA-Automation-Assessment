
package com.uzera.qa.client;

import com.uzera.qa.model.InvitationRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class InvitationClient {

    private final String baseUrl;

    public InvitationClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response sendInvitation(String email)
    {
        InvitationRequest request = new InvitationRequest(email);
        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/team/invitations");
    }

    public Response getInvitations() {

        return given()
                .baseUri(baseUrl)
                .when()
                .get("/api/team/invitations");
    }

    public Response revokeInvitation(int invitationId) {

        return given()
                .baseUri(baseUrl)
                .when()
                .delete("/api/team/invitations/" + invitationId);
    }

    public Response revokeInvitationWithInvalidId(String id) {

        return given()
                .baseUri(baseUrl)
                .when()
                .delete("/api/team/invitations/" + id);
    }

    public Response sendMalformedJson() {

        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body("{\"email\":")
                .when()
                .post("/api/team/invitations");
    }

    public Response updateInvitationUsingPut() {

        return given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body("{\"email\":\"user@example.com\"}")
                .when()
                .put("/api/team/invitations");
    }


}
