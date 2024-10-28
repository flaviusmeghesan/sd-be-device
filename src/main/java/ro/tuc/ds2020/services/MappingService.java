package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.MappingDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.builders.MappingBuilder;
import ro.tuc.ds2020.entities.Mapping;
import ro.tuc.ds2020.repositories.MappingRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MappingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingService.class);
    private final MappingRepository mappingRepository;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    public MappingService(MappingRepository mappingRepository, RestTemplate restTemplate) {
        this.mappingRepository = mappingRepository;
        this.restTemplate = restTemplate;
    }

    // method hasMappingForUser
    public boolean hasMappingsForUser(UUID user_id) {
        List<Mapping> mappingList = mappingRepository.findAll();
        for (Mapping mapping : mappingList) {
            if (mapping.getUser_id().equals(user_id)) {
                return true;
            }
        }
        return false;
    }
    public List<MappingDTO> findMappings(){
        List<MappingDTO> mappingList = mappingRepository.findAll().stream()
                .map(MappingBuilder::toMappingDTO)
                .collect(Collectors.toList());
        return mappingList;
    }


    public UUID insert(@Valid MappingDTO mappingDTO) {
        Mapping mapping = MappingBuilder.toEntity(mappingDTO);
        mapping = mappingRepository.save(mapping);
        LOGGER.debug("Mapping with id {} was inserted in db", mapping.getId());
        return mapping.getId();
    }

    public void delete(UUID id) {
        Optional<Mapping> mappingOptional = mappingRepository.findById(id);
        if (!mappingOptional.isPresent()) {
            LOGGER.error("Mapping with id {} was not found in db", id);
            throw new ResourceNotFoundException(Mapping.class.getSimpleName() + " with id: " + id);
        }
        mappingRepository.deleteById(id);
    }
    public List<MappingDTO> findMappingsByUserId(UUID user_id){
        List<MappingDTO> mappingList = mappingRepository.findAll().stream()
                .filter(mapping -> mapping.getUser_id().equals(user_id))
                .map(MappingBuilder::toMappingDTO)
                .collect(Collectors.toList());
        return mappingList;
    }

    public List<MappingDTO> findMappingsByDeviceId(UUID device_id){
        List<MappingDTO> mappingList = mappingRepository.findAll().stream()
                .filter(mapping -> mapping.getDevice_id().equals(device_id))
                .map(MappingBuilder::toMappingDTO)
                .collect(Collectors.toList());
        return mappingList;
    }
}
