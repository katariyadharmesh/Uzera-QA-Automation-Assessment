
package com.uzera.qa.tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.*;

public class InvitationConcurrencyTest extends BaseTest {


    @Test
    public void verifyConcurrentInvitationsRespectFreePlanLimit()
            throws Exception {

        System.out.println("\nTEST: Concurrent Invitations");

        // Create one existing invitation
        Response existing =
                client.sendInvitation("existing@example.com");

        Assert.assertEquals(existing.getStatusCode(), 201);

        // Two worker threads
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            // First concurrent request
            Future<Response> futureA = executor.submit(() -> {
                ready.countDown();
                start.await();
                return client.sendInvitation("userA@example.com");
            });

            // Second concurrent request
            Future<Response> futureB = executor.submit(() -> {
                ready.countDown();
                start.await();
                return client.sendInvitation("userB@example.com");
            });

            // Wait until both threads are ready
            Assert.assertTrue(
                    ready.await(5, TimeUnit.SECONDS),
                    "Both request threads should be ready");

            // Release both threads together
            start.countDown();

            Response responseA =
                    futureA.get(10, TimeUnit.SECONDS);

            Response responseB =
                    futureB.get(10, TimeUnit.SECONDS);

            System.out.println("User A Status: "
                    + responseA.getStatusCode());

            System.out.println("User B Status: "
                    + responseB.getStatusCode());

            List<Integer> statusCodes = List.of(
                    responseA.getStatusCode(),
                    responseB.getStatusCode());

            long successCount = statusCodes.stream()
                    .filter(status -> status == 201)
                    .count();

            long rejectedCount = statusCodes.stream()
                    .filter(status -> status == 403)
                    .count();

            Assert.assertEquals(successCount, 1L);
            Assert.assertEquals(rejectedCount, 1L);

            Response list = client.getInvitations();

            Assert.assertEquals(list.getStatusCode(), 200);

            int finalCount =
                    list.jsonPath().getList("$").size();

            Assert.assertEquals(finalCount, 2);

            System.out.println("Final invitation count: "
                    + finalCount);

            System.out.println(
                    "TEST PASSED: Free plan limit maintained");

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }
}
