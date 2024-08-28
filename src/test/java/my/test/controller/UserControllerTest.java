package my.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import my.test.dto.UserDTOWithTasks;
import my.test.mapper.UserMapper;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User();
        user.setName("Username");
        UserDTOWithTasks userDTOWithTasks = new UserDTOWithTasks();
        userDTOWithTasks.setName("Username");
        List<User> users = Arrays.asList(user);
        List<UserDTOWithTasks> userDTOWithTasksList = Arrays.asList(userDTOWithTasks);

        when(userService.findAll()).thenReturn(users);
        when(userMapper.toDtoWithTasks(user)).thenReturn(userDTOWithTasks);  

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Username"));

        verify(userService, times(1)).findAll();
        verify(userMapper, times(1)).toDtoWithTasks(users.get(0));  
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        UserDTOWithTasks userDTOWithTasks = new UserDTOWithTasks();
        userDTOWithTasks.setName("Username");

        when(userService.findById(anyLong())).thenReturn(user);
        when(userMapper.toDtoWithTasks(user)).thenReturn(userDTOWithTasks);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Username"));

        verify(userService, times(1)).findById(anyLong());
        verify(userMapper, times(1)).toDtoWithTasks(user);
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTOWithTasks userDTOWithTasks = new UserDTOWithTasks();
        userDTOWithTasks.setName("New User");

        User user = new User();
        user.setName("New User");

        when(userMapper.toEntity(any(UserDTOWithTasks.class))).thenReturn(user);
        when(userMapper.toDtoWithTasks(any(User.class))).thenReturn(userDTOWithTasks);
        when(userService.create(any(User.class))).thenReturn(user);  

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTOWithTasks)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New User"));

        verify(userMapper, times(1)).toEntity(any(UserDTOWithTasks.class));
        verify(userService, times(1)).create(any(User.class));
        verify(userMapper, times(1)).toDtoWithTasks(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        Long id = 1L;
        UserDTOWithTasks userDTOWithTasks = new UserDTOWithTasks();
        userDTOWithTasks.setName("Updated User");

        User user = new User();
        user.setName("Updated User");

        when(userMapper.toEntity(any(UserDTOWithTasks.class))).thenReturn(user);
        when(userService.update(eq(id), any(User.class))).thenReturn(user);
        when(userMapper.toDtoWithTasks(any(User.class))).thenReturn(userDTOWithTasks);

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTOWithTasks)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated User"));

        verify(userMapper, times(1)).toEntity(any(UserDTOWithTasks.class));
        verify(userService, times(1)).update(eq(id), any(User.class));
        verify(userMapper, times(1)).toDtoWithTasks(any(User.class));
    }

    @Test
    void testDeleteTag() throws Exception {
        Long id = 1L;

        doNothing().when(userService).delete(id);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(id);
    }

    @Test
    void testHandleEntityNotFoundException() throws Exception {
        ResponseEntity<Object> responseEntity = userController.handleEntityNotFoundException(new EntityNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testHandleDataIntegrityViolationException() throws Exception {
        ResponseEntity<Object> responseEntity = userController.handleDataIntegrityViolationException(new DataIntegrityViolationException(""));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testHandleOtherException() throws Exception {
        ResponseEntity<Object> responseEntity = userController.handleOtherException(new Exception());
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
