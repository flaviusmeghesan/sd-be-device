package ro.tuc.ds2020.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.MappingDTO;
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.utils.JwtUtil;

import io.jsonwebtoken.Claims;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:3000"}, allowCredentials = "true")
@RequestMapping(value = "/device")
public class DeviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);
    private final DeviceService deviceService;

    private RestTemplate restTemplate;

    @Autowired
    public DeviceController(DeviceService deviceService, RestTemplate restTemplate) {
        this.deviceService = deviceService;
        this.restTemplate = restTemplate;
    }

    // Helper method to check if the logged-in user is an admin based on JWT
    private boolean isAdmin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        try {
            Claims claims = JwtUtil.validateToken(token);
            String role = claims.get("role", String.class);
            return "admin".equalsIgnoreCase(role);
        } catch (Exception e) {
            LOGGER.error("JWT validation failed: {}", e.getMessage());
            return false; // Invalid token
        }
    }

    private boolean isClient(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        try {
            Claims claims = JwtUtil.validateToken(token);
            String role = claims.get("role", String.class);
            return "client".equalsIgnoreCase(role);
        } catch (Exception e) {
            LOGGER.error("JWT validation failed: {}", e.getMessage());
            return false; // Invalid token or role
        }
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUserId(@PathVariable("id") UUID user_id, HttpServletRequest request) {
        if (!isClient(request)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<DeviceDTO> devices = deviceService.findDevicesByUserId(user_id);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }



    // Helper method to validate JWT and return user ID from token
    private UUID getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = JwtUtil.validateToken(token);
            return UUID.fromString(claims.get("id", String.class));
        } catch (Exception e) {
            LOGGER.error("Failed to extract user ID from token: {}", e.getMessage());
            return null; // Invalid token
        }
    }


    // Get all devices (admin only)
    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices(HttpServletRequest request) {
        if (!isAdmin(request)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<DeviceDTO> devices = deviceService.findDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    // Insert a new device (admin only)
    @PostMapping()
    public ResponseEntity<UUID> insertDevice(@Valid @RequestBody DeviceDetailsDTO deviceDTO, HttpServletRequest request) {
        if (!isAdmin(request)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        UUID deviceId = deviceService.insert(deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.CREATED);
    }

    // Update a device (admin only)
    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updateDevice(@PathVariable("id") UUID id, @Valid @RequestBody DeviceDetailsDTO deviceDTO, HttpServletRequest request) {
        if (!isAdmin(request)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        UUID deviceId = deviceService.update(id, deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.OK);
    }

    // Delete a device (admin only)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable("id") UUID deviceId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }



        deviceService.delete(deviceId);
        return new ResponseEntity<>("Device with id " + deviceId + " was deleted!", HttpStatus.OK);
    }

    // Assign a device to a user (admin only)
    @PostMapping(value = "/assign/{user_id}/{device_id}")
    public ResponseEntity<String> assignDeviceToUser(@PathVariable("user_id") UUID user_id, @PathVariable("device_id") UUID device_id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        deviceService.assignDeviceToUser(user_id, device_id);
        return new ResponseEntity<>("Device with id " + device_id + " was assigned to user with id " + user_id, HttpStatus.OK);
    }


}
