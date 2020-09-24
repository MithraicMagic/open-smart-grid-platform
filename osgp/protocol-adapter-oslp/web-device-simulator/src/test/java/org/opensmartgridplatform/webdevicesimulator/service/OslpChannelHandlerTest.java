package org.opensmartgridplatform.webdevicesimulator.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.opensmartgridplatform.webdevicesimulator.service.OslpChannelHandler.OutOfSequenceEvent;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OslpChannelHandlerTest {


    @Mock
    private OslpLogItemRepository oslpLogItemRepository;

    @Mock
    private PrivateKey privateKey;

    @Mock
    private Bootstrap bootstrap;

    @Mock
    private DeviceManagementService deviceManagementService;

    @Mock
    private RegisterDevice registerDevice;
    
    @InjectMocks
    private OslpChannelHandler oslpChannelHandler;
    
    @Test
    public void testSetBootstrap() {
    	//Arrange
    	Bootstrap bootstrap = new Bootstrap();
    	
    	//Act
    	oslpChannelHandler.setBootstrap(bootstrap);

    	//Assert
    	assertEquals(bootstrap, oslpChannelHandler.getBootstrap());
    }
    
    @Test
    public void testHasOutOfSequenceEventForDeviceWithIdNotInList() {
    	//Arrange
    	long deviceId = 1;
    	OutOfSequenceEvent expectedOuput = null;
    	
    	//Act
    	OutOfSequenceEvent output = oslpChannelHandler.hasOutOfSequenceEventForDevice(deviceId);
    	
    	//Assert
    	assertEquals(expectedOuput, output);
    }
    
    @Mock
    ChannelHandlerContext mockCtx;
    
    @Test
    public void testChannelRead0() {
    	//Arrange
    	ChannelHandlerContext ctx = null;
    	
    	byte[] deviceId = "testtesttest".getBytes();
    	KeyFactory fact = null;
		try {
			fact = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	PrivateKey p = null;
		try {
			p = fact.generatePrivate(new RSAPrivateKeySpec(new BigInteger("12"), new BigInteger("13")));
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    	OslpEnvelope message = new OslpEnvelope.Builder()
    			.withDeviceId(deviceId)
    			.withSignature("SHA512encryptedwithRSA")
    			.withPrimaryKey(p)
    			.build();
    	
    	//Act
    	try {
			oslpChannelHandler.channelRead0(ctx, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//Assert
    	//assertEquals(expectedInput, output);
    }
}