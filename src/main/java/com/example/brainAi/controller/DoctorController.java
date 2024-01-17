package com.example.brainAi.controller;

import com.example.brainAi.dto.DoctorDTO;
import com.example.brainAi.entity.Doctor;
import com.example.brainAi.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> createPatient(@Valid @RequestBody DoctorDTO doctorDTO)  {
        return ResponseEntity.ok(doctorService.createDoctor(doctorDTO));
    }

    @GetMapping
    @Secured("DOCTOR")
    public ResponseEntity<List<Doctor>> getAllDoctors() throws Exception {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public DoctorDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctorPassword(@PathVariable Long id, @Valid @RequestBody DoctorDTO doctorDTO) throws Exception {
        return ResponseEntity.ok(doctorService.updateDoctorPassword(id, doctorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) throws Exception {
        try {
            doctorService.deleteDoctor(id);
            return new ResponseEntity<>("deleted!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Some error occured", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}