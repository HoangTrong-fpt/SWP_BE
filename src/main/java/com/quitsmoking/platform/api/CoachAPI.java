package com.quitsmoking.platform.api;

import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaches")
public class CoachAPI{

    @Autowired
    private CoachService coachService;

    // Create a new coach
    @PostMapping
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.createCoach(coach));
    }

    // Get all coaches
    @GetMapping
    public List<Coach> getAllCoaches() {
        return coachService.getAllCoaches();
    }

    // Get a coach by ID
    @GetMapping("/{id}")
    public ResponseEntity<Coach> getCoachById(@PathVariable Long id) {
        Optional<Coach> coach = coachService.getCoachById(id);
        return coach.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a coach
    @PutMapping("/{id}")
    public ResponseEntity<Coach> updateCoach(@PathVariable Long id, @RequestBody Coach coachDetails) {
        Coach updatedCoach = coachService.updateCoach(id, coachDetails);
        return updatedCoach != null ? ResponseEntity.ok(updatedCoach) : ResponseEntity.notFound().build();
    }

    // Delete a coach
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
}
