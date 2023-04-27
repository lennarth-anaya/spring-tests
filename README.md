
# Spring Automated Tests

## SecuredControllerTest

Shows how to use @SpringBootTest with Spring Security to verify that endpoints are
properly non-secured, secured by any authenticated user or even by an specific role
per HTTP method.

## HttpIntegrationBaseTest

Showcases the use of mock-server and htmlunit libraries for doing HTTP integration tests.

In most of the cases, MockMvc and other Spring mocking utilities should suffice for most
integration testing purposes, but there are a few cases in which a real HTTP connection
would be necessary for such integration testing:

1. When you want to validate your endpoint's parameters are properly defined so Spring can
  parse HTTP query parameters, for instance: optionals, composite object, lists of values,
  etc.
  
2. When you want to validate HTTP headers, like 302 redirection, tested in this project by
  Test302RedirectIntegrationTest, which extends HttpIntegrationBaseTest to reuse repetitive 
  setup code.
  
  Test302RedirectIntegrationTest -- calls --> HttpRedirectionController -- responds -->
    HTTP 302 -- to --> remote host mocked by server-mock
  
3. When you want to validate HTML default navigation that does not require a user interaction,
  which will behave like an HTTP redirect but instead of an HTTP header, it would be JavaScript
  code. This scenario is a bad practice but I've seen it in real life and needed to test it.
  htmlunit library evaluates the response's HTML with JavaScript gotten, so it will automatically
  perform the redirect behind the scenes in the automated test, allowing in combination of
  mock-server to inspect the navigation.
  
  This is tested in this project by TestJavaScriptRedirectIntegrationTest, which extends 
  HttpIntegrationBaseTest to reuse repetitive setup code.
  
  TestJavaScriptRedirectIntegrationTest -- calls --> JavaScriptRedirectionController -- responds -->
    HTML with JavaScript that auto-submits form -- to --> remote host mocked by server-mock

## Test Containers

Includes a simple test that uses Testcontainers with a MariaDB database to test transactional commits and rollbacks.
