package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<Device> findByDescription(String description);

    /**
     * Example: Write Custom Query
     */
    @Query(value = "SELECT d " +
            "FROM Device d " +
            "WHERE d.description = :description " +
            "AND d.maxHourlyEnergyConsumption > :consumption")
    Optional<Device> findDevicesByDescriptionAndConsumption(@Param("description") String description, @Param("consumption") double consumption);

}