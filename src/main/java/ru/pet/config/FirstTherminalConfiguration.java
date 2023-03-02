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
        basePackages = "ru.pet.repository.firstTherminal",
        entityManagerFactoryRef = "entityManagerFactoryBeanFirstTherminal",
        transactionManagerRef = "FirstTerminalTransactionManager"
)
public class FirstTherminalConfiguration {

    @Value("${first.terminal.url}")
    private String URL;
    @Value("${first.terminal.username}")
    private String USERNAME;
    @Value("${first.terminal.password}")
    private String PASSWORD;

    @Bean(name = "dataSourceFirstTherminal")
    public DataSource dataSourceFirstTherminal(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }

    @Bean(name = "entityManagerFactoryBeanFirstTherminal")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(this.dataSourceFirstTherminal());
        em.setPackagesToScan(BRCB.class.getPackage().getName());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return em;
    }

    @Bean
    public PlatformTransactionManager FirstTerminalTransactionManager(
            @Qualifier("entityManagerFactoryBeanFirstTherminal") LocalContainerEntityManagerFactoryBean FirstTerminalEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(FirstTerminalEntityManagerFactory.getObject()));
    }

}
