package my.test.repository;

import my.test.config.DatabaseConfig;
import my.test.config.DbInitializer;
import my.test.model.Task;
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
public class TaskRepositoryTest {
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
        List<Task> tasks = taskRepository.findAll();
        assertTrue(tasks.size() >= 4);
    }

    @Test
    void findByIdTest() {
        assertEquals(1L, taskRepository.findById(1L).get().getId());
    }

    @Test
    void saveTest() {
        Task task = new Task();
        task.setTitle("Title");
        task.setDescription("Description");
        task.setUser(null);
        task.setTags(null);

        Task savedTask = taskRepository.save(task);

        assertEquals(task.getTitle(), savedTask.getTitle());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertNull(savedTask.getUser());
        assertNull(savedTask.getTags());
    }

    @Test
    void updateTest() {
        Task task = taskRepository.findById(1L).get();
        task.setTitle("test");

        taskRepository.save(task);
        Task task1 = taskRepository.findById(1L).get();
        assertEquals("test", task1.getTitle());
    }

    @Test
    void deleteTest() {
        Task task = new Task();
        task.setId(1L);  
        task.setTitle("Title");
        task.setDescription("Description");
        taskRepository.save(task);

        taskRepository.deleteById(1L);

        assertTrue(taskRepository.findById(1L).isEmpty());
    }

}
