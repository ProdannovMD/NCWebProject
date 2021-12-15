package com.netcracker.application.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mySqlEntityManagerFactoryBean",
        transactionManagerRef = "mySqlTransactionManager",
        basePackages = "com.netcracker.application.repository"
)
@PropertySource("classpath:application.properties")
public class MySQLConfig {
    final Environment environment;

    @Autowired
    public MySQLConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mysql.datasource")
    public DataSource mySqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("spring.mysql.datasource.driver-class-name"));
        dataSource.setUrl(environment.getProperty("spring.mysql.datasource.url"));
        dataSource.setUsername(environment.getProperty("spring.mysql.datasource.username"));
        dataSource.setPassword(environment.getProperty("spring.mysql.datasource.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mySqlEntityManagerFactoryBean(@Qualifier("mySqlDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPersistenceUnitName("mysqlPU");
        emf.setJpaProperties(hibernateProperties());
        emf.setPackagesToScan("com.netcracker.application.model");
        emf.setJpaVendorAdapter(getHibernateAdapter());
        return emf;
    }

    @Bean
    public PlatformTransactionManager mySqlTransactionManager(@Qualifier("mySqlEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.setProperty("hibernate.show_sql", "true");
        return props;
    }

    @Bean
    public JpaVendorAdapter getHibernateAdapter() {
        return new HibernateJpaVendorAdapter();
    }
}
