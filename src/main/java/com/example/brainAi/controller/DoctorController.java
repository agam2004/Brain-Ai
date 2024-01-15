package com.example.brainAi.controller;

import com.example.brainAi.dto.DoctorDTO;
import com.example.brainAi.entity.Doctor;
import com.example.brainAi.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
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
    public List<Doctor> getAllDoctors() throws Exception { return doctorService.getAllDoctors(); }

    @GetMapping("/{id}")
    public DoctorDTO getDoctorById(Long id) {
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