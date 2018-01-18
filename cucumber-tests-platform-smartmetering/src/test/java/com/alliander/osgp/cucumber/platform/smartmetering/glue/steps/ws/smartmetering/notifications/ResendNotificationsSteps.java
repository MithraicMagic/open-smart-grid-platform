/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.notifications;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResendNotificationsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationsSteps.class);

    @When("^OSGP checks for which response data a notification has to be resend$")
    public void osgpChecksForWhichResponseDataANotificationHasToBeResend() throws Throwable {
        final int initial_timeout = 60000; // needed to make sure the final ResendNotificationJob has at final least
                                           // runned once

        try {
            Thread.sleep(initial_timeout);
        } catch (final InterruptedException e) {
            LOGGER.error("Thread sleep interrupted ", e.getMessage());
        }
    }

    @Then("^a notification is sent$")
    public void theMissedNotificationIsResent(final Map<String, String> settings) throws Throwable {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^no notification is sent$")
    public void noNotificationIsResend() throws Throwable {
        // Do nothing - scheduled task runs automatically
    }
}
