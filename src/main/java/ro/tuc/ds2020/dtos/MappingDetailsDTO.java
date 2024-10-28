package ro.tuc.ds2020.dtos;

import java.util.Objects;
import java.util.UUID;

public class MappingDetailsDTO {

    private UUID id;
    private UUID deviceId;
    private String deviceDescription; // Optional: more details about the device
    private UUID userId;
    private String userName; // Optional: more details about the user

    public MappingDetailsDTO() {
    }

    public MappingDetailsDTO(UUID id, UUID deviceId, String deviceDescription, UUID userId, String userName) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceDescription = deviceDescription;
        this.userId = userId;
        this.userName = userName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MappingDetailsDTO)) return false;
        MappingDetailsDTO that = (MappingDetailsDTO) o;
        return id.equals(that.id) &&
                deviceId.equals(that.deviceId) &&
                Objects.equals(deviceDescription, that.deviceDescription) &&
                userId.equals(that.userId) &&
                Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceId, deviceDescription, userId, userName);
    }
}
