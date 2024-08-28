package my.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import my.test.dto.TaskCreateUpdateDTO;
import my.test.dto.TaskDTO;
import my.test.mapper.TaskMapper;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.model.User;
import my.test.service.TagService;
import my.test.service.TaskService;
import my.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Task Title");
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Task Title");
        List<Task> tasks = Arrays.asList(task);
        List<TaskDTO> taskDTOs = Arrays.asList(taskDTO);

        when(taskService.findAll()).thenReturn(tasks);
        when(taskMapper.toDtoAll(tasks)).thenReturn(taskDTOs);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Task Title"));

        verify(taskService, times(1)).findAll();
        verify(taskMapper, times(1)).toDtoAll(tasks);
    }

    @Test
    void testGetTaskById() throws Exception {
        Task task = new Task();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Sample Title");

        when(taskService.findById(anyLong())).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Sample Title"));

        verify(taskService, times(1)).findById(anyLong());
        verify(taskMapper, times(1)).toDto(task);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskCreateUpdateDTO taskCreateUpdateDTO = new TaskCreateUpdateDTO();
        taskCreateUpdateDTO.setTitle("New Task");
        Task task = new Task();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("New Task");

        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);
        when(taskService.create(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskCreateUpdateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Task"));

        verify(taskService, times(1)).create(any(Task.class));
        verify(taskMapper, times(1)).toDto(any(Task.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        TaskCreateUpdateDTO taskCreateUpdateDTO = new TaskCreateUpdateDTO();
        taskCreateUpdateDTO.setTitle("Updated Task");
        Task existingTask = new Task();
        Task updatedTask = new Task();
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task");

        when(taskService.findById(anyLong())).thenReturn(existingTask);
        when(taskService.update(anyLong(), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(taskCreateUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Task"));

        verify(taskService, times(1)).findById(anyLong());
        verify(taskService, times(1)).update(anyLong(), any(Task.class));
        verify(taskMapper, times(1)).toDto(updatedTask);
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).delete(anyLong());

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).delete(anyLong());
    }

    @Test
    void testAssignUserAndTagToTask() throws Exception {
        Task task = new Task();
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Task with User and Tag");

        when(taskService.findById(anyLong())).thenReturn(task);
        when(taskService.assignUserToTask(any(Task.class), anyLong())).thenReturn(task);
        when(taskService.assignTagToTask(any(Task.class), anyLong())).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        mockMvc.perform(put("/tasks/1/assign")
                        .param("user", "1")
                        .param("tag", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Task with User and Tag"));

        verify(taskService, times(1)).findById(anyLong());
        verify(taskService, times(1)).assignUserToTask(any(Task.class), anyLong());
        verify(taskService, times(1)).assignTagToTask(any(Task.class), anyLong());
        verify(taskMapper, times(1)).toDto(task);
    }

    @Test
    void testCreateTaskWithUserAndTags() throws Exception {
        Long userId = 1L;
        List<Long> tagIds = Arrays.asList(2L, 3L);

        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Task Title");
        dto.setDescription("Task Description");
        dto.setUserId(userId);
        dto.setTagIds(tagIds);

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        Tag tag1 = new Tag();
        tag1.setId(2L);
        tag1.setTitle("Tag1");

        Tag tag2 = new Tag();
        tag2.setId(3L);
        tag2.setTitle("Tag2");

        List<Tag> tags = Arrays.asList(tag1, tag2);

        Task task = new Task();
        task.setTitle("Task Title");
        task.setDescription("Task Description");

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Task Title");

        when(userService.findById(userId)).thenReturn(user);
        when(tagService.findByIds(tagIds)).thenReturn(tags);
        when(taskService.create(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task Title"));

        verify(userService, times(1)).findById(userId);
        verify(tagService, times(1)).findByIds(tagIds);
    }

    @Test
    void testCreateTaskWithoutUserAndTags() throws Exception {
        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Task Title");
        dto.setDescription("Task Description");

        Task task = new Task();
        task.setTitle("Task Title");
        task.setDescription("Task Description");

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Task Title");

        when(taskService.create(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task Title"));

        verify(userService, never()).findById(anyLong());
        verify(tagService, never()).findByIds(anyList());
    }

    @Test
    void testUpdateTaskWithUserAndTags() throws Exception {
        Long id = 1L;
        Long userId = 2L;
        List<Long> tagIds = Arrays.asList(3L, 4L);

        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Updated Task Title");
        dto.setDescription("Updated Task Description");
        dto.setUserId(userId);
        dto.setTagIds(tagIds);

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");

        Tag tag1 = new Tag();
        tag1.setId(3L);
        tag1.setTitle("Tag3");

        Tag tag2 = new Tag();
        tag2.setId(4L);
        tag2.setTitle("Tag4");

        List<Tag> tags = Arrays.asList(tag1, tag2);

        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setUser(user);
        updatedTask.setTags(tags);

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task Title");

        when(taskService.findById(id)).thenReturn(existingTask);
        when(userService.findById(userId)).thenReturn(user);
        when(tagService.findByIds(tagIds)).thenReturn(tags);
        when(taskService.update(eq(id), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"));

        verify(taskService, times(1)).findById(id);
        verify(userService, times(1)).findById(userId);
        verify(tagService, times(1)).findByIds(tagIds);
        verify(taskService, times(1)).update(eq(id), any(Task.class));
    }

    @Test
    void testUpdateTaskWithoutUserAndTags() throws Exception {
        Long id = 1L;

        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Updated Task Title");
        dto.setDescription("Updated Task Description");

        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated Task Description");

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task Title");

        when(taskService.findById(id)).thenReturn(existingTask);
        when(taskService.update(eq(id), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"));

        verify(taskService, times(1)).findById(id);
        verify(userService, never()).findById(anyLong());  
        verify(tagService, never()).findByIds(anyList());  
        verify(taskService, times(1)).update(eq(id), any(Task.class));
    }

    @Test
    void testUpdateTaskWithUserRemoved() throws Exception {
        Long id = 1L;

        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Updated Task Title");
        dto.setDescription("Updated Task Description");
        dto.setUserId(null);  

        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setUser(new User());  

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setUser(null);  

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task Title");

        when(taskService.findById(id)).thenReturn(existingTask);
        when(taskService.update(eq(id), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"));

        verify(taskService, times(1)).findById(id);
        verify(userService, never()).findById(anyLong());  
        verify(tagService, never()).findByIds(anyList());  
        verify(taskService, times(1)).update(eq(id), any(Task.class));
    }

    @Test
    void testUpdateTaskWithTagsRemoved() throws Exception {
        Long id = 1L;

        TaskCreateUpdateDTO dto = new TaskCreateUpdateDTO();
        dto.setTitle("Updated Task Title");
        dto.setDescription("Updated Task Description");
        dto.setTagIds(new ArrayList<>());  

        Task existingTask = new Task();
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setTags(Arrays.asList(new Tag(), new Tag()));  

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated Task Description");
        updatedTask.setTags(new ArrayList<>());  

        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setTitle("Updated Task Title");

        when(taskService.findById(id)).thenReturn(existingTask);
        when(taskService.update(eq(id), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updatedTaskDTO);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"));

        verify(taskService, times(1)).findById(id);
        verify(tagService, never()).findByIds(anyList());  
        verify(taskService, times(1)).update(eq(id), any(Task.class));
    }

    @Test
    void testHandleEntityNotFoundException() throws Exception {
        ResponseEntity<Object> responseEntity = taskController.handleEntityNotFoundException(new EntityNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testHandleDataIntegrityViolationException() throws Exception {
        ResponseEntity<Object> responseEntity = taskController.handleDataIntegrityViolationException(new DataIntegrityViolationException(""));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testHandleOtherException() throws Exception {
        ResponseEntity<Object> responseEntity = taskController.handleOtherException(new Exception());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
