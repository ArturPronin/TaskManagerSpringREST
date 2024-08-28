package my.test.repository;

import my.test.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {
    @NonNull
    List<Tag> findAll();
}
