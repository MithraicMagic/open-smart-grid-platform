package org.opensmartgridplatform.webdeviceeditor.application.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ImportResource("classpath:applicationContext.xml")
@ComponentScan(basePackages = { "org.opensmartgridplatform.webdeviceeditor.application" })
public class DeviceEditorConfig {
    private final OrganisationRepository organisationRepository;

    @Value("${organisation.identification}")
    private String organisationIdentification;

    @Autowired
    public DeviceEditorConfig(final OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    @Bean
    public Organisation organisation() throws UnknownEntityException {
        final Organisation organisation = this.organisationRepository.findByOrganisationIdentification(
                this.organisationIdentification);
        if (organisation == null) {
            throw new UnknownEntityException(Organisation.class, this.organisationIdentification);
        }
        return organisation;
    }

    @Bean
    public MapperFacade deviceEditorMapperFacade() {
        final MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.classMap(org.opensmartgridplatform.domain.core.entities.Device.class,
                org.opensmartgridplatform.webdeviceeditor.domain.entities.Device.class).byDefault().register();

        return factory.getMapperFacade();
    }
}
