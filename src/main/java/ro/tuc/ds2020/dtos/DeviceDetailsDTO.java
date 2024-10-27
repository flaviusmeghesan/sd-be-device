package ro.tuc.ds2020.dtos;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;
    @NotNull
    private String description;
    @NotNull
    private String address;
    @NotNull
    private double maxHourlyEnergyConsumption;

    @Nullable

    private UUID assignedUserId;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(UUID id, String description, String address, double maxHourlyEnergyConsumption, UUID assignedUserId) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.assignedUserId = assignedUserId;
    }

    public DeviceDetailsDTO(String description, String address, double maxHourlyEnergyConsumption, UUID assignedUserId) {
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.assignedUserId = assignedUserId;
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

    @Nullable
    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(@Nullable UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
}