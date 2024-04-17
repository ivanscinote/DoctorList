package de.wetego.vaadin.service;

import de.wetego.vaadin.model.Doctor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DoctorService {
    private final List<Doctor> doctors = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public List<Doctor> findAll() {
        return doctors;
    }

    public Doctor findById(Long id) {
        return doctors.stream().filter(doctor -> doctor.getId().equals(id)).findFirst().orElse(null);
    }

    public Doctor addDoctor(String name, String specialty) {
        Doctor newDoctor = new Doctor(counter.incrementAndGet(), name, specialty);
        doctors.add(newDoctor);
        return newDoctor;
    }
}
