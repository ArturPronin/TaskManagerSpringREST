package my.test.dto;

import java.util.List;

public class UserDTOWithTasks {
    private Long id;
    private String name;
    private List<TaskSimpleDTO> tasks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskSimpleDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSimpleDTO> tasks) {
        this.tasks = tasks;
    }
}