package com.example.brainAi.service;

import com.example.brainAi.dto.DoctorDTO;
import com.example.brainAi.entity.Doctor;
import com.example.brainAi.entity.Role;
import com.example.brainAi.entity.User;
import com.example.brainAi.repository.DoctorRepository;
import com.example.brainAi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    // Mapping from Doctor entity to DoctorDTO
    public DoctorDTO mapDoctorToDTODoctor(Doctor doctor) {
        if (doctor != null) {
            DoctorDTO doctorDTO = new DoctorDTO();
            doctorDTO.setFirstName(doctor.getFirstName());
            doctorDTO.setLastName(doctor.getLastName());
            doctorDTO.setEmail(doctor.getEmail());
            doctorDTO.setPassword(doctor.getPassword());
            return doctorDTO;
        }
        return null;
    }

    // Mapping from DoctorDTO to Doctor entity
    public Doctor mapDTODoctorToDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setPassword(doctorDTO.getPassword());
        return doctor;
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        // Check if the email is already in use
        if (userRepository.existsByEmail(doctorDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create a new User entity for the Doctor
        User user = new User();
        user.setEmail(doctorDTO.getEmail());
        user.setFirstName(doctorDTO.getFirstName());
        user.setLastName(doctorDTO.getLastName());
        user.setPassword(doctorDTO.getPassword());
        user.getRoles().add(new Role("DOCTOR")); // Assuming you have a UserRole enum
        userRepository.save(user);
        Doctor doctor = mapDTODoctorToDoctor(doctorDTO);
        doctor.setUser(user);
        doctorRepository.save(doctor);
        return mapDoctorToDTODoctor(doctor);
    }
    public List<Doctor> getAllDoctors() throws  Exception {
        List<Doctor> doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) {
            throw new Exception("Error");
        }
        return  doctors;
    }

    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
        DoctorDTO doctorDTO = mapDoctorToDTODoctor(doctor);
        return doctorDTO;
    }
    public DoctorDTO updateDoctorPassword(Long id, DoctorDTO doctorDTO) throws Exception {
        if (doctorDTO == null || doctorDTO.getEmail() == null || doctorDTO.getPassword() == null) {
            throw new Exception("Invalid doctor data.");
        }
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new Exception("Doctor not found"));
        doctor.setPassword(doctorDTO.getPassword());

        doctor = doctorRepository.save(doctor);
        return mapDoctorToDTODoctor(doctor);
    }

    public void deleteDoctor(Long idDoctor) throws Exception {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(idDoctor);
        if(optionalDoctor.isEmpty()) {
            throw new Exception("Author not found with ID: " + idDoctor);
        }
        Doctor doctor = optionalDoctor.get();
        doctorRepository.delete(doctor);
    }
}