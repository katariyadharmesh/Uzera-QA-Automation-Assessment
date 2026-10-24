
package com.uzera.qa.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MockInvitationServer {

    private final HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<Map<String, Object>> invitations =
            new ArrayList<>();

    private final AtomicInteger idCounter =
            new AtomicInteger(1);

    private String plan = "FREE";

    public MockInvitationServer() throws IOException {

        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0);

        server.createContext(
                "/api/team/invitations",
                this::handleInvitations);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public synchronized void reset(String subscriptionPlan) {
        if (!subscriptionPlan.equals("FREE")
                && !subscriptionPlan.equals("PREMIUM")) {
            throw new IllegalArgumentException("Invalid plan");
        }

        invitations.clear();
        idCounter.set(1);
        plan = subscriptionPlan;
    }


    private synchronized void handleInvitations(
            HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals("POST")
                && path.equals("/api/team/invitations")) {

            createInvitation(exchange);

        } else if (method.equals("GET")
                && path.equals("/api/team/invitations")) {

            sendResponse(exchange, 200, invitations);

        } else if (method.equals("DELETE")
                && path.startsWith("/api/team/invitations/")) {

            revokeInvitation(exchange);

        } else {
            sendResponse(exchange, 405,
                    Map.of("error", "METHOD_NOT_ALLOWED"));
        }
    }


    private void revokeInvitation(
            HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        String idValue = path.substring(
                "/api/team/invitations/".length());

        int invitationId;

        try {
            invitationId = Integer.parseInt(idValue);
        } catch (NumberFormatException e) {

            sendResponse(exchange, 400,
                    Map.of("error", "INVALID_INVITATION_ID"));
            return;
        }

        boolean removed = invitations.removeIf(
                invitation -> invitation.get("id")
                        .equals(invitationId));

        if (removed) {
            sendResponse(exchange, 200,
                    Map.of("message", "Invitation revoked"));
        } else {
            sendResponse(exchange, 404,
                    Map.of("error", "INVITATION_NOT_FOUND"));
        }
    }



    private void createInvitation(
            HttpExchange exchange) throws IOException {

        JsonNode request;

        try {
            request = mapper.readTree(
                    exchange.getRequestBody());
        } catch (Exception e) {
            sendResponse(exchange, 400,
                    Map.of("error", "INVALID_JSON"));
            return;
        }

        String email = request == null
                ? ""
                : request.path("email").asText("").trim();

        if (email.isBlank() ||
                !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {

            sendResponse(exchange, 400,
                    Map.of("error", "INVALID_EMAIL"));
            return;
        }

        boolean duplicate = invitations.stream()
                .anyMatch(invitation ->
                        invitation.get("email")
                                .toString()
                                .equalsIgnoreCase(email));

        if (duplicate) {
            sendResponse(exchange, 409,
                    Map.of("error", "DUPLICATE_INVITATION"));
            return;
        }

        if (plan.equals("FREE")
                && invitations.size() >= 2) {

            sendResponse(exchange, 403,
                    Map.of(
                            "error", "INVITATION_LIMIT_REACHED",
                            "message",
                            "Free plan allows maximum 2 members"
                    ));
            return;
        }

        Map<String, Object> invitation = Map.of(
                "id", idCounter.getAndIncrement(),
                "email", email,
                "status", "PENDING"
        );

        invitations.add(invitation);

        sendResponse(exchange, 201, invitation);
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            Object body) throws IOException {

        byte[] response = mapper.writeValueAsString(body)
                .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json");

        exchange.sendResponseHeaders(
                statusCode, response.length);

        try (var output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    public synchronized void changePlan(String newPlan) {

        if (!newPlan.equals("FREE")
                && !newPlan.equals("PREMIUM")) {

            throw new IllegalArgumentException(
                    "Invalid subscription plan: " + newPlan);
        }

        this.plan = newPlan;

        System.out.println(
                "Subscription changed to: " + newPlan);
    }

}
