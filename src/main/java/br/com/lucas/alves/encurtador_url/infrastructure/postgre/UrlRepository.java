package br.com.lucas.alves.encurtador_url.infrastructure.postgre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.domain.entity.Url;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToInsertException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToRetrieveGeneratedIdException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToUpdateException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;

@Repository
public class UrlRepository implements IUrlOutputPort {
private static final String SELECT_URL_BY_ORIGINAL_URL = "SELECT id, short_code, original_url, created_at FROM urls WHERE original_url = ?";
    private static final String UPDATE_URL_SET_SHORT_CODE_WHERE_ID_QUERY = "UPDATE urls SET short_code = ? WHERE id = ?";
    private static final String INSERT_URL_QUERY = "INSERT INTO urls (short_code, original_url) VALUES (?, ?)";
    private static final String SELECT_URL_QUERY = "SELECT original_url FROM urls WHERE short_code = ?";

    private final Logger LOGGER = LoggerFactory.getLogger(UrlRepository.class);

    private final DataSource dataSource;

    public UrlRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getUrlByShortened(String shortened) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_URL_QUERY)) { // Quando declarado entre parenteses, o PreparedStatement é fechado automaticamente após o bloco try-with-resources
            ps.setString(1, shortened);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String originalUrl = rs.getString("original_url");
                    LOGGER.info("URL found: {}", originalUrl);
                    return originalUrl;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving URL: {}", e.getMessage());
            throw new RuntimeException("Error retrieving URL", e);
        }
        throw new ShortCodeNotFoundException("URL não encontrada para o código encurtado fornecido.");
    }

    @Override
    public Optional<Url> getByUrl(String original) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_URL_BY_ORIGINAL_URL)) {
            ps.setString(1, original);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Url url = new Url();
                    url.setId(rs.getLong("id"));
                    url.setShortCode(rs.getString("short_code"));
                    url.setOriginalUrl(rs.getString("original_url"));
                    url.setCreatedAt(rs.getString("created_at"));
                    return Optional.of(url);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving URL: {}", e.getMessage());
            throw new RuntimeException("Error retrieving URL", e);
        }
        return Optional.empty();
    }

    @Override
    public long saveUrl(String original, String shortened) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(INSERT_URL_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, shortened);
            ps.setString(2, original);

            int result = ps.executeUpdate();

            if (result == 0) {
                throw new FailToInsertException("Falha ao inserir URL no banco de dados.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                Long id = rs.getLong(1);

                LOGGER.info("URL salva com sucesso com id: {}", id);

                return id;
            }
            throw new FailToRetrieveGeneratedIdException("Falha ao recuperar o ID gerado.");
        }
        } catch (SQLException e) {
            LOGGER.error("Error saving URL: {}", e.getMessage());
            throw new RuntimeException("Error saving URL", e);
        } 
    }

    @Override
    public void updateUrlShortCode(long id, String shortCode) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_URL_SET_SHORT_CODE_WHERE_ID_QUERY)) {
            ps.setString(1, shortCode);
            ps.setLong(2, id);
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new FailToUpdateException("Falha ao atualizar código curto da URL.");
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating URL short code: {}", e.getMessage());
            throw new RuntimeException("Error updating URL short code", e);
        }
    }
}
