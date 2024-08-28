package my.test.service;

import my.test.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> findAll();

    Tag findById(Long id);

    Tag create(Tag tag);

    Tag update(Long id, Tag tag);

    void delete(Long id);

    List<Tag> findByIds(List<Long> ids);
}
