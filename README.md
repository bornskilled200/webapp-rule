# webapp-rule

Simple rules to reduce boilerplate when integration testing for web apps.

```java
public class IntegrationTest {
  public static H2DataSourceRule dataSourceRule = new H2DataSourceRule("test");

  public static JettyRule jettyRule = JettyRule.builder()
          .webapp()
          .jars("shiro-web", "struts")
          .jsp()
          .jndi()
          .jndi("hospital", dataSourceRule.getDataSource())
          .jndi("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
          .jndi("hibernate.hbm2ddl.auto", "update")
          .build();

  @ClassRule
  public static TestRule testRule = RuleChain.outerRule(dataSourceRule).around(jettyRule);
  
  @Rule
  public HtmlUnitRule htmlUnitRule = new HtmlUnitRule();
  
  @Test
  public void testConnection() {
    assertThat(htmlUnitRule.getWebClient().getPage(jettyRule.getUrl()).getWebResponse().getStatusCode(), is(200)));
  }
}
```
