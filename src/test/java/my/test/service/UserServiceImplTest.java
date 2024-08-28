package my.test.service;

import jakarta.persistence.EntityNotFoundException;
import my.test.model.Task;
import my.test.model.User;
import my.test.repository.UserRepository;
import my.test.service.impl.UserServiceImpl;
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

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testCreate() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);
        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdate() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.update(1L, updatedUser);
        assertEquals("Updated Name", result.getName());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setId(1L);
        Task task = new Task();
        task.setUser(user);
        user.setTasks(Arrays.asList(task));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
        assertNull(task.getUser());
    }
}
