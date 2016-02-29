package com.unseenspace.junit.rule;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.rules.ExternalResource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import static junit.framework.TestCase.fail;

/**
 * Runs a jetty instance with given jndi and jsp/jstl offering
 * <p>
 * Some sensible defaults are added in
 * Created by David Park on 2/22/2016.
 */
public class JettyRule extends ExternalResource {
    private final Server server;
    private InitialContext context;

    public JettyRule() {
        server = new Server(0);
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        server.start();
    }

    @Override
    protected void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.after();
    }

    public String getUrl() {
        return server.getURI().toString();
    }

    public URI getUri() {
        return server.getURI();
    }

    public String getHost() {
        return server.getURI().getHost();
    }

    public int getPort() {
        return server.getURI().getPort();
    }

    @SuppressWarnings("SameParameterValue")
    public static class Builder {
        private JettyRule jettyRule;
        private Configuration.ClassList classList;
        private WebAppContext webapp;

        public Builder() {
            jettyRule = new JettyRule();
            classList = Configuration.ClassList.setServerDefault(jettyRule.server);
        }

        public static String jarPattern(String... regexArray) {
            String pattern = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$";
            for (String regex : regexArray) {
                pattern += "|.*/.*" + regex + ".*\\.jar$";
            }
            return pattern;
        }

        public JettyRule build() {
            jettyRule.server.setHandler(webapp);
            return jettyRule;
        }

        public Builder jndi() {
            classList.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                    "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                    "org.eclipse.jetty.plus.webapp.PlusConfiguration");
            return this;
        }

        public Builder jndi(String string, Object resource) {
            try {
                jettyRule.context.bind(string, resource);
            } catch (NamingException e) {
                e.printStackTrace();
                fail();
            }
            return this;
        }

        public Builder jars(String... regex) {
            ClassLoader classLoader = JettyRule.class.getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader urlLoader = (URLClassLoader) classLoader;
                URL[] urls = urlLoader.getURLs();
                if (urls.length == 1) {
                    String urlString = urls[0].toString();
                    urlString = urlString.substring(urlString.lastIndexOf('/'));
                    if (urlString.startsWith("surefirebooter"))
                        fail("Cannot run surefire tester without setting useManifestOnlyJar to false, ");
                }
            }
            webapp.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", jarPattern(regex));
            return this;
        }

        /**
         * Enables both jsp and jstl
         *
         * @return this for chaining
         */
        public Builder jsp() {
            classList.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration");
            return this;
        }

        /**
         * Maven based project assumed for sensible defaults
         *
         * @return this for chaining
         */
        public Builder webapp() {
            return webapp(false);
        }

        public Builder webapp(boolean isTestPath) {
            return webapp(Class.class.getResource("/.").toString() + "../../src/" + (isTestPath ? "test" : "main") + "/webapp");
        }

        public Builder webapp(String path) {
            webapp = new WebAppContext(path, "");
            try {
                webapp.setClassLoader(new WebAppClassLoader(JettyRule.class.getClassLoader(), webapp));
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }

            // Sensible defaults
            // Remove Directory listing
            webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            // Allow a servlet as a welcome-file
            webapp.setInitParameter("org.eclipse.jetty.servlet.Default.welcomeServlets", "true");
            return this;
        }
    }
}
