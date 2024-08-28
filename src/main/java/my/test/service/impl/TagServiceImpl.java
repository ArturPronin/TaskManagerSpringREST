package my.test.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.repository.TagRepository;
import my.test.service.TagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public Tag findById(Long id) {
        return tagRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional
    public Tag create(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag update(Long id, Tag tag) {
        tag.setId(id);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        for (Task task : tag.getTasks()) {
            task.getTags().remove(tag);
        }
        tagRepository.delete(tag);
    }

    @Override
    @Transactional
    public List<Tag> findByIds(List<Long> ids) {
        List<Tag> tags = new ArrayList<>();
        tags.add(tagRepository.findAllById(ids).iterator().next());
        return tags;
    }
}
