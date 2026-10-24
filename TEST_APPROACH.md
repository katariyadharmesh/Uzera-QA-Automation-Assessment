# Part 1 – Test Approach: Team Invitations

## Objective

Verify that team invitations work correctly based on the user's subscription plan. The main focus is the Free plan limit, Premium plan behavior, invitation validation, and subscription changes.

## Key Test Scenarios

| Scenario | Expected Result |
|---|---|
| First and second invitation on Free plan | Invitations allowed |
| Third invitation on Free plan | Invitation blocked |
| Multiple invitations on Premium plan | Invitations allowed |
| Invalid or empty email | Validation error |
| Duplicate invitation | Duplicate not allowed |
| Revoke an invitation | Invitation removed and slot available |
| Upgrade Free to Premium | Additional invitations allowed |
| Downgrade Premium to Free | Existing invitations retained; new ones blocked if limit reached |
| Two invitations sent at the same time | Free plan limit should not be exceeded |
| Invalid invitation ID or malformed request | Proper error response |

## Other Testing Areas

- **UI:** Check invitation form, validation messages, plan-limit message, and invitation list.
- **Security:** Verify only authorized users can invite or remove team members.
- **Performance:** Check API response time and behavior with multiple simultaneous requests.
- **Email:** Verify invitation email, link, expiry, and acceptance flow.
- **Compatibility:** Basic testing on supported browsers.

## Manual vs Automation

**Automation:** API scenarios, plan limits, validations, revocation, subscription changes, concurrency, and regression.

**Manual:** UI behavior, email invitation flow, usability, and exploratory testing.

## Assumptions / Clarifications

- Free plan supports a maximum of 2 members. Need to confirm whether pending invitations and the team owner count toward this limit.
- Premium plan has no member limit.
- Need confirmation of invitation expiry time and acceptance rules.
- Need confirmation of expected behavior when downgrading a subscription.

## Automation Implementation

As the actual application/API was not provided, I created a mock API using Java and automated the scenarios using REST Assured, TestNG, and Maven.

Currently, **15 automated tests are passing**. Each test runs with fresh test data and independent setup/cleanup.

The automation covers the main API business rules. UI, actual email delivery, authentication, and invitation acceptance/expiry are not included in the mock implementation.