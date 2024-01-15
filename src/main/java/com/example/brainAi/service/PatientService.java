package com.example.brainAi.service;

import com.example.brainAi.dto.PatientDTO;
import com.example.brainAi.entity.Patient;
import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.PatientRepository;
import com.example.brainAi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }
    @Transactional
    // Utility method to convert Patient to PatientDTO
    public PatientDTO mapPatientToPatientDTO(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(patient.getFirstName());
        patientDTO.setLastName(patient.getLastName());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setPassword(patient.getPassword());
        return patientDTO;
    }

    public Patient mapPatientDTOToPatient(PatientDTO patientDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPassword(patientDTO.getPassword());
        return patient;
    }

    public PatientDTO createPatient(PatientDTO patientDTO) {
        if (userRepository.existsByEmail(patientDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create a new User entity for the Doctor
        User user = new User();
        user.setEmail(patientDTO.getEmail());
        user.setFirstName(patientDTO.getFirstName());
        user.setLastName(patientDTO.getLastName());
        user.setPassword(patientDTO.getPassword());
        user.getRoles().add(new Role("PATIENT")); // Assuming you have a UserRole enum
        userRepository.save(user);
        Patient patient = mapPatientDTOToPatient(patientDTO);
        patient.setUser(user);
        patientRepository.save(patient);
        return patientDTO;
    }

    public List<Patient> getAllPatients() throws  Exception {
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            throw new Exception("Error");
        }
        return  patients;
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        PatientDTO patientDTO = mapPatientToPatientDTO(patient);
        return patientDTO;
    }

    public Patient findPatientByEmail(String email) throws Exception {
        Patient patient = patientRepository.findPatientByEmail(email);
        if(patient == null) {
            throw new Exception("User not found for email: " + email);
        }
        return patient;
    }

    public PatientDTO updatePatientPassword(Long id, PatientDTO patientDTO) throws Exception {
        if (patientDTO == null || patientDTO.getEmail() == null || patientDTO.getPassword() == null) {
            throw new Exception("Invalid user data.");
        }
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
        patient.setPassword(patientDTO.getPassword());

        patient = patientRepository.save(patient);
        return mapPatientToPatientDTO(patient);
    }

    public void deletePatient(Long idPatient) throws Exception {
        Optional<Patient> optionalPatient = patientRepository.findById(idPatient);
        if(optionalPatient.isEmpty()) {
            throw new Exception("User not found");
        }
        Patient patient = optionalPatient.get();
        patientRepository.delete(patient);
    }
}
