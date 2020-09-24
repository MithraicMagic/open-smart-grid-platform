package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.tariffswitching.application.mapping.DomainTariffSwitchingMapper;
import org.opensmartgridplatform.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScheduleManagementServiceTest{
	@Mock
	private DeviceDomainService deviceDomainService;
	
	@Mock
	private DomainTariffSwitchingMapper domainCoreMapper;
	
	@Mock 
	private SsldRepository ssldRepository;
	
	@Mock
	private OrganisationDomainService organisationDomainService;
	
	@Mock
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

	@Mock
	private Device device;
	
	@Mock
	private Ssld ssld;
	
	@Mock
	private CorrelationIds correlationIds;
	
	@InjectMocks
	private ScheduleManagementService scheduleManagementService;
	
	private List<ScheduleEntry> scheduleEntries;
	private List<DeviceOutputSetting> deviceOutputSettings;
	private List<LightValue> lightValues;
	
	
	@BeforeEach
	public void setup() throws FunctionalException {
		this.scheduleEntries = new ArrayList<>();
		this.deviceOutputSettings = new ArrayList<>();
		this.lightValues = new ArrayList<>();
		
		final DeviceOutputSetting dos = Mockito.mock(DeviceOutputSetting.class);
		when(dos.getOutputType()).thenReturn(RelayType.TARIFF_REVERSED);
		
		final ScheduleEntry entry = Mockito.mock(ScheduleEntry.class);
		when(entry.getLightValue()).thenReturn(this.lightValues);
		
		final LightValue value = Mockito.mock(LightValue.class);
		
		//idk how many entries are typically required
		for(int i =0; i < 3; i++) {
			this.scheduleEntries.add(entry);
			this.deviceOutputSettings.add(dos);
			this.lightValues.add(value);
		}
		
		when(this.device.getId()).thenReturn((long) 0.0f);
		when(this.ssld.getOutputSettings()).thenReturn(this.deviceOutputSettings);
		when(this.ssldRepository.findById(any())).thenReturn(Optional.of(this.ssld));
		when(this.device.getIpAddress()).thenReturn("127.0.0.1");
		when(this.deviceDomainService.searchActiveDevice(any(), any(ComponentType.class))).thenReturn(this.device);
	}
	
	@Test
	public void testSetTariffSchedule() throws FunctionalException {
		when(this.device.getDeviceType()).thenReturn(Ssld.SSLD_TYPE);

		final long scheduleTime = (long) 0.0f;
		final String messageType = "testType";
		final int priority = 1;

		final ArgumentCaptor<RequestMessage> requestMessageArgumentCaptor =
				ArgumentCaptor.forClass(RequestMessage.class);
		final ArgumentCaptor<Long> scheduleTimeCaptor = ArgumentCaptor.forClass(long.class);
		final ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> messageTypeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Integer> priorityCapture = ArgumentCaptor.forClass(int.class);

		this.scheduleManagementService.setTariffSchedule(this.correlationIds, this.scheduleEntries, scheduleTime,
				messageType,priority
				);

		verify(this.osgpCoreRequestMessageSender).send(requestMessageArgumentCaptor.capture(),
				messageTypeCaptor.capture(),priorityCapture.capture(),ipCaptor.capture(), scheduleTimeCaptor.capture());

		assertEquals(messageType,messageTypeCaptor.getValue());
		assertEquals(scheduleTime,scheduleTimeCaptor.getValue());
		assertEquals(priority,priorityCapture.getValue());
	}
	
	@Test
	public void testSetTariffScheduleFunctionalExceptionThrown() throws FunctionalException {
		//incorrect type should return an exception
		when(this.device.getDeviceType()).thenReturn(Ssld.PSLD_TYPE);
		
		assertThatThrownBy(()->{
			this.scheduleManagementService.setTariffSchedule(this.correlationIds, this.scheduleEntries, (long) 0.0f, "messageType", 1);});
		verify(this.osgpCoreRequestMessageSender, never()).send(any(RequestMessage.class), eq("messageType"), eq(1), eq(null));
	}
}
