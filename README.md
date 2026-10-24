# Uzera QA Automation Engineer Assessment

## Project Overview

This project demonstrates API automation testing for a plan-based team invitation system.

**Business rules:**
- FREE subscription: Maximum 2 team invitations/members, based on the assessment assumption.
- PREMIUM subscription: Unlimited invitations.
- Invitations can be created, listed, and revoked.
- Subscription upgrades and downgrades affect invitation limits.

## Technology Stack

- Java 21
- Maven
- REST Assured
- TestNG
- Jackson
- IntelliJ IDEA

## Framework Structure

```text
src/
├── main/java/com/uzera/qa/
│   ├── mock/
│   │   └── MockInvitationServer.java
│   └── model/
│       ├── InvitationRequest.java
│       └── InvitationResponse.java
└── test/java/com/uzera/qa/
    ├── client/
    │   └── InvitationClient.java
    └── tests/
        ├── BaseTest.java
        ├── FreePlanInvitationTest.java
        ├── PremiumPlanInvitationTest.java
        ├── InvitationValidationTest.java
        ├── InvitationRevocationTest.java
        ├── SubscriptionChangeTest.java
        ├── InvitationConcurrencyTest.java
        └── InvitationNegativeTest.java
```

## Setup and Execution

**Prerequisites:**
- JDK 21
- Maven
- Java and Maven available in the system PATH

**Run all automated tests:**

```bash
mvn clean test
```

**Run an individual test class:**

```bash
mvn -Dtest=FreePlanInvitationTest test
```

## Automated Test Coverage

| Area | Test scenarios |
|---|---|
| Free plan | First and second invitations accepted; third rejected |
| Premium plan | Multiple invitations accepted |
| Validation | Duplicate, invalid and blank emails rejected |
| Revocation | Revoking an invitation releases a Free-plan slot |
| Subscription changes | Free to Premium upgrade; Premium to Free downgrade |
| Concurrency | Simultaneous invitations cannot exceed Free-plan limit |
| Negative testing | Invalid IDs, nonexistent invitations, malformed JSON and unsupported HTTP methods |

**Latest verified execution:** 15 tests passed, 0 failures, 0 errors, 0 skipped.

## Mock API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/team/invitations` | Create invitation |
| GET | `/api/team/invitations` | List invitations |
| DELETE | `/api/team/invitations/{id}` | Revoke invitation |

## Assumptions

- The actual Uzera application/API was not provided for this assessment.
- An in-memory mock HTTP server was implemented to demonstrate executable API automation.
- The FREE-plan limit is applied to stored invitations as a simplified representation of team capacity.
- PREMIUM subscriptions do not enforce a maximum invitation count.
- New invitations have a `PENDING` status.
- Revoked invitations are removed from the in-memory collection.
- Downgrading to FREE preserves existing invitations but blocks new invitations while the limit is reached.
- Tests start with fresh mock-server state and use dynamically allocated local ports.

## Limitations

- Tests validate the implemented mock API, not the actual Uzera production system.
- Email delivery, invitation acceptance, expiry and authentication are not implemented.
- Browser/UI tests are outside the scope of the runnable automation.
- Persistence and distributed concurrency are not simulated.
- Additional production integration tests would be needed to validate real team-member counting and invitation lifecycle rules.

## Test Reports

Maven Surefire generates test execution results under:

`target/surefire-reports/`

## Test Design

The framework separates API request handling, request/response models, test logic and mock-server behavior.

`BaseTest` provides common setup and cleanup to support independent and repeatable test execution.