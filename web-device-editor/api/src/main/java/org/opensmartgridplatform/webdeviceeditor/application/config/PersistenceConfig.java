package org.opensmartgridplatform.webdeviceeditor.application.config;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = { DeviceRepository.class, OrganisationRepository.class })
@EnableTransactionManagement
@PropertySource(value = "classpath:web-device-editor.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@EntityScan(basePackageClasses = { Device.class, Organisation.class })
public class PersistenceConfig extends AbstractPersistenceConfig {
    @Value("${db.driver}")
    private String driver;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.url}")
    private String url;

    @Value("${db.max_pool_size}")
    private int maxPoolSize;

    @Value("${db.auto_commit}")
    private boolean autoCommit;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.format_sql}")
    private String hibernateFormatSql;

    @Value("${hibernate.physical_naming_strategy}")
    private String hibernatePhysicalStategy;

    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;

    @Value("${flyway.initial.version}")
    private String flywayInitialVersion;

    @Value("${flyway.initial.description}")
    private String flywayInitialDescription;

    @Value("${flyway.init.on.migrate:true}")
    private boolean flywayInitOnMigrate;

    @Value("${entitymanager.packages.to.scan}")
    private String entitymanagerPackagesToScan;

    private HikariDataSource dataSource;

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    @Override
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            final HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setDriverClassName(this.driver);
            hikariConfig.setJdbcUrl(this.url);
            hikariConfig.setUsername(this.username);
            hikariConfig.setPassword(this.password);

            hikariConfig.setMaximumPoolSize(this.maxPoolSize);
            hikariConfig.setAutoCommit(this.autoCommit);

            this.dataSource = new HikariDataSource(hikariConfig);
        }
        return this.dataSource;
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     */
    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        // @formatter:off
        return Flyway.configure()
                .baselineVersion(MigrationVersion
                        .fromVersion(this.flywayInitialVersion))
                .baselineDescription(this.flywayInitialDescription)
                .baselineOnMigrate(this.flywayInitOnMigrate)
                .outOfOrder(true).table("schema_version")
                .dataSource(this.getDataSource())
                .load();
        // @formatter:on
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Override
    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("WEB_DEVICE_EDITOR");
        entityManagerFactoryBean.setDataSource(this.getDataSource());
        entityManagerFactoryBean.setPackagesToScan(this.entitymanagerPackagesToScan);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", this.hibernateDialect);
        jpaProperties.put("hibernate.format_sql", this.hibernateFormatSql);
        jpaProperties.put("hibernate.physical_naming_strategy", this.hibernatePhysicalStategy);
        jpaProperties.put("hibernate.show_sql", this.hibernateShowSql);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
}
