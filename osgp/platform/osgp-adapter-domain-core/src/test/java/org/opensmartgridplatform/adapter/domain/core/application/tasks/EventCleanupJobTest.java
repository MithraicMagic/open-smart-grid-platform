/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.core.application.services.TransactionalEventService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.shared.utils.FileUtils;
import org.springframework.test.util.ReflectionTestUtils;

public class EventCleanupJobTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @InjectMocks
    private EventCleanupJob eventCleanupJob;

    @Mock
    private TransactionalEventService transactionalEventService;

    private final String filePrefix = "junit-mocked-csv-file-";

    @Before
    public void initMocksAndSetProperties() {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(this.eventCleanupJob, "eventRetentionPeriodInMonths", 1);
        ReflectionTestUtils.setField(this.eventCleanupJob, "csvFileLocation", this.folder.getRoot().getAbsolutePath());
        ReflectionTestUtils.setField(this.eventCleanupJob, "csvFilePrefix", this.filePrefix);
        ReflectionTestUtils.setField(this.eventCleanupJob, "csvFileCompressionEnabled", true);
        ReflectionTestUtils.setField(this.eventCleanupJob, "eventPageSize", 10);
    }

    @Test
    public void test() throws IOException {
        // Arrange
        final List<Event> events = this.createEvents();
        Mockito.when(this.transactionalEventService.getEventsBeforeDate(any(Date.class), any(Integer.class)))
                .thenReturn(events);

        // Act
        this.eventCleanupJob.execute(null);

        // Assert

        // Example path:
        // /tmp/junit7318456469288690301/junit-mocked-csv-file-20190416-112237.csv.zip
        final String path = this.folder.getRoot().getAbsolutePath();

        final File zipFile = FileUtils.findFileInFolderUsingFilePrefix(path, this.filePrefix);
        assertThat(zipFile.exists()).isTrue();
        assertThat(zipFile).hasExtension("zip");
        assertThat(zipFile.length()).isNotZero();

        ZipFileReader.extractZipFile(zipFile.getAbsolutePath(), path);
        zipFile.delete();

        final File csvFile = FileUtils.findFileInFolderUsingFilePrefix(path, this.filePrefix);
        assertThat(csvFile.exists()).isTrue();
        assertThat(csvFile).hasExtension("csv");
        assertThat(csvFile.length()).isNotZero();

        final int numberOfLines = FileUtils.countNumberOfLinesInFile(csvFile);
        assertThat(numberOfLines).isEqualTo(2);
    }

    private List<Event> createEvents() {
        final Event event = new Event(new Device("test"), DateTime.now().minusMonths(1).toDate(),
                EventType.DIAG_EVENTS_GENERAL, "description", 1);
        return Arrays.asList(event);
    }

}
