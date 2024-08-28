package my.test.service;

import jakarta.persistence.EntityNotFoundException;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.repository.TagRepository;
import my.test.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.findAll();
        assertEquals(2, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Tag tag = new Tag();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag result = tagService.findById(1L);
        assertNotNull(result);
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.findById(1L));
    }

    @Test
    void testCreate() {
        Tag tag = new Tag();
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.create(tag);
        assertNotNull(result);
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void testUpdate() {
        Tag updatedTag = new Tag();
        updatedTag.setTitle("Updated Title");

        when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        Tag result = tagService.update(1L, updatedTag);
        assertEquals("Updated Title", result.getTitle());
        verify(tagRepository, times(1)).save(updatedTag);
    }

    @Test
    void testDelete() {
        Tag tag = new Tag();
        tag.setId(1L);
        Task task = new Task();
        task.getTags().add(tag);
        tag.setTasks(Arrays.asList(task));

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        tagService.delete(1L);

        verify(tagRepository, times(1)).delete(tag);
        assertFalse(task.getTags().contains(tag));
    }

    @Test
    void testFindByIds() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        List<Long> ids = Arrays.asList(1L, 2L);

        when(tagRepository.findAllById(ids)).thenReturn(Arrays.asList(tag1, tag2));

        List<Tag> result = tagService.findByIds(ids);
        assertEquals(1, result.size());  
        verify(tagRepository, times(1)).findAllById(ids);
    }
}
