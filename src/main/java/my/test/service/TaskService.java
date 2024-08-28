package my.test.service;

import my.test.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> findAll();

    Task findById(Long id);

    Task create(Task task);

    Task update(Long id, Task task);

    void delete(Long id);

    Task assignUserToTask(Task task, Long userId);

    Task assignTagToTask(Task task, Long tagId);

}
