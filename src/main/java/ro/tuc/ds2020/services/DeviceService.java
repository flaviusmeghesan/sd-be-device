package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.MappingDTO;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.dtos.builders.MappingBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.Mapping;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.MappingRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private MappingRepository mappingRepository;
    @Autowired
    private MappingService mappingService;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, RestTemplate restTemplate) {
        this.deviceRepository = deviceRepository;
        this.restTemplate = restTemplate;
    }


    public PersonDTO getPersonData(UUID personId) {
        String url = "http://localhost:8080/person/" + personId; // URL of the user microservice
        return restTemplate.getForObject(url, PersonDTO.class);
    }


    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public List<DeviceDTO> findDevicesByUserId( UUID user_id) {
        List<MappingDTO> mappingList = mappingRepository.findAll().stream()
                .filter(mapping -> mapping.getUser_id().equals(user_id))
                .map(MappingBuilder::toMappingDTO)
                .collect(Collectors.toList());
        return mappingList.stream()
                .map(mapping -> findDeviceById(mapping.getDeviceId())) // Call findDeviceById for each deviceId
                .collect(Collectors.toList());
    }
    public DeviceDTO findDeviceById(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDTO(deviceOptional.get());
    }


    public void delete(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);

        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        // Check if there are any mappings associated with this device
        List<MappingDTO> mappings = mappingService.findMappingsByDeviceId(id);
        if (!mappings.isEmpty()) {
            LOGGER.error("Device with id {} has assigned users and cannot be deleted", id);
            throw new IllegalStateException("Cannot delete device with id " + id + " because it is assigned to one or more users.");
        }

        // If no mappings found, proceed with deletion
        deviceRepository.deleteById(id);
        LOGGER.info("Device with id {} was deleted successfully", id);
    }


    public UUID insert(@Valid DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public UUID update(UUID id, @Valid DeviceDetailsDTO deviceDetailsDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        Device device = DeviceBuilder.toEntity(deviceDetailsDTO);
        device.setId(id);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was updated in db", id);
        return device.getId();
    }

    public UUID assignDeviceToUser(UUID user_id, UUID device_id) {
        Optional<Device> deviceOptional = deviceRepository.findById(device_id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", device_id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + device_id);
        }
        MappingDTO mappingDTO = new MappingDTO();
        mappingDTO.setDeviceId(device_id);
        mappingDTO.setUserId(user_id);

        return mappingService.insert(mappingDTO);
    }






}
