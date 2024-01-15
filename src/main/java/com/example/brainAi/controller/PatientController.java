package com.example.brainAi.controller;

import com.example.brainAi.dto.PatientDTO;
import com.example.brainAi.entity.Patient;
import com.example.brainAi.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    @Autowired
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.createPatient(patientDTO));
    }

    @GetMapping
    public List<Patient> getAllPatients() throws Exception { return patientService.getAllPatients(); }

    @GetMapping("/{id}")
    public PatientDTO getPatientById(Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/email/{email}")
    public Patient findPatientByEmail(@Valid @PathVariable String email) throws Exception {
        try {
            return patientService.findPatientByEmail(email);
        } catch(Exception e) {
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatientPassword(Long id, PatientDTO patientDTO) throws Exception {
        return ResponseEntity.ok(patientService.updatePatientPassword(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) throws Exception {
        try {
            patientService.deletePatient(id);
            return new ResponseEntity<>("deleted!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Some error occured", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}