package br.com.lucas.alves.encurtador_url.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;

@Repository
public class UrlRepository implements IUrlRepository {
    private static final String insertUrlQuery = "INSERT INTO urls (short_code, original_url) VALUES (?, ?)";
    
    private final Logger LOG = LoggerFactory.getLogger(UrlRepository.class);
    
    private Connection connection;

    public UrlRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String getUrlByShortened(String shortened) {
        // Implement the logic to retrieve the original URL based on the shortened URL
        return null; // Placeholder return statement
    }

    @Override
    public void saveUrl(String original, String shortened) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(insertUrlQuery);
            ps.setString(1, shortened);
            ps.setString(2, original);

            int result = ps.executeUpdate();
            if (result == 0) {
                throw new RuntimeException("Failed to insert URL into the database.");
            }
            LOG.info("URL saved successfully.");
            
            ps.close();
        } catch (SQLException e) {
            LOG.error("Error saving URL: {}", e.getMessage());
            throw new RuntimeException("Error saving URL", e);
        }
    }
}
