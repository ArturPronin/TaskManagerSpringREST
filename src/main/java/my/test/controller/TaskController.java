package my.test.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final TagService tagService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService taskService, TaskMapper taskMapper, UserService userService, TagService tagService) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.userService = userService;
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> allTasks = taskService.findAll();
        List<TaskDTO> taskDTOs = taskMapper.toDtoAll(allTasks);
        return new ResponseEntity<>(taskDTOs, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("id") Long id) {
        Task task = taskService.findById(id);
        TaskDTO taskDTO = taskMapper.toDto(task);
        return new ResponseEntity<>(taskDTO, OK);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskCreateUpdateDTO taskCreateUpdateDTO) {
        Task task = new Task();
        task.setTitle(taskCreateUpdateDTO.getTitle());
        task.setDescription(taskCreateUpdateDTO.getDescription());
        if (taskCreateUpdateDTO.getUserId() != null) {
            User user = userService.findById(taskCreateUpdateDTO.getUserId());
            task.setUser(user);
        }

        if (taskCreateUpdateDTO.getTagIds() != null && !taskCreateUpdateDTO.getTagIds().isEmpty()) {
            List<Tag> tags = tagService.findByIds(taskCreateUpdateDTO.getTagIds());
            task.setTags(tags);
        }
        Task savedTask = taskService.create(task);
        TaskDTO taskDTO = taskMapper.toDto(savedTask);
        return new ResponseEntity<>(taskDTO, CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable("id") Long id, @RequestBody TaskCreateUpdateDTO taskDTO) {
        Task existingTask = taskService.findById(id);
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        if (taskDTO.getUserId() != null) {
            User user = userService.findById(taskDTO.getUserId());
            existingTask.setUser(user);
        } else {
            existingTask.setUser(null);
        }
        if (taskDTO.getTagIds() != null && !taskDTO.getTagIds().isEmpty()) {
            List<Tag> tags = tagService.findByIds(taskDTO.getTagIds());
            existingTask.setTags(tags);
        } else {
            existingTask.setTags(new ArrayList<>());
        }
        Task updatedTask = taskService.update(id, existingTask);
        TaskDTO updatedTaskDTO = taskMapper.toDto(updatedTask);
        return new ResponseEntity<>(updatedTaskDTO, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        taskService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskDTO> assignUserAndTagToTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam(value = "user", required = false) Long userId,
            @RequestParam(value = "tag", required = false) Long tagId) {
        Task task = taskService.findById(taskId);
        if (userId != null) {
            taskService.assignUserToTask(task, userId);
        }
        if (tagId != null) {
            taskService.assignTagToTask(task, tagId);
        }
        TaskDTO taskDTO = taskMapper.toDto(task);
        return new ResponseEntity<>(taskDTO, OK);
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
