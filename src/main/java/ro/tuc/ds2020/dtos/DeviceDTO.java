package ro.tuc.ds2020.dtos;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {
    private UUID id;
    private String description;
    private String address;
    private double maxHourlyEnergyConsumption;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String description, String address, double maxHourlyEnergyConsumption) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(double maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceDTO)) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Double.compare(deviceDTO.maxHourlyEnergyConsumption, maxHourlyEnergyConsumption) == 0 &&
                id.equals(deviceDTO.id) &&
                description.equals(deviceDTO.description) &&
                address.equals(deviceDTO.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, address, maxHourlyEnergyConsumption);
    }
}