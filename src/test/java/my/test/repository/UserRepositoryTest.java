package my.test.repository;

import my.test.config.DatabaseConfig;
import my.test.config.DbInitializer;
import my.test.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DatabaseConfig.class)
public class UserRepositoryTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TagRepository tagRepository;
    public static PostgreSQLContainer postgreSQLContainer = DbInitializer.buildMyPostgreSQLContainer();

    @DynamicPropertySource
    static void initDbWithProperties(DynamicPropertyRegistry registry) {
        DbInitializer.initDbWithProperties(registry, postgreSQLContainer);
    }

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    void findAllTest() {
        List<User> users = userRepository.findAll();
        assertTrue(users.size() >= 4);
    }

    @Test
    void findByIdTest() {
        assertEquals(1L, userRepository.findById(1L).get().getId());
    }

    @Test
    void saveTest() {
        User user = new User();
        user.setName("Username");
        user.setTasks(null);

        User savedUsers = userRepository.save(user);

        assertEquals(user.getName(), savedUsers.getName());
        assertNull(user.getTasks());
    }

    @Test
    void updateTest() {
        User user = userRepository.findById(1L).get();
        user.setName("test Username");

        userRepository.save(user);
        User user1 = userRepository.findById(1L).get();
        assertEquals("test Username", user1.getName());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setName("Username");
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.findById(savedUser.getId()).isPresent());

        userRepository.deleteById(savedUser.getId());

        assertTrue(userRepository.findById(savedUser.getId()).isEmpty());
    }

}
