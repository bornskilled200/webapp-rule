package com.unseenspace.junit.rule;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.rules.ExternalResource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Making a datasource form H2 in memory database
 * Created by David Park on 2/22/2016.
 */
public class H2DataSourceRule extends ExternalResource {

    private JdbcDataSource dataSource;
    private Connection connection;

    /**
     * Makes more sense to have a default where multiple connections can connect be the same database
     */
    public H2DataSourceRule() {
        this("test");
    }

    public H2DataSourceRule(String name) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + name);
    }

    public JdbcDataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        // Hold a connection to make sure database does not close
        connection = dataSource.getConnection();
    }

    @Override
    protected void after() {
        try {
            connection.close();
        } catch (SQLException e) {
            // Don't really worry about an exception here
        }
        //H2 closes the datasource on the termination of the jvm
        super.after();
    }

    /**
     * Convenience method for getDataSource().getConnection();
     *
     * @return a new connection
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
