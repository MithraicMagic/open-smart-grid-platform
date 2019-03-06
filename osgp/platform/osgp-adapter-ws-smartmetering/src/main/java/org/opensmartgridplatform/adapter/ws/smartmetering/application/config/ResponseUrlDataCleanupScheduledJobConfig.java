/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ResponseUrlDataCleanupJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class ResponseUrlDataCleanupScheduledJobConfig {

    @Value("${smartmetering.scheduling.job.cleanup.response.data.cron.expression}")
    private String cronExpression;

    @Value("${smartmetering.scheduling.job.cleanup.response.data.retention.time.in.days}")
    private int urlDataCleanupJobRetentionTimeInDays;

    @Autowired
    private OsgpScheduler osgpScheduler;

    @Bean
    public int urlDataCleanupJobRetentionTimeInDays() {
        return this.urlDataCleanupJobRetentionTimeInDays;
    }

    @PostConstruct
    private void initializeScheduledJob() throws SchedulerException {
        this.osgpScheduler.createAndScheduleJob(ResponseUrlDataCleanupJob.class, this.cronExpression);
    }
}