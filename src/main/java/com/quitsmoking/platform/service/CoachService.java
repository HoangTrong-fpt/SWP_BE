package com.quitsmoking.platform.service;

import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    // Create a new Coach
    public Coach createCoach(Coach coach) {
        return coachRepository.save(coach);
    }

    // Get all Coaches
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    // Get Coach by ID
    public Optional<Coach> getCoachById(Long id) {
        return coachRepository.findById(id);
    }

    // Update a Coach
    public Coach updateCoach(Long id, Coach coachDetails) {
        if (coachRepository.existsById(id)) {
            coachDetails.setId(id);
            return coachRepository.save(coachDetails);
        }
        return null; // or throw an exception if coach not found
    }

    // Delete a Coach
    public void deleteCoach(Long id) {
        coachRepository.deleteById(id);
    }
}
