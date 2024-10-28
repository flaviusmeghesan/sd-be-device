package ro.tuc.ds2020.controllers;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.MappingDTO;
import ro.tuc.ds2020.services.MappingService;

import jakarta.servlet.http.HttpServletRequest;
import ro.tuc.ds2020.utils.JwtUtil;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:3000"}, allowCredentials = "true")
@RequestMapping(value = "/mappings")
public class MappingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MappingController.class);

    private final MappingService mappingService;

    @Autowired
    public MappingController(MappingService mappingService) {
        this.mappingService = mappingService;
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<Boolean> hasAssignedDevices(@PathVariable UUID userId) {

        boolean hasMappings = mappingService.hasMappingsForUser(userId);
        return new ResponseEntity<>(hasMappings, HttpStatus.OK);
    }

    // Get all mappings (admin only)
    @GetMapping()
    public ResponseEntity<List<MappingDTO>> getAllMappings(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<MappingDTO> mappings = mappingService.findMappings();
        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }

    // Insert a new mapping (admin only)
    @PostMapping()
    public ResponseEntity<UUID> insertMapping(@RequestBody MappingDTO mappingDTO, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UUID mappingId = mappingService.insert(mappingDTO);
        return new ResponseEntity<>(mappingId, HttpStatus.CREATED);
    }

    // Delete a mapping by ID (admin only)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UUID> deleteMapping(@PathVariable UUID id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        mappingService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    // get mapping for a specific user (client only)
    @GetMapping(value = "/{user_id}")
    public ResponseEntity<List<MappingDTO>> getMappingsByUserId(@PathVariable UUID user_id, HttpServletRequest request) {
        if (!isClient(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<MappingDTO> mappings = mappingService.findMappingsByUserId(user_id);
        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }
}
