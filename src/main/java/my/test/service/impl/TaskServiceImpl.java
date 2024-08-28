package my.test.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.model.User;
import my.test.repository.TagRepository;
import my.test.repository.TaskRepository;
import my.test.repository.UserRepository;
import my.test.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional
    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task update(Long id, Task task) {
        Task existingTask = findById(id);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setUser(task.getUser());
        existingTask.setTags(task.getTags());
        return taskRepository.save(existingTask);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        if (task.getUser() != null) {
            task.getUser().getTasks().remove(task);
        }
        for (Tag tag : task.getTags()) {
            tag.getTasks().remove(task);
        }
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public Task assignUserToTask(Task task, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task assignTagToTask(Task task, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        boolean tagExists = task.getTags().stream()
                .anyMatch(existingTag -> existingTag.getId().equals(tagId));

        if (!tagExists) {
            task.getTags().add(tag);
            task = taskRepository.save(task);
        }

        return task;
    }
}
