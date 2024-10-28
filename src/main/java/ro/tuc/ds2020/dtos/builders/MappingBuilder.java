package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.MappingDTO;
import ro.tuc.ds2020.dtos.MappingDetailsDTO;
import ro.tuc.ds2020.entities.Mapping;

public class MappingBuilder {

    private MappingBuilder() {
    }

    // Convert Mapping entity to MappingDTO
    public static MappingDTO toMappingDTO(Mapping mapping) {
        return new MappingDTO(
                mapping.getId(),
                mapping.getDevice_id(),
                mapping.getUser_id()
        );
    }

    // Convert Mapping entity to MappingDetailsDTO
    public static MappingDetailsDTO toMappingDetailsDTO(Mapping mapping, String deviceDescription, String userName) {
        return new MappingDetailsDTO(
                mapping.getId(),
                mapping.getDevice_id(),
                deviceDescription,
                mapping.getUser_id(),
                userName
        );
    }

    // Convert MappingDTO to Mapping entity
    public static Mapping toEntity(MappingDTO mappingDTO) {
        return new Mapping(
                mappingDTO.getId(),
                mappingDTO.getDeviceId(),
                mappingDTO.getUserId()
        );
    }

    // Convert MappingDetailsDTO to Mapping entity (if needed)
    public static Mapping toEntity(MappingDetailsDTO mappingDetailsDTO) {
        return new Mapping(
                mappingDetailsDTO.getId(),
                mappingDetailsDTO.getDeviceId(),
                mappingDetailsDTO.getUserId()
        );
    }
}
