package com.unseenspace.junit.rule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by David Park on 2/28/2016.
 */
public class JettyRuleTest {
    public static Connection connection = mock(Connection.class);

    public static DataSource dataSource = mock(DataSource.class);

    @ClassRule
    public static JettyRule jettyRule = JettyRule.builder()
            .webapp(true)
            .jars()
            .jndi().jndi("dataSource", dataSource)
            .build();

    @BeforeClass
    public static void beforeClass() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        verify(dataSource).getConnection();
        verify(connection).close();
    }

    @Test
    public void testJndi() throws NamingException {
        Context context = new InitialContext();

        assertThat(context.lookup("dataSource"), notNullValue());
    }

    @Test
    public void testUrl() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) jettyRule.getUri().toURL().openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            assertThat(reader.readLine(), is("Hello World!"));
        }
        connection.disconnect();
    }
}
