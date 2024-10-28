package ro.tuc.ds2020.dtos;

import java.util.UUID;
import java.util.Objects;

public class MappingDTO {

    private UUID id;        // Mapping ID
    private UUID deviceId;
    private UUID userId;

    public MappingDTO() {
    }

    public MappingDTO(UUID id, UUID deviceId, UUID userId) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MappingDTO)) return false;
        MappingDTO that = (MappingDTO) o;
        return id.equals(that.id) && deviceId.equals(that.deviceId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceId, userId);
    }
}
