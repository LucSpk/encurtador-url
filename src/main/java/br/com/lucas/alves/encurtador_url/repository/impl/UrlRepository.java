package br.com.lucas.alves.encurtador_url.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.com.lucas.alves.encurtador_url.domain.entity.Url;
import br.com.lucas.alves.encurtador_url.repository.IUrlRepository;

@Repository
public class UrlRepository implements IUrlRepository {
    private static final String SELECT_URL_BY_ORIGINAL_URL = "SELECT * FROM urls WHERE original_url = ?";
    private static final String UPDATE_URL_SET_SHORT_CODE_WHERE_ID_QUERY = "UPDATE urls SET short_code = ? WHERE id = ?";
    private static final String INSERT_URL_QUERY = "INSERT INTO urls (short_code, original_url) VALUES (?, ?)";
    private static final String SELECT_URL_QUERY = "SELECT original_url FROM urls WHERE short_code = ?";

    private final Logger LOGGER = LoggerFactory.getLogger(UrlRepository.class);
    
    private Connection connection;

    public UrlRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String getUrlByShortened(String shortened) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_URL_QUERY)) { // Quando declarado entre parenteses, o PreparedStatement é fechado automaticamente após o bloco try-with-resources
            ps.setString(1, shortened);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String originalUrl = rs.getString("original_url");
                    LOGGER.info("URL found for short code {}: {}", shortened, originalUrl);
                    return originalUrl;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving URL: {}", e.getMessage());
            throw new RuntimeException("Error retrieving URL", e);
        }
        throw new RuntimeException("URL not found for the given shortened code.");
    }

    @Override
    public Url getByUrl(String original) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_URL_BY_ORIGINAL_URL)) {
            ps.setString(1, original);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Url url = new Url();
                    url.setId(rs.getLong("id"));
                    url.setShortCode(rs.getString("short_code"));
                    url.setOriginalUrl(rs.getString("original_url"));
                    url.setCreatedAt(rs.getString("created_at"));
                    return url;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving URL: {}", e.getMessage());
            throw new RuntimeException("Error retrieving URL", e);
        }
        throw new RuntimeException("URL not found for the given original URL.");
    }

    @Override
    public long saveUrl(String original, String shortened) {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_URL_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, shortened);
            ps.setString(2, original);

            int result = ps.executeUpdate();

            if (result == 0) {
                throw new RuntimeException("Failed to insert URL into the database.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                Long id = rs.getLong(1);

                LOGGER.info("URL saved successfully with id: {}", id);

                return id;
            }
            throw new RuntimeException("Failed to retrieve generated ID.");
        }
        } catch (SQLException e) {
            LOGGER.error("Error saving URL: {}", e.getMessage());
            throw new RuntimeException("Error saving URL", e);
        } 
    }

    @Override
    public void updateUrlShortCode(long id, String shortCode) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_URL_SET_SHORT_CODE_WHERE_ID_QUERY)) {
            ps.setString(1, shortCode);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error updating URL short code: {}", e.getMessage());
            throw new RuntimeException("Error updating URL short code", e);
        }
    }
}
