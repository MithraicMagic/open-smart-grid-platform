/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * An entity class which contains the information of a single relay of a device.
 */
@Entity
public class RelayStatus extends AbstractEntity {

    private static final long serialVersionUID = -6288672019209482063L;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Device device;

    @Column
    private boolean lastKnownState;

    @Column
    private Date lastKnowSwitchingTime;

    public RelayStatus() {
        // Default constructor
    }

    public RelayStatus(final boolean lastKnownState, final Date lastKnowSwitchingTime) {
        this.lastKnownState = lastKnownState;
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public void updateStatus(final boolean lastKnownState, final Date lastKnowSwitchingTime) {
        this.lastKnownState = lastKnownState;
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public boolean isLastKnownState() {
        return this.lastKnownState;
    }

    public void setLastKnownState(final boolean lastKnownState) {
        this.lastKnownState = lastKnownState;
    }

    public Date getLastKnowSwitchingTime() {
        return this.lastKnowSwitchingTime;
    }

    public void setLastKnowSwitchingTime(final Date lastKnowSwitchingTime) {
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public Device getDevice() {
        return this.device;
    }

    public void Device(final Device device) {
        this.device = device;
    }
}