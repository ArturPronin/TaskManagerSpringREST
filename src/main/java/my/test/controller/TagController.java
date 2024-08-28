package my.test.controller;

import jakarta.persistence.EntityNotFoundException;
import my.test.dto.TagDTOWithTasks;
import my.test.mapper.TagMapper;
import my.test.model.Tag;
import my.test.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    public TagController(TagService tagService, TagMapper tagMapper) {
        this.tagService = tagService;
        this.tagMapper = tagMapper;
    }

    @GetMapping
    public ResponseEntity<List<TagDTOWithTasks>> getAllTags() {
        List<Tag> allTags = tagService.findAll();
        List<TagDTOWithTasks> tagDTOs = allTags.stream().map(tagMapper::toDtoWithTasks).toList();
        return new ResponseEntity<>(tagDTOs, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTOWithTasks> getTagById(@PathVariable("id") Long id) {
        Tag tag = tagService.findById(id);
        TagDTOWithTasks tagDTO = tagMapper.toDtoWithTasks(tag);
        return new ResponseEntity<>(tagDTO, OK);
    }

    @PostMapping
    public ResponseEntity<TagDTOWithTasks> createTag(@RequestBody TagDTOWithTasks tagDTOWithTasks) {
        Tag tag = tagMapper.toEntityWithTasks(tagDTOWithTasks);
        Tag savedTag = tagService.create(tag);  // Получите сохраненный объект
        TagDTOWithTasks createdTagDTO = tagMapper.toDtoWithTasks(savedTag);  // Преобразуйте сохраненный объект
        return new ResponseEntity<>(createdTagDTO, CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTOWithTasks> updateTag(@PathVariable("id") Long id, @RequestBody TagDTOWithTasks tagDTOWithTasks) {
        Tag tag = tagMapper.toEntityWithTasks(tagDTOWithTasks);
        Tag updatedTag = tagService.update(id, tag);
        TagDTOWithTasks updatedTagDTO = tagMapper.toDtoWithTasks(updatedTag);
        return new ResponseEntity<>(updatedTagDTO, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") Long id) {
        tagService.delete(id);
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
