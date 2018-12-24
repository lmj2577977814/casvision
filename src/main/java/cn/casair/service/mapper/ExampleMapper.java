package cn.casair.service.mapper;

import cn.casair.domain.Example;
import cn.casair.service.dto.ExampleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity User and its DTO called UserDTO.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
@Mapper(componentModel = "spring")
public interface ExampleMapper extends EntityMapper<ExampleDTO, Example>{

    @Mappings({
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    @Override
    Example toEntity(ExampleDTO dto);

    @Override
    ExampleDTO toDto(Example entity);

    default Example fromId(Long id) {
        if (id == null) {
            return null;
        }
        Example example = new Example();
        example.setId(id);
        return example;
    }

}
