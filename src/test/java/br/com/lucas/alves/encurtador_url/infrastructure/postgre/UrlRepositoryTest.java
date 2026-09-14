package br.com.lucas.alves.encurtador_url.infrastructure.postgre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        @BeforeEach
        void setUp() throws SQLException {
            when(dataSource.getConnection()).thenReturn(connection);
            when(connection.prepareStatement("SELECT original_url FROM urls WHERE short_code = ?"))
                .thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
        }

        @Test
        @DisplayName("Quando o código curto existe, então retorna a URL original")
        void whenShortCodeExists_thenReturnsOriginalUrl() throws SQLException {
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getString("original_url")).thenReturn("https://www.example.com");

            String originalUrl = urlRepository.getUrlByShortened("abc123");

            assertEquals("https://www.example.com", originalUrl);
            verify(preparedStatement).setString(1, "abc123");
        }

        @Test
        @DisplayName("Quando o código curto não existe, então lança ShortCodeNotFoundException")
        void whenShortCodeDoesNotExist_thenThrowsShortCodeNotFoundException() throws SQLException {
            when(resultSet.next()).thenReturn(false);

            ShortCodeNotFoundException exception = assertThrows(
                ShortCodeNotFoundException.class,
                () -> urlRepository.getUrlByShortened("not-found")
            );

            assertEquals("URL não encontrada para o código encurtado fornecido.", exception.getMessage());
            verify(preparedStatement).setString(1, "not-found");
        }
    }
}
