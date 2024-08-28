package my.test.mapper;

import my.test.dto.TaskSimpleDTO;
import my.test.dto.UserDTO;
import my.test.dto.UserDTOWithTasks;
import my.test.model.Task;
import my.test.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private TaskMapper taskMapper;
    private User user;
    private UserDTO userDTO;
    private UserDTOWithTasks userDTOWithTasks;
    private List<User> userList;
    private List<UserDTO> userDTOList;
    private List<UserDTOWithTasks> userDTOWithTasksList;
    private List<Task> taskList;
    private List<TaskSimpleDTO> taskSimpeDTOList;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");
        task.setDescription("Description 1");

        taskList = Arrays.asList(task);
        taskSimpeDTOList = Arrays.asList(new TaskSimpleDTO());

        user = new User();
        user.setId(1L);
        user.setName("User1");
        user.setTasks(taskList);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("User1");

        userDTOWithTasks = new UserDTOWithTasks();
        userDTOWithTasks.setId(1L);
        userDTOWithTasks.setName("User1");
        userDTOWithTasks.setTasks(taskSimpeDTOList);

        userList = Arrays.asList(user);
        userDTOList = Arrays.asList(userDTO);
        userDTOWithTasksList = Arrays.asList(userDTOWithTasks);
    }

    @Test
    void toDtoWithTasks() {
        when(taskMapper.toSimpleDtoAll(taskList)).thenReturn(taskSimpeDTOList);
        UserDTOWithTasks dtoWithTasks = userMapper.toDtoWithTasks(user);
        assertEquals(user.getId(), dtoWithTasks.getId());
        assertEquals(user.getName(), dtoWithTasks.getName());
        assertEquals(taskSimpeDTOList, dtoWithTasks.getTasks());
    }

    @Test
    void toEntityWithTasks() {
        when(taskMapper.toEntitySimpleAll(taskSimpeDTOList)).thenReturn(taskList);
        User entity = userMapper.toEntity(userDTOWithTasks);
        assertEquals(userDTOWithTasks.getId(), entity.getId());
        assertEquals(userDTOWithTasks.getName(), entity.getName());
        assertEquals(taskList, entity.getTasks());
    }

    @Test
    void toDto() {
        UserDTO dto = userMapper.toDto(user);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
    }

    @Test
    void toEntity() {
        User entity = userMapper.toEntity(userDTO);
        assertEquals(userDTO.getId(), entity.getId());
        assertEquals(userDTO.getName(), entity.getName());
    }

    @Test
    void toDtoAll() {
        List<UserDTO> dtoList = userMapper.toDtoAll(userList);
        assertEquals(userList.size(), dtoList.size());
    }

    @Test
    void toEntityAll() {
        List<User> entityList = userMapper.toEntityAll(userDTOList);
        assertEquals(userDTOList.size(), entityList.size());
    }
}
