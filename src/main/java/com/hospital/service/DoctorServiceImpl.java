package com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.LabTest;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.LabTestRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.UserRepository;
import com.hospital.repository.VitalRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final AppointmentRepository appointmentRepository;
    private final VitalRepository vitalRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabTestRepository labTestRepository;
    private final UserRepository userRepository;
    private final AdmissionRepository admissionRepository;


    public DoctorServiceImpl(
            AppointmentRepository appointmentRepository,
            VitalRepository vitalRepository,
            ConsultationRepository consultationRepository,
            PrescriptionRepository prescriptionRepository,
            LabTestRepository labTestRepository,
            UserRepository userRepository,
            AdmissionRepository admissionRepository) {

        this.appointmentRepository = appointmentRepository;
        this.vitalRepository = vitalRepository;
        this.consultationRepository = consultationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labTestRepository = labTestRepository;
        this.userRepository = userRepository;
        this.admissionRepository = admissionRepository;
    }


    // =========================================================
    // APPOINTMENTS
    // =========================================================

    @Override
    public List<Appointment> getDoctorAppointments(
            Long doctorId) {

        return appointmentRepository.findByDoctorId(
                doctorId
        );
    }


    @Override
    public List<Appointment> getDoctorAppointmentsByStatus(
            Long doctorId,
            String status) {

        return appointmentRepository
                .findByDoctorIdAndStatus(
                        doctorId,
                        status
                );
    }


    // =========================================================
    // PATIENT DETAILS
    // =========================================================

    @Override
    public User getPatientById(Long patientId) {

        User patient = userRepository
                .findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found"
                        )
                );

        if (!"PATIENT".equalsIgnoreCase(
                patient.getRole())) {

            throw new RuntimeException(
                    "User is not a patient"
            );
        }

        return patient;
    }


    // =========================================================
    // VITALS
    // =========================================================

    @Override
    public List<Vital> getPatientVitals(
            Long patientId) {

        return vitalRepository.findByPatientId(
                patientId
        );
    }


    // =========================================================
    // CONSULTATION
    // =========================================================

    @Override
    public Consultation createConsultation(Consultation consultation) {

        if (consultation.getAppointment() == null ||
                consultation.getAppointment().getId() == null) {

            throw new RuntimeException(
                    "Appointment is required for consultation"
            );
        }

        if (consultation.getDoctor() == null ||
                consultation.getDoctor().getId() == null) {

            throw new RuntimeException(
                    "Doctor is required for consultation"
            );
        }

        if (consultation.getPatient() == null ||
                consultation.getPatient().getId() == null) {

            throw new RuntimeException(
                    "Patient is required for consultation"
            );
        }


        // Find actual appointment
        Appointment appointment = appointmentRepository.findById(
                consultation.getAppointment().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Appointment not found: "
                        + consultation.getAppointment().getId()
                )
        );


        // Find actual doctor
        User doctor = userRepository.findById(
                consultation.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Doctor not found"
                )
        );


        // Find actual patient
        User patient = userRepository.findById(
                consultation.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Patient not found"
                )
        );


        // Set real entities
        consultation.setAppointment(appointment);
        consultation.setDoctor(doctor);
        consultation.setPatient(patient);


        // Set consultation date automatically
        if (consultation.getConsultationDate() == null) {

            consultation.setConsultationDate(
                    java.time.LocalDateTime.now()
            );
        }


        return consultationRepository.save(consultation);
    }


    @Override
    public Consultation getConsultationByAppointment(
            Long appointmentId) {

        return consultationRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Consultation not found"
                        )
                );
    }


    @Override
    public List<Consultation> getDoctorConsultations(
            Long doctorId) {

        return consultationRepository
                .findByDoctorId(doctorId);
    }


    // =========================================================
    // PRESCRIPTIONS
    // =========================================================

    @Override
    public Prescription createPrescription(
            Prescription prescription) {

        User doctor = userRepository.findById(
                prescription.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException("Doctor not found")
        );

        User patient = userRepository.findById(
                prescription.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException("Patient not found")
        );

        prescription.setDoctor(doctor);
        prescription.setPatient(patient);

        if (prescription.getPrescriptionDate() == null) {
            prescription.setPrescriptionDate(
                    java.time.LocalDate.now()
            );
        }

        return prescriptionRepository.save(prescription);
    }


    @Override
    public List<Prescription> getPatientPrescriptions(
            Long patientId) {

        return prescriptionRepository
                .findByPatientId(patientId);
    }


    // =========================================================
    // LAB
    // =========================================================

    @Override
    public LabTest orderLabTest(LabTest labTest) {

        if (labTest.getPatient() == null ||
                labTest.getPatient().getId() == null) {

            throw new RuntimeException("Patient ID is required");
        }

        if (labTest.getDoctor() == null ||
                labTest.getDoctor().getId() == null) {

            throw new RuntimeException("Doctor ID is required");
        }

        if (labTest.getAppointment() == null ||
                labTest.getAppointment().getId() == null) {

            throw new RuntimeException("Appointment ID is required");
        }

        User patient = userRepository.findById(
                labTest.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException("Patient not found")
        );

        User doctor = userRepository.findById(
                labTest.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException("Doctor not found")
        );

        Appointment appointment = appointmentRepository.findById(
                labTest.getAppointment().getId()
        ).orElseThrow(() ->
                new RuntimeException("Appointment not found")
        );

        if (appointment.getPatient() == null ||
                appointment.getPatient().getId() == null ||
                !appointment.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Appointment does not belong to this patient"
            );
        }

        if (appointment.getDoctor() == null ||
                appointment.getDoctor().getId() == null ||
                !appointment.getDoctor().getId()
                        .equals(doctor.getId())) {

            throw new RuntimeException(
                    "Appointment does not belong to this doctor"
            );
        }

        labTest.setPatient(patient);
        labTest.setDoctor(doctor);
        labTest.setAppointment(appointment);
        List<com.hospital.entity.Admission> activeAdmissions =
                admissionRepository.findByPatientIdAndStatus(
                        patient.getId(),
                        "ADMITTED"
                );

        if (!activeAdmissions.isEmpty()) {
            labTest.setAdmission(activeAdmissions.get(0));
        } else {
            labTest.setAdmission(null);
        }

        labTest.setStatus("PENDING");
        labTest.setTestDate(null);
        labTest.setReportDate(null);

        return labTestRepository.save(labTest);
    }

    @Override
    public List<LabTest> getPatientLabTests(
            Long patientId) {

        return labTestRepository
                .findByPatientId(patientId);
    }


    // =========================================================
    // RECOMMENDATIONS
    // =========================================================

    @Override
    public Consultation recommendAdmission(
            Long consultationId) {

        Consultation consultation =
                getConsultation(consultationId);

        consultation.setAdmissionRecommended(true);

        return consultationRepository.save(
                consultation
        );
    }


    @Override
    public Consultation recommendOperation(
            Long consultationId) {

        Consultation consultation =
                getConsultation(consultationId);

        consultation.setOperationRecommended(true);

        return consultationRepository.save(
                consultation
        );
    }


    @Override
    public Consultation recommendDischarge(
            Long consultationId) {

        Consultation consultation =
                getConsultation(consultationId);

        consultation.setDischargeRecommended(true);

        return consultationRepository.save(
                consultation
        );
    }


    private Consultation getConsultation(
            Long consultationId) {

        return consultationRepository
                .findById(consultationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Consultation not found"
                        )
                );
    }


    // =========================================================
    // SPECIALIZATIONS
    // =========================================================

    @Override
    public List<String> getSpecializations() {

        return userRepository
                .findByRole("DOCTOR")
                .stream()

                .filter(user ->
                        user.getStatus() != null &&
                        (
                            user.getStatus()
                                    .equalsIgnoreCase("ACTIVE")
                            ||
                            user.getStatus()
                                    .equalsIgnoreCase("APPROVED")
                        )
                )

                .map(User::getSpecialization)

                .filter(specialization ->
                        specialization != null &&
                        !specialization
                                .trim()
                                .isEmpty()
                )

                .map(String::trim)

                .distinct()

                .sorted()

                .toList();
    }


    @Override
    public List<User> getDoctorsBySpecialization(
            String specialization) {

        return userRepository
                .findByRole("DOCTOR")
                .stream()

                .filter(user ->
                        user.getStatus() != null &&
                        (
                            user.getStatus()
                                    .equalsIgnoreCase("ACTIVE")
                            ||
                            user.getStatus()
                                    .equalsIgnoreCase("APPROVED")
                        )
                )

                .filter(user ->
                        user.getSpecialization() != null &&
                        user.getSpecialization()
                                .equalsIgnoreCase(
                                        specialization
                                )
                )

                .toList();
    }
    @Override
    public User getDoctorById(Long doctorId) {

        return userRepository.findById(doctorId)
                .filter(user ->
                        "DOCTOR".equalsIgnoreCase(user.getRole())
                )
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found")
                );
    }
    
    @Override
    public Appointment verifyAppointment(Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Appointment not found")
                );

        appointment.setVerified(true);

        return appointmentRepository.save(appointment);
    }
    
    @Override
    public List<Prescription> getPrescriptionsByAppointment(Long appointmentId) {

        Consultation consultation = consultationRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Consultation not found for appointment"));

        return prescriptionRepository
                .findByConsultationId(consultation.getId());
    }
    
    @Override
    public List<Vital> getVitalsByAppointment(Long appointmentId) {

        return vitalRepository.findByAppointmentId(appointmentId);
    }
}