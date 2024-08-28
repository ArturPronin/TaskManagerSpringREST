package my.test.mapper;

import my.test.dto.TagDTO;
import my.test.dto.TagDTOWithTasks;
import my.test.model.Tag;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface TagMapper {
    TagDTOWithTasks toDtoWithTasks(Tag tag);

    Tag toEntityWithTasks(TagDTOWithTasks tagDTOWithTasks);

    TagDTO toDto(Tag tag);

    Tag toEntity(TagDTO tagDTO);

    List<TagDTO> toDtoAll(List<Tag> tags);

    List<Tag> toEntityAll(List<TagDTO> tagDTOs);
}