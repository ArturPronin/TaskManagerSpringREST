package my.test.repository;

import my.test.config.DatabaseConfig;
import my.test.config.DbInitializer;
import my.test.model.Tag;
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
public class TagRepositoryTest {
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
    static void setUp() {postgreSQLContainer.start();}

    @AfterAll
    static void tearDown() {postgreSQLContainer.stop();}

    @Test
    void findAllTest() {
        List<Tag> tags = tagRepository.findAll();
        assertTrue(tags.size() >= 4);
    }

    @Test
    void findByIdTest() {
        assertEquals(1L, tagRepository.findById(1L).get().getId());
    }

    @Test
    void saveTest() {
        Tag tag = new Tag();
        tag.setTitle("Title Tag");
        tag.setTasks(null);

        Tag savedTags = tagRepository.save(tag);

        assertEquals(tag.getTitle(), savedTags.getTitle());
        assertNull(tag.getTasks());
    }

    @Test
    void updateTest() {
        Tag tag = tagRepository.findById(1L).get();
        tag.setTitle("test Tag");

        tagRepository.save(tag);
        Tag tag1 = tagRepository.findById(1L).get();
        assertEquals("test Tag", tag1.getTitle());
    }

    @Test
    void deleteTest() {
        // Создаем и сохраняем тег
        Tag tag = new Tag();
        tag.setTitle("Title");
        Tag savedTag = tagRepository.save(tag);

        // Убеждаемся, что тег сохранен
        assertTrue(tagRepository.findById(savedTag.getId()).isPresent());

        // Удаляем тег
        tagRepository.deleteById(savedTag.getId());

        // Проверяем, что тег действительно удален
        assertTrue(tagRepository.findById(savedTag.getId()).isEmpty());
    }

}
