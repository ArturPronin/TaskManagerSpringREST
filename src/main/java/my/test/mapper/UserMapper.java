package my.test.mapper;

import my.test.dto.UserDTO;
import my.test.dto.UserDTOWithTasks;
import my.test.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface UserMapper {
    UserDTOWithTasks toDtoWithTasks(User user);

    User toEntity(UserDTOWithTasks userDTOWithTasks);

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDtoAll(List<User> users);

    List<User> toEntityAll(List<UserDTO> userDTOs);
}

