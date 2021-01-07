package org.opensmartgridplatform.webdeviceeditor.domain.entities;

import java.net.InetAddress;
import java.util.Date;

import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;

public class Device {
    private String deviceIdentification;
    private String alias;
    private Address containerAddress;
    private GpsCoordinates gpsCoordinates;
    private CdmaSettings cdmaSettings;
    private String deviceType;
    private InetAddress networkAddress;
    private boolean isActivated;
    //private final List<DeviceAuthorization> authorizations = new ArrayList();
    private ProtocolInfo protocolInfo;
    private boolean inMaintenance;
    private org.opensmartgridplatform.domain.core.entities.Device gatewayDevice;
    //private final List<String> organisations = new ArrayList();
    private DeviceModel deviceModel;
    private Date technicalInstallationDate;
    private DeviceLifecycleStatus deviceLifecycleStatus;
    private Date lastSuccessfulConnectionTimestamp;
    private Date lastFailedConnectionTimestamp;
    private Integer failedConnectionCount;
    private IntegrationType integrationType;

    public Device() {
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public Address getContainerAddress() {
        return this.containerAddress;
    }

    public void setContainerAddress(final Address containerAddress) {
        this.containerAddress = containerAddress;
    }

    public GpsCoordinates getGpsCoordinates() {
        return this.gpsCoordinates;
    }

    public void setGpsCoordinates(final GpsCoordinates gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public CdmaSettings getCdmaSettings() {
        return this.cdmaSettings;
    }

    public void setCdmaSettings(final CdmaSettings cdmaSettings) {
        this.cdmaSettings = cdmaSettings;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public void setNetworkAddress(final InetAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    public boolean isActivated() {
        return this.isActivated;
    }

    public void setActivated(final boolean activated) {
        this.isActivated = activated;
    }

    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public void setProtocolInfo(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public boolean isInMaintenance() {
        return this.inMaintenance;
    }

    public void setInMaintenance(final boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public org.opensmartgridplatform.domain.core.entities.Device getGatewayDevice() {
        return this.gatewayDevice;
    }

    public void setGatewayDevice(final org.opensmartgridplatform.domain.core.entities.Device gatewayDevice) {
        this.gatewayDevice = gatewayDevice;
    }

    public DeviceModel getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(final DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Date getTechnicalInstallationDate() {
        return this.technicalInstallationDate;
    }

    public void setTechnicalInstallationDate(final Date technicalInstallationDate) {
        this.technicalInstallationDate = technicalInstallationDate;
    }

    public DeviceLifecycleStatus getDeviceLifecycleStatus() {
        return this.deviceLifecycleStatus;
    }

    public void setDeviceLifecycleStatus(final DeviceLifecycleStatus deviceLifecycleStatus) {
        this.deviceLifecycleStatus = deviceLifecycleStatus;
    }

    public Date getLastSuccessfulConnectionTimestamp() {
        return this.lastSuccessfulConnectionTimestamp;
    }

    public void setLastSuccessfulConnectionTimestamp(final Date lastSuccessfulConnectionTimestamp) {
        this.lastSuccessfulConnectionTimestamp = lastSuccessfulConnectionTimestamp;
    }

    public Date getLastFailedConnectionTimestamp() {
        return this.lastFailedConnectionTimestamp;
    }

    public void setLastFailedConnectionTimestamp(final Date lastFailedConnectionTimestamp) {
        this.lastFailedConnectionTimestamp = lastFailedConnectionTimestamp;
    }

    public IntegrationType getIntegrationType() {
        return this.integrationType;
    }

    public void setIntegrationType(final IntegrationType integrationType) {
        this.integrationType = integrationType;
    }
}
