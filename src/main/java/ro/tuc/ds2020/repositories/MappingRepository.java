package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Mapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MappingRepository extends JpaRepository<Mapping, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
//    Optional<Mapping> findByuser_id( UUID user_id);

//    Optional<Mapping> findByDeviceId( UUID device_id);
    /**
     * Example: Write Custom Query
     */
    @Query(value = "SELECT m " +
            "FROM Mapping m " +
            "WHERE m.id = :id ")
    Optional<Mapping> findById(@Param("id") UUID id);


}