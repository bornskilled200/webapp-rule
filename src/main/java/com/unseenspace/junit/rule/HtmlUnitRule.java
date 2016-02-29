package com.unseenspace.junit.rule;

import com.gargoylesoftware.htmlunit.DefaultCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.rules.ExternalResource;
import org.w3c.css.sac.CSSParseException;

/**
 * Default rule for an making a WebClient from HtmlUnit
 * Created by David Park on 2/22/2016.
 */
public class HtmlUnitRule extends ExternalResource {
    private WebClient webClient;

    public static WebClient createWebClient() {
        WebClient webClient = new WebClient();
        webClient.setCssErrorHandler(new LooseCssErrorHandler());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        return webClient;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        webClient = createWebClient();
    }

    public WebClient getWebClient() {
        return webClient;
    }

    @Override
    protected void after() {
        webClient.close();
        super.after();
    }

    /**
     * Make it so HtmlUnit ignores common false positives
     * Created by David Park on 2/9/2016.
     */
    public static class LooseCssErrorHandler extends DefaultCssErrorHandler {

        @Override
        public void error(CSSParseException exception) {
            if (isSeriousError(exception))
                super.error(exception);
        }

        private boolean isSeriousError(CSSParseException exception) {
            return !exception.getURI().contains("bootstrap.min.css") &&
                    !exception.getURI().contains("bootstrap-theme.min.css") &&
                    !exception.getURI().contains("bootstrap-drawer.min.css") &&
                    !exception.getURI().contains("bootstrap-datetimepicker.min.css") &&
                    !exception.getURI().contains("font-awesome.min.css") &&
                    !exception.getURI().contains("dataTables.bootstrap.css");
        }

        @Override
        public void fatalError(CSSParseException exception) {
            if (isSeriousError(exception))
                super.fatalError(exception);
        }

        @Override
        public void warning(CSSParseException exception) {
            if (isSeriousError(exception))
                super.warning(exception);
        }
    }
}
