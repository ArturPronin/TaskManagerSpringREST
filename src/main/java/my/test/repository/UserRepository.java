package my.test.repository;

import my.test.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    @NonNull
    List<User> findAll();
}
