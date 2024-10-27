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
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.utils.JwtUtil;

import io.jsonwebtoken.Claims;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
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

        // Check if the device is assigned to a user
        DeviceDTO device = deviceService.findDeviceById(deviceId);
        if (device.getAssignedUserId() != null) {
            return new ResponseEntity<>("Cannot delete device. The device is assigned to a user.", HttpStatus.BAD_REQUEST);
        }

        deviceService.delete(deviceId);
        return new ResponseEntity<>("Device with id " + deviceId + " was deleted!", HttpStatus.OK);
    }

    @GetMapping("/my-devices")
    public ResponseEntity<List<DeviceDTO>> getAssignedDevices(HttpServletRequest request) {
        // Step 1: Validate the token and ensure user is a client
        UUID userId = getUserIdFromToken(request);
        if (userId == null || !isClient(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Step 2: Retrieve devices assigned to the logged-in user
        List<DeviceDTO> devices = deviceService.findDevicesByUserId(userId);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }



    @PutMapping("/assign/{deviceId}/{userId}")
    public ResponseEntity<String> assignDeviceToUser(@PathVariable("deviceId") UUID deviceId, @PathVariable("userId") UUID userId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            // Step 1: Update device's assigned_user_id in Device Microservice database
            deviceService.assignDeviceToUser(deviceId, userId);

            // Step 2: Call the User Microservice to update the assigned_devices_id in user's record
            String userServiceUrl = "http://localhost:8080/person/" + userId + "/assignDevice/" + deviceId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", request.getHeader("Authorization"));  // Forward the JWT token
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> userServiceResponse = restTemplate.exchange(userServiceUrl, HttpMethod.PUT, entity, String.class);

            if (userServiceResponse.getStatusCode() == HttpStatus.OK) {
                return new ResponseEntity<>("Device assigned to user successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to update user record in User Microservice.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("Error assigning device to user: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
