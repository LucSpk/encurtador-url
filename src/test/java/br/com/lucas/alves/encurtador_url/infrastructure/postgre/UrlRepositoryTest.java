package br.com.lucas.alves.encurtador_url.infrastructure.postgre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lucas.alves.encurtador_url.domain.entity.Url;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToInsertException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.FailToRetrieveGeneratedIdException;
import br.com.lucas.alves.encurtador_url.domain.exceptions.ShortCodeNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UrlRepository")
class UrlRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        urlRepository = new UrlRepository(dataSource);
    }

    @Nested
    @DisplayName("Quando o método getUrlByShortened é executado")
    class GetUrlByShortenedTests {

        private void givenSuccessfulQuery() throws SQLException {
            preparaMocks("SELECT original_url FROM urls WHERE short_code = ?");
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
        }

        @Test
        @DisplayName("Quando o código curto existe, então retorna a URL original")
        void whenShortCodeExists_thenReturnsOriginalUrl() throws SQLException {
            givenSuccessfulQuery();
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("original_url")).thenReturn("https://www.example.com");

            String originalUrl = urlRepository.getUrlByShortened("abc123");

            assertEquals("https://www.example.com", originalUrl);
            verify(preparedStatement).setString(1, "abc123");
        }

        @Test
        @DisplayName("Quando o código curto não existe, então lança ShortCodeNotFoundException")
        void whenShortCodeDoesNotExist_thenThrowsShortCodeNotFoundException() throws SQLException {
            givenSuccessfulQuery();
            when(resultSet.next()).thenReturn(false);

            ShortCodeNotFoundException exception = assertThrows(
                ShortCodeNotFoundException.class,
                () -> urlRepository.getUrlByShortened("not-found")
            );

            assertEquals("URL não encontrada para o código encurtado fornecido.", exception.getMessage());
            verify(preparedStatement).setString(1, "not-found");
        }

        @Test
        @DisplayName("Quando o ocorrer uma SQLException deve lançar RuntimeException ao recuperar URL")
        void whenSQLException_thenThrowRuntimeException() throws SQLException {
            SQLException sqlException = new SQLException("Erro no banco");

            when(dataSource.getConnection()).thenThrow(sqlException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> urlRepository.getUrlByShortened("abc123")
            );

            verificaExeption("Error retrieving URL", sqlException, exception);
        }
    }

    @Nested
    @DisplayName("Quando o método getByUrl é executado")
    class GetByUrlTests {
        private void givenSuccessfulQuery() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("SELECT id, short_code, original_url, created_at FROM urls WHERE original_url = ?"))
                .thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
        }

        @Test 
        @DisplayName("Quando a URL original existe, então retorna o objeto Url")
        void whenOriginalUrlExists_thenReturnsUrlObject() throws SQLException {
            givenSuccessfulQuery();
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getLong("id")).thenReturn(1L);
            when(resultSet.getString("short_code")).thenReturn("abc123");
            when(resultSet.getString("original_url")).thenReturn("https://www.example.com");
            when(resultSet.getString("created_at")).thenReturn("2024-06-01 12:00:00");

            Optional<Url> urlOptional = urlRepository.getByUrl("https://www.example.com");

            assertEquals(true, urlOptional.isPresent());
            Url url = urlOptional.get();
            assertEquals(1L, url.getId());
            assertEquals("abc123", url.getShortCode());
            assertEquals("https://www.example.com", url.getOriginalUrl());
            assertEquals("2024-06-01 12:00:00", url.getCreatedAt());

            verify(preparedStatement).setString(1, "https://www.example.com");
        }

        @Test 
        @DisplayName("Quan a URL original não existe, então retornar nulo") 
        void whenOriginalNotExists_thenReturnsNull() throws SQLException {
            givenSuccessfulQuery();
            when(resultSet.next()).thenReturn(false);

            Optional<Url> urlOptional = urlRepository.getByUrl("https://www.example.com");

            assertEquals(false, urlOptional.isPresent());

            verify(preparedStatement).setString(1, "https://www.example.com");
        }

        @Test
        @DisplayName("Quando o ocorrer uma SQLException deve lançar RuntimeException ao recuperar URL")
        void whenSQLException_thenThrowRuntimeException() throws SQLException{
            SQLException sqlException = new SQLException("Erro no banco");
            when(dataSource.getConnection()).thenThrow(sqlException);

            RuntimeException exception = assertThrows(
                RuntimeException.class, 
                () -> urlRepository.getByUrl("https://www.example.com")
            );

            verificaExeption("Error retrieving URL", sqlException, exception);
        } 
    }

    @Nested 
    @DisplayName("Quando o método saveUrl é executado")
    class SaveUrlTesTs {

        private void givenSuccessfulQuery() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("INSERT INTO urls (short_code, original_url) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(1);
        }

        @Test
        @DisplayName("Quando a URL inserida ainda não existir salva com sucesso e retorna o id")
        void whenUrlNotExists_ThenSaveAndReturnId()throws SQLException {
            givenSuccessfulQuery();
            when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getLong(1)).thenReturn(5L);
            
            long id = urlRepository.saveUrl("https://www.example.com", "abc123");

            assertEquals(5L, id);
            verify(preparedStatement).setString(1, "abc123");
            verify(preparedStatement).setString(2, "https://www.example.com");
        }

        @Test
        @DisplayName("Quando a URL inserida ocorre uma falha ao recuperar o ID, então lança FailToRetrieveGeneratedIdException")
        void whenUrlNotExists_ThenThrowFailToRetrieveIdException()throws SQLException {
            givenSuccessfulQuery();
            when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);
            
            FailToRetrieveGeneratedIdException exception = assertThrows(
                FailToRetrieveGeneratedIdException.class,
                () -> urlRepository.saveUrl("https://www.example.com", "abc123")
            );

            assertEquals("Falha ao recuperar o ID gerado.", exception.getMessage());
            verify(preparedStatement).setString(1, "abc123");
            verify(preparedStatement).setString(2, "https://www.example.com");
        }

        @Test
        @DisplayName("Quando a URL inserida ocorre uma falha ao inserir, então lança FailToInsertException")
        void whenUrlNotExists_ThenThrowFailToInsertException()throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("INSERT INTO urls (short_code, original_url) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
            when(preparedStatement.executeUpdate()).thenReturn(0);
            
            FailToInsertException exception = assertThrows(
                FailToInsertException.class,
                () -> urlRepository.saveUrl("https://www.example.com", "abc123")
            );

            assertEquals("Falha ao inserir URL no banco de dados.", exception.getMessage());
            verify(preparedStatement).setString(1, "abc123");
            verify(preparedStatement).setString(2, "https://www.example.com");
        }

        @Test
        @DisplayName("Quando o ocorrer uma SQLException deve lançar RuntimeException ao recuperar URL")
        void whenSQLException_thenThrowRuntimeException() throws SQLException {
            SQLException sqlException = new SQLException("Erro no banco");

            when(dataSource.getConnection()).thenThrow(sqlException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> urlRepository.saveUrl("https://www.example.com", "abc123")
            );

            verificaExeption("Error saving URL", sqlException, exception);
        }
    }

    @Nested
    @DisplayName("Quando o método updateUrlShortCode é executado") 
    class UpdateUrlShortCodeTests {

        private void givenSuccessfulQuery() throws SQLException {
            preparaMocks("UPDATE urls SET short_code = ? WHERE id = ?");
        }

        @Test
        @DisplayName("Quando o código curto é atualizado com sucesso")
        void whenUpdateShortCode_thenSuccess() throws SQLException {
            givenSuccessfulQuery();
            urlRepository.updateUrlShortCode(1L, "newCode");

            verify(preparedStatement).setString(1, "newCode");
            verify(preparedStatement).setLong(2, 1L);
        }

        @Test
        @DisplayName("Quando o ocorrer uma SQLException deve lançar RuntimeException ao recuperar URL")
        void whenSQLException_thenThrowRuntimeException() throws SQLException {
            SQLException sqlException = new SQLException("Erro no banco");

            when(dataSource.getConnection()).thenThrow(sqlException);

            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> urlRepository.updateUrlShortCode(1L, "newCode")
            );
            
            verificaExeption("Error updating URL short code", sqlException, exception);
        }
    }

    private void verificaExeption(String mensagemEsperada, SQLException sqlException, RuntimeException exception) throws SQLException {
        assertEquals(mensagemEsperada, exception.getMessage());
        assertEquals(sqlException, exception.getCause());

        verify(dataSource).getConnection();
    }

    private void preparaMocks(String query) throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
    }
}
