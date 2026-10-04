package br.com.lucas.alves.encurtador_url.infrastructure.postgre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import br.com.lucas.alves.encurtador_url.application.ports.output.IUrlOutputPort;
import br.com.lucas.alves.encurtador_url.domain.entity.Url;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToInsertException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToRetrieveGeneratedIdException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToUpdateException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;

@Repository
public class UrlRepository implements IUrlOutputPort {
    private static final String SELECT_URL_BY_ORIGINAL_URL = "SELECT id, short_code, original_url, created_at, expires_at, ttl_minutes FROM urls WHERE original_url = ?";
    private static final String UPDATE_URL_SET_SHORT_CODE_WHERE_ID_QUERY = "UPDATE urls SET short_code = ? WHERE id = ?";
    private static final String INSERT_URL_QUERY = "INSERT INTO urls (short_code, original_url) VALUES (?, ?)";
    private static final String SELECT_URL_QUERY = "SELECT original_url FROM urls WHERE short_code = ?";
    private static final String INSERT_URL_WITH_TTL_QUERY = "INSERT INTO urls (short_code, original_url, expires_at, ttl_minutes) VALUES (?, ?, ?, ?)";
    private static final String SELECT_URL_WITH_EXPIRY = "SELECT original_url, expires_at FROM urls WHERE short_code = ?";

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
                    url.setExpiresAt(rs.getLong("expires_at"));
                    url.setTtlMinutes(rs.getInt("ttl_minutes"));
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
            if (e instanceof PSQLException pgException
                    && "23505".equals(pgException.getSQLState())
                    && pgException.getServerErrorMessage() != null
                    && "urls_original_url_unique".equals(
                            pgException.getServerErrorMessage().getConstraint())) {
                throw new DuplicateKeyException("Esta URL já está cadastrada.");
            }

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

    @Override
    public long saveUrlWithTtl(String original, String shortened, Integer ttlMinutes) {
        try (Connection connection = dataSource.getConnection(); 
            PreparedStatement ps = connection.prepareStatement(INSERT_URL_WITH_TTL_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, shortened);
            ps.setString(2, original);
            
            if (ttlMinutes != null && ttlMinutes > 0) {
                long expiresAt = System.currentTimeMillis() + (ttlMinutes * 60 * 1000L);
                ps.setLong(3, expiresAt);
                ps.setInt(4, ttlMinutes);
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            int result = ps.executeUpdate();

            if (result == 0) {
                throw new FailToInsertException("Falha ao inserir URL no banco de dados.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    LOGGER.info("URL salva com sucesso com id: {} e TTL: {} minutos", id, ttlMinutes);
                    return id;
                }
            }
            
            throw new FailToRetrieveGeneratedIdException("Falha ao recuperar ID gerado.");
        } catch (SQLException e) {
            if (e instanceof PSQLException pgException
                    && "23505".equals(pgException.getSQLState())
                    && pgException.getServerErrorMessage() != null
                    && "urls_original_url_unique".equals(
                            pgException.getServerErrorMessage().getConstraint())) {
                throw new DuplicateKeyException("Esta URL já está cadastrada.");
            }
            
            LOGGER.error("Error saving URL: {}", e.getMessage());
            throw new RuntimeException("Error saving URL", e);
        }
    }

    @Override
    public String getUrlByShortCodeWithExpiryCheck(String shortCode) {
       try (Connection connection = dataSource.getConnection(); 
             PreparedStatement ps = connection.prepareStatement(SELECT_URL_WITH_EXPIRY)) {
            
            ps.setString(1, shortCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String originalUrl = rs.getString("original_url");
                    Long expiresAt = rs.getLong("expires_at");
                    
                    // Verificar se a URL expirou
                    if (expiresAt > 0 && System.currentTimeMillis() > expiresAt) {
                        LOGGER.warn("URL com código {} expirou em: {}", shortCode, new java.util.Date(expiresAt));
                        deleteExpiredUrl(shortCode); // 🆕 Deletar URL expirada (opcional)
                        throw new ShortCodeNotFoundException("URL expirada para o código encurtado fornecido.");
                    }
                    
                    LOGGER.info("URL found and valid: {}", originalUrl);
                    return originalUrl;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving URL with expiry check: {}", e.getMessage());
            throw new RuntimeException("Error retrieving URL with expiry check", e);
        }
        
        throw new ShortCodeNotFoundException("URL não encontrada para o código encurtado fornecido.");
    }

    private void deleteExpiredUrl(String shortCode) {
        try (Connection connection = dataSource.getConnection(); 
             PreparedStatement ps = connection.prepareStatement("DELETE FROM urls WHERE short_code = ?")) {
            
            ps.setString(1, shortCode);
            ps.executeUpdate();
            LOGGER.info("URL expirada deletada: {}", shortCode);
        } catch (SQLException e) {
            LOGGER.warn("Erro ao deletar URL expirada {}: {}", shortCode, e.getMessage());
            // Não lançar exceção aqui, é apenas uma limpeza
        }
    }
}
