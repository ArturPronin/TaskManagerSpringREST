package my.test.service;

import jakarta.persistence.EntityNotFoundException;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.model.User;
import my.test.repository.TagRepository;
import my.test.repository.TaskRepository;
import my.test.repository.UserRepository;
import my.test.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Task task1 = new Task();
        Task task2 = new Task();
        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.findAll();
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(1L);
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.findById(1L));
    }

    @Test
    void testCreate() {
        Task task = new Task();
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.create(task);
        assertNotNull(result);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdate() {
        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("New Description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.update(1L, updatedTask);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testDelete() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testAssignUserToTask() {
        Task task = new Task();
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.assignUserToTask(task, 1L);
        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testAssignTagToTask() {
        Task task = new Task();
        Tag tag = new Tag();
        tag.setId(1L);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.assignTagToTask(task, 1L);
        assertNotNull(result);
        assertTrue(result.getTags().contains(tag));
        verify(tagRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testDeleteTaskWithUser() {
        
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setTasks(new ArrayList<>());
        task.setUser(user);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        user.setTasks(tasks);
        
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        taskService.delete(1L);
        
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).delete(task);
        
        assertTrue(user.getTasks().isEmpty(), "Task should be removed from user's tasks list");
    }

    @Test
    void testDeleteTaskWithTags() {
        
        Task task = new Task();
        task.setId(1L);
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tag1.setTasks(new ArrayList<>());
        tag2.setTasks(new ArrayList<>());
        task.setTags(List.of(tag1, tag2));

        
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        
        taskService.delete(1L);

        
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).delete(task);

        
        assertTrue(tag1.getTasks().isEmpty(), "Task should be removed from tag's tasks list");
        assertTrue(tag2.getTasks().isEmpty(), "Task should be removed from tag's tasks list");
    }

    @Test
    void testDeleteTaskWhenTaskNotFound() {
        
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        
        assertThrows(EntityNotFoundException.class, () -> taskService.delete(1L));

        
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, never()).delete(any(Task.class));
    }
}