package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getDescription(),
                device.getAddress(),
                device.getMaxHourlyEnergyConsumption(),
                device.getAssignedUserId()
        );
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(
                deviceDetailsDTO.getId(),
                deviceDetailsDTO.getDescription(),
                deviceDetailsDTO.getAddress(),
                deviceDetailsDTO.getMaxHourlyEnergyConsumption(),
                deviceDetailsDTO.getAssignedUserId()
        );
    }
}