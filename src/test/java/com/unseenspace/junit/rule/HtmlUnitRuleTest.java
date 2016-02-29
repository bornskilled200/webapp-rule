package com.unseenspace.junit.rule;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by David Park on 2/28/2016.
 */
public class HtmlUnitRuleTest {
    @Rule
    public HtmlUnitRule htmlUnitRule = new HtmlUnitRule();

    @Test
    public void testUrl() throws IOException, SQLException {
        try (WebClient client = htmlUnitRule.getWebClient()) {
            client.getPage("https://www.google.com");
        }
    }
}
