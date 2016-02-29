package com.unseenspace.junit.rule;

import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by David Park on 2/28/2016.
 */
public class H2DataSourceRuleTest {
    @ClassRule
    public static H2DataSourceRule dataSourceRule = new H2DataSourceRule();

    @Test
    public void testUrl() throws IOException, SQLException {
        try (Connection connection = dataSourceRule.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE TEST_TABLE");
        }
        try (Connection connection = dataSourceRule.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SELECT * FROM TEST_TABLE");
        }
    }
}
