package my.test.mapper;

import my.test.dto.TagDTO;
import my.test.dto.TagDTOWithTasks;
import my.test.dto.TaskSimpleDTO;
import my.test.model.Tag;
import my.test.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagMapperTest {
    @InjectMocks
    private TagMapper tagMapper = Mappers.getMapper(TagMapper.class);
    @Mock
    private TaskMapper taskMapper;
    private Tag tag;
    private TagDTO tagDTO;
    private TagDTOWithTasks tagDTOWithTasks;
    private List<Tag> tagList;
    private List<TagDTO> tagDTOList;
    private List<TagDTOWithTasks> tagDTOWithTasksList;
    private List<Task> taskList;
    private List<TaskSimpleDTO> taskSimpleDTOList;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");
        task.setDescription("Description 1");

        taskList = Arrays.asList(task);
        taskSimpleDTOList = Arrays.asList(new TaskSimpleDTO());

        tag = new Tag();
        tag.setId(1L);
        tag.setTitle("Tag 1");
        tag.setTasks(taskList);

        tagDTO = new TagDTO();
        tagDTO.setId(1L);
        tagDTO.setTitle("Tag 1");

        tagDTOWithTasks = new TagDTOWithTasks();
        tagDTOWithTasks.setId(1L);
        tagDTOWithTasks.setTitle("Tag 1");
        tagDTOWithTasks.setTasks(taskSimpleDTOList);

        tagList = Arrays.asList(tag);
        tagDTOList = Arrays.asList(tagDTO);
        tagDTOWithTasksList = Arrays.asList(tagDTOWithTasks);
    }

    @Test
    void toDtoWithTasks() {
        when(taskMapper.toSimpleDtoAll(taskList)).thenReturn(taskSimpleDTOList);
        TagDTOWithTasks dtoWithTasks = tagMapper.toDtoWithTasks(tag);
        assertEquals(tag.getId(), dtoWithTasks.getId());
        assertEquals(tag.getTitle(), dtoWithTasks.getTitle());
        assertEquals(taskSimpleDTOList, dtoWithTasks.getTasks());
    }

    @Test
    void toEntityWithTasks() {
        when(taskMapper.toEntitySimpleAll(taskSimpleDTOList)).thenReturn(taskList);
        Tag entity = tagMapper.toEntityWithTasks(tagDTOWithTasks);
        assertEquals(tagDTOWithTasks.getId(), entity.getId());
        assertEquals(tagDTOWithTasks.getTitle(), entity.getTitle());
        assertEquals(taskList, entity.getTasks());
    }

    @Test
    void toDto() {
        TagDTO dto = tagMapper.toDto(tag);
        assertEquals(tag.getId(), dto.getId());
        assertEquals(tag.getTitle(), dto.getTitle());
    }

    @Test
    void toEntity() {
        Tag entity = tagMapper.toEntity(tagDTO);
        assertEquals(tagDTO.getId(), entity.getId());
        assertEquals(tagDTO.getTitle(), entity.getTitle());
    }

    @Test
    void toDtoAll() {
        List<TagDTO> dtoList = tagMapper.toDtoAll(tagList);
        assertEquals(tagList.size(), dtoList.size());
    }

    @Test
    void toEntityAll() {
        List<Tag> entityList = tagMapper.toEntityAll(tagDTOList);
        assertEquals(tagDTOList.size(), entityList.size());
    }
}