package my.test.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;


public class DbInitializer {

    public DbInitializer() {
    }

    public static PostgreSQLContainer buildMyPostgreSQLContainer() {
        return new PostgreSQLContainer("postgres:16")
                .withDatabaseName("taskmanagerdb")
                .withUsername("postgres")
                .withPassword("1234");
    }

    public static void initDbWithProperties(
            DynamicPropertyRegistry registry, PostgreSQLContainer postgreSQLContainer) {
        registry.add("database.url", postgreSQLContainer::getJdbcUrl);
        registry.add("database.username", postgreSQLContainer::getUsername);
        registry.add("database.password", postgreSQLContainer::getPassword);
    }
}
