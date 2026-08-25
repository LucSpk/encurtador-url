package br.com.lucas.alves.encurtador_url.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class PostgreSqlConfig {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlConfig.class);

    private String postgreUrl = "jdbc:postgresql://localhost:5432/url_shortener";
    private String postgreUser = "postgres";
    private String postgrePass = "postgres";

    @Bean
    public Connection postgreSqlConnection() {
        try {
            Connection conexao = DriverManager.getConnection(postgreUrl, postgreUser, postgrePass);
            ResultSet resultSet = conexao.createStatement().executeQuery("SELECT version();");

            while (resultSet.next()) {
                LOG.info("PostgreSQL Version: {}", resultSet.getString(1));
            }

            LOG.info("PostgreSQL connection established successfully.");
            return conexao;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create PostgreSQL connection", e);
        } 
    }

    @Bean
    public DataSource dataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(postgreUrl);
        config.setUsername(postgreUser);
        config.setPassword(postgrePass);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }
}
