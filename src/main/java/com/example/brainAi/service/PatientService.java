package com.example.brainAi.service;

import com.example.brainAi.dto.PatientDTO;
import com.example.brainAi.entity.Patient;
import com.example.brainAi.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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
        Patient patient = mapPatientDTOToPatient(patientDTO);
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
