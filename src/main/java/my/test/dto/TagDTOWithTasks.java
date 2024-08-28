package my.test.dto;

import java.util.List;

public class TagDTOWithTasks {
    private Long id;
    private String title;
    private List<TaskSimpleDTO> tasks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TaskSimpleDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSimpleDTO> tasks) {
        this.tasks = tasks;
    }
}