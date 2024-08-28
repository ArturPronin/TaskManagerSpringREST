package my.test.repository;

import my.test.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    @NonNull
    List<Task> findAll();
}
