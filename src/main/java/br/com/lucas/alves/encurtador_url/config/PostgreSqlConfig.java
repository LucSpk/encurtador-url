package br.com.lucas.alves.encurtador_url.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
