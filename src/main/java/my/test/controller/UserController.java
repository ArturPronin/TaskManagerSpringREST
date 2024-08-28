package my.test.controller;

import jakarta.persistence.EntityNotFoundException;
import my.test.dto.UserDTOWithTasks;
import my.test.mapper.UserMapper;
import my.test.model.User;
import my.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDTOWithTasks>> getAllUsers() {
        List<User> allUsers = userService.findAll();
        List<UserDTOWithTasks> userDTOs = allUsers.stream()
                .map(userMapper::toDtoWithTasks)
                .toList();
        return new ResponseEntity<>(userDTOs, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOWithTasks> getUserById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        UserDTOWithTasks userDTO = userMapper.toDtoWithTasks(user);
        return new ResponseEntity<>(userDTO, OK);
    }

    @PostMapping
    public ResponseEntity<UserDTOWithTasks> createUser(@RequestBody UserDTOWithTasks userDTOWithTasks) {
        User user = userMapper.toEntity(userDTOWithTasks);
        userService.create(user);
        UserDTOWithTasks createdUserDTO = userMapper.toDtoWithTasks(user);
        return new ResponseEntity<>(createdUserDTO, CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTOWithTasks> updateUser(@PathVariable("id") Long id, @RequestBody UserDTOWithTasks userDTOWithTasks) {
        User user = userMapper.toEntity(userDTOWithTasks);
        User updatedUser = userService.update(id, user);
        UserDTOWithTasks updatedUserDTO = userMapper.toDtoWithTasks(updatedUser);
        return new ResponseEntity<>(updatedUserDTO, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        LOGGER.error("Entity not found", e);
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        LOGGER.error("Data integrity violation", e);
        return new ResponseEntity<>(BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception e) {
        LOGGER.error("Unexpected error", e);
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
}
