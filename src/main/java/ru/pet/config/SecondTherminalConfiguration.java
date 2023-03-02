package ru.pet.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import ru.pet.model.MMS.BRCB;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.pet.repository.secondTherminal",
        entityManagerFactoryRef = "entityManagerFactoryBeanSecondTherminal",
        transactionManagerRef = "secondTerminalTransactionManager"
)
public class SecondTherminalConfiguration {

    @Value("${second.terminal.url}")
    private String URL;
    @Value("${second.terminal.username}")
    private String USERNAME;
    @Value("${second.terminal.password}")
    private String PASSWORD;

    @Bean(name = "dataSourceSecondTherminal")
    public DataSource dataSourceSecondTherminal(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }

    @Bean(name = "entityManagerFactoryBeanSecondTherminal")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(this.dataSourceSecondTherminal());
        em.setPackagesToScan(BRCB.class.getPackage().getName());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return em;
    }

    @Bean
    public PlatformTransactionManager secondTerminalTransactionManager(
            @Qualifier("entityManagerFactoryBeanSecondTherminal") LocalContainerEntityManagerFactoryBean secondTerminalEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(secondTerminalEntityManagerFactory.getObject()));
    }
}