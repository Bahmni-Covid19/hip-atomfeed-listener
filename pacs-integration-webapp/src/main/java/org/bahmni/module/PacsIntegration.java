package org.bahmni.module;

import org.bahmni.module.pacsintegration.repository.CronJobRepository;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = "org.bahmni.module.*")
@EnableTransactionManagement
public class PacsIntegration extends SpringBootServletInitializer {

    @Autowired
    CronJobRepository cronJobRepository;

    @RequestMapping("/")
    String home() {
        return "PACS Integration module is up and running.";
    }

    public static void main(String[] args) throws Exception {
        createDB();
        SpringApplication.run(PacsIntegration.class, args);
    }

    private static boolean checkIfDBExits(Connection connection, String databaseName) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT FROM pg_database WHERE datname = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setString(1,databaseName);
        ResultSet rs = stmt.executeQuery();
        rs.last();
        return rs.getRow() == 0;
    }

    private static void createDB() throws Exception {
        String jdbcUrl = System.getenv("DATABASE_URL");
        String jdbcUsername = System.getenv("DATABASE_USERNAME");
        String jdbcUserPwd = System.getenv("DATABASE_PASSWORD");
        String jdbcDatabase = System.getenv("DATABASE_NAME");
        if (isEmptyString(jdbcUrl) || isEmptyString(jdbcUsername) || isEmptyString(jdbcUserPwd) || isEmptyString(jdbcDatabase)) {
            throw new Exception("you must set DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, DATABASE_NAME as properties, either using -D option or setting as env");
        }
        Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcUserPwd);
        if(checkIfDBExits(connection,jdbcDatabase)){
            PreparedStatement stmt = connection.prepareStatement("CREATE DATABASE " + jdbcDatabase);
            System.out.println("Creating database for hip feed listener");
            stmt.executeUpdate();
        }
    }

    private static boolean isEmptyString(String value) {
        return (value == null) || "".equals(value.trim());
    }

    @Bean
    public SessionFactory sessionFactory(HibernateEntityManagerFactory hemf) {
        return hemf.getSessionFactory();
    }
}
