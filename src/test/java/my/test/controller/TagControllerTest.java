package my.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import my.test.dto.TagDTOWithTasks;
import my.test.mapper.TagMapper;
import my.test.model.Tag;
import my.test.service.TagService;
import my.test.service.TaskService;
import my.test.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TagControllerTest {
    private MockMvc mockMvc;
    @Mock
    private TaskService taskService;
    @Mock
    private TagMapper tagMapper;
    @Mock
    private UserService userService;
    @Mock
    private TagService tagService;
    @InjectMocks
    private TagController tagController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @Test
    void testGetAllTags() throws Exception {
        Tag tag = new Tag();
        tag.setTitle("Tag Title");
        TagDTOWithTasks tagDTOWithTasks = new TagDTOWithTasks();
        tagDTOWithTasks.setTitle("Tag Title");
        List<Tag> tags = Arrays.asList(tag);
        List<TagDTOWithTasks> tagDTOWithTasksList = Arrays.asList(tagDTOWithTasks);

        when(tagService.findAll()).thenReturn(tags);
        when(tagMapper.toDtoWithTasks(tag)).thenReturn(tagDTOWithTasks);  

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Tag Title"));

        verify(tagService, times(1)).findAll();
        verify(tagMapper, times(1)).toDtoWithTasks(tags.get(0));  
    }

    @Test
    void testGetTagsById() throws Exception {
        Tag tag = new Tag();
        TagDTOWithTasks tagDTOWithTasks = new TagDTOWithTasks();
        tagDTOWithTasks.setTitle("Tag Title");

        when(tagService.findById(anyLong())).thenReturn(tag);
        when(tagMapper.toDtoWithTasks(tag)).thenReturn(tagDTOWithTasks);

        mockMvc.perform(get("/tags/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Tag Title"));

        verify(tagService, times(1)).findById(anyLong());
        verify(tagMapper, times(1)).toDtoWithTasks(tag);
    }

    @Test
    void testCreateTag() throws Exception {
        TagDTOWithTasks tagDTOWithTasks = new TagDTOWithTasks();
        tagDTOWithTasks.setTitle("New Tag");

        Tag tag = new Tag();
        tag.setTitle("New Tag");

        when(tagMapper.toEntityWithTasks(any(TagDTOWithTasks.class))).thenReturn(tag);
        when(tagMapper.toDtoWithTasks(any(Tag.class))).thenReturn(tagDTOWithTasks);
        when(tagService.create(any(Tag.class))).thenReturn(tag);  

        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(tagDTOWithTasks)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Tag"));

        verify(tagMapper, times(1)).toEntityWithTasks(any(TagDTOWithTasks.class));
        verify(tagService, times(1)).create(any(Tag.class));
        verify(tagMapper, times(1)).toDtoWithTasks(any(Tag.class));
    }

    @Test
    void testUpdateTag() throws Exception {
        Long id = 1L;
        TagDTOWithTasks tagDTOWithTasks = new TagDTOWithTasks();
        tagDTOWithTasks.setTitle("Updated Tag");

        Tag tag = new Tag();
        tag.setTitle("Updated Tag");

        when(tagMapper.toEntityWithTasks(any(TagDTOWithTasks.class))).thenReturn(tag);
        when(tagService.update(eq(id), any(Tag.class))).thenReturn(tag);
        when(tagMapper.toDtoWithTasks(any(Tag.class))).thenReturn(tagDTOWithTasks);

        mockMvc.perform(put("/tags/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(tagDTOWithTasks)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Tag"));

        verify(tagMapper, times(1)).toEntityWithTasks(any(TagDTOWithTasks.class));
        verify(tagService, times(1)).update(eq(id), any(Tag.class));
        verify(tagMapper, times(1)).toDtoWithTasks(any(Tag.class));
    }

    @Test
    void testDeleteTag() throws Exception {
        Long id = 1L;

        doNothing().when(tagService).delete(id);

        mockMvc.perform(delete("/tags/{id}", id))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).delete(id);
    }

    @Test
    void testHandleEntityNotFoundException() throws Exception {
        ResponseEntity<Object> responseEntity = tagController.handleEntityNotFoundException(new EntityNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testHandleDataIntegrityViolationException() throws Exception {
        ResponseEntity<Object> responseEntity = tagController.handleDataIntegrityViolationException(new DataIntegrityViolationException(""));
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testHandleOtherException() throws Exception {
        ResponseEntity<Object> responseEntity = tagController.handleOtherException(new Exception());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }


    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
