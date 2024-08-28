package my.test.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConfigurationTest {

    @Test
    void testDatabaseConfig() {
        DatabaseConfig dbConfig = new DatabaseConfig(new StandardEnvironment());
        assertThrows(IllegalStateException.class, () -> dbConfig.dataSource());
        assertThrows(Exception.class, () -> dbConfig.liquibase(null));
    }

    @Test
    void testDispatcherInit() {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ServletRegistration.Dynamic dynamic = Mockito.mock(ServletRegistration.Dynamic.class);
        when(servletContext.addServlet(eq("dispatcher"), any(DispatcherServlet.class)))
                .thenReturn(dynamic);

        DispatcherServletInitializer initializer = new DispatcherServletInitializer();
        initializer.onStartup(servletContext);

        verify(servletContext).addServlet(eq("dispatcher"), any(DispatcherServlet.class));
        verify(dynamic).setLoadOnStartup(1);
        verify(dynamic).addMapping("/");
    }

    @Test
    void testMvcConfig() {
        MvcConfig mvcConfig = new MvcConfig();
        assertNotNull(mvcConfig);
    }
}
