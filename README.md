# Example of Test-Driven Development
Sample project to show how TDD works. Go through whole history to see individual steps

## Domain
Sample project creates a simple compoment that allows registering of participant to webinars. Participant provides their email address and receives verification email with token. Calling another method confirms the address and participant gets thank-you email. Finally, the component allows to send all confirmed participants an email about stating the webinar.
## What is missing
Most of things is missing, like actual sending emails, persistence, web server.
Point is to show how **red-green-refactor** works and how to use Mockito for mocking dependencies.
## How to use it
Clone repository and go through all commits from oldest one to newest one.