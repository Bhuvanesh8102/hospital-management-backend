package com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.HospitalBillRepository;
import com.hospital.repository.UserRepository;

@Service
public class ReceptionistServiceImpl implements ReceptionistService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AdmissionRepository admissionRepository;
    private final HospitalBillRepository hospitalBillRepository;

    // NEW
    private final AuthService authService;

    public ReceptionistServiceImpl(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            AdmissionRepository admissionRepository,
            HospitalBillRepository hospitalBillRepository,
            AuthService authService) {

        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.admissionRepository = admissionRepository;
        this.hospitalBillRepository = hospitalBillRepository;

        // NEW
        this.authService = authService;
    }

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    @Override
    public Appointment getAppointmentByAppointmentId(
            String appointmentId) {

        return appointmentRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found"));
    }

    @Override
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    @Override
    public Appointment confirmAppointment(Long appointmentId) {

        Appointment appointment =
                getAppointment(appointmentId);

        appointment.setStatus("CONFIRMED");

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId) {

        Appointment appointment =
                getAppointment(appointmentId);

        appointment.setStatus("CANCELLED");

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment checkInAppointment(Long appointmentId) {

        Appointment appointment =
                getAppointment(appointmentId);

        appointment.setStatus("CHECKED_IN");

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment markNoShow(Long appointmentId) {

        Appointment appointment =
                getAppointment(appointmentId);

        appointment.setStatus("NO_SHOW");

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment createAppointment(Appointment appointment) {

        String generatedAppointmentId =
                "APT-" + System.currentTimeMillis();

        appointment.setAppointmentId(generatedAppointmentId);
        appointment.setStatus("PENDING");

        // Connect patient
        if (appointment.getPatient() != null
                && appointment.getPatient().getId() != null) {

            User patient = userRepository.findById(
                    appointment.getPatient().getId()
            ).orElseThrow(() ->
                    new RuntimeException("Patient not found"));

            appointment.setPatient(patient);
        }

        // Connect doctor
        if (appointment.getDoctor() != null
                && appointment.getDoctor().getId() != null) {

            User doctor = userRepository.findById(
                    appointment.getDoctor().getId()
            ).orElseThrow(() ->
                    new RuntimeException("Doctor not found"));

            appointment.setDoctor(doctor);
        }

        return appointmentRepository.save(appointment);
    }

    // =========================================================
    // WALK-IN PATIENT REGISTRATION
    // =========================================================

    @Override
    public User registerWalkInPatient(User user) {

        return authService.registerWalkInPatient(user);
    }

    // =========================================================
    // PATIENT SEARCH
    // =========================================================

    @Override
    public User searchPatientByUhid(String uhid) {

        try {

            Long patientId =
                    Long.parseLong(uhid);

            return userRepository
                    .findById(patientId)
                    .filter(user ->
                            "PATIENT".equalsIgnoreCase(
                                    user.getRole()))
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Patient not found"));

        } catch (NumberFormatException e) {

            return userRepository
                    .findByUhid(uhid)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Patient not found"));
        }
    }

    @Override
    public User searchPatientByMobile(String mobile) {

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        mobile.equals(user.getMobile())
                        &&
                        "PATIENT".equalsIgnoreCase(
                                user.getRole()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found"));
    }

    @Override
    public User searchPatientByName(String name) {

        return userRepository.findAll()
                .stream()
                .filter(user ->
                        user.getFullName() != null
                        &&
                        user.getFullName()
                                .equalsIgnoreCase(name)
                        &&
                        "PATIENT".equalsIgnoreCase(
                                user.getRole()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found"));
    }

    // =========================================================
    // ADMISSION
    // =========================================================

    @Override
    public Admission createAdmission(Admission admission) {

        // =====================================================
        // PATIENT
        // =====================================================

        if (admission.getPatient() == null
                || admission.getPatient().getId() == null) {

            throw new RuntimeException(
                    "Patient ID is required"
            );
        }

        User patient = userRepository.findById(
                admission.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Patient not found"
                )
        );

        admission.setPatient(patient);

        // =====================================================
        // DOCTOR
        // =====================================================

        if (admission.getDoctor() == null
                || admission.getDoctor().getId() == null) {

            throw new RuntimeException(
                    "Doctor ID is required"
            );
        }

        User doctor = userRepository.findById(
                admission.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Doctor not found"
                )
        );

        admission.setDoctor(doctor);

        // =====================================================
        // APPOINTMENT
        // =====================================================

        if (admission.getAppointment() == null
                || admission.getAppointment().getId() == null) {

            throw new RuntimeException(
                    "Appointment ID is required for admission"
            );
        }

        Appointment appointment =
                appointmentRepository.findById(
                        admission.getAppointment().getId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found"
                        )
                );

        // =====================================================
        // VALIDATE APPOINTMENT PATIENT
        // =====================================================

        if (appointment.getPatient() == null
                || appointment.getPatient().getId() == null
                || !appointment.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Appointment does not belong to this patient"
            );
        }

        // =====================================================
        // VALIDATE APPOINTMENT DOCTOR
        // =====================================================

        if (appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null
                || !appointment.getDoctor().getId()
                        .equals(doctor.getId())) {

            throw new RuntimeException(
                    "Appointment does not belong to this doctor"
            );
        }

        admission.setAppointment(appointment);

        // =====================================================
        // DEFAULT VALUES
        // =====================================================

        if (admission.getAdmissionDate() == null) {

            admission.setAdmissionDate(
                    java.time.LocalDate.now()
            );
        }

        if (admission.getStatus() == null
                || admission.getStatus().trim().isEmpty()) {

            admission.setStatus("ADMITTED");
        }

        // =====================================================
        // SAVE
        // =====================================================

        return admissionRepository.save(admission);
    }

    @Override
    public Admission processDischarge(
            Long admissionId) {

        Admission admission =
                admissionRepository.findById(
                        admissionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Admission not found"));

        admission.setStatus("DISCHARGED");

        return admissionRepository.save(admission);
    }

    // =========================================================
    // HOSPITAL BILL
    // =========================================================

    @Override
    public HospitalBill createHospitalBill(
            HospitalBill hospitalBill) {

        // Generate bill number
        hospitalBill.setBillNumber(
                "BILL-" + System.currentTimeMillis()
        );

        // Bill date
        hospitalBill.setBillDate(
                java.time.LocalDateTime.now()
        );

        // Payment completed immediately
        hospitalBill.setPaymentStatus("PAID");

        // ---------------------------------------------------------
        // PATIENT
        // ---------------------------------------------------------

        if (hospitalBill.getPatient() != null
                && hospitalBill.getPatient().getId() != null) {

            User patient =
                    userRepository.findById(
                            hospitalBill
                                    .getPatient()
                                    .getId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Patient not found"));

            hospitalBill.setPatient(patient);
        }

        // ---------------------------------------------------------
        // APPOINTMENT + DOCTOR
        // ---------------------------------------------------------

        if (hospitalBill.getAppointment() != null
                && hospitalBill
                        .getAppointment()
                        .getId() != null) {

            Appointment appointment =
                    appointmentRepository.findById(
                            hospitalBill
                                    .getAppointment()
                                    .getId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "Appointment not found"));

            hospitalBill.setAppointment(
                    appointment);

            // Automatically get doctor from appointment
            if (appointment.getDoctor() != null) {

                User doctor =
                        userRepository.findById(
                                appointment
                                        .getDoctor()
                                        .getId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Doctor not found"));

                hospitalBill.setDoctor(doctor);
            }

            // Automatically connect patient
            if (hospitalBill.getPatient() == null
                    && appointment.getPatient() != null) {

                hospitalBill.setPatient(
                        appointment.getPatient());
            }
        }

        // ---------------------------------------------------------
        // CALCULATE TOTAL
        // ---------------------------------------------------------

        double total =

                (hospitalBill.getRegistrationCharges()
                        != null
                        ? hospitalBill
                                .getRegistrationCharges()
                        : 0)

                +

                (hospitalBill.getConsultationCharges()
                        != null
                        ? hospitalBill
                                .getConsultationCharges()
                        : 0)

                +

                (hospitalBill.getLaboratoryCharges()
                        != null
                        ? hospitalBill
                                .getLaboratoryCharges()
                        : 0)

                +

                (hospitalBill.getAdmissionCharges()
                        != null
                        ? hospitalBill
                                .getAdmissionCharges()
                        : 0)

                +

                (hospitalBill.getRoomCharges()
                        != null
                        ? hospitalBill
                                .getRoomCharges()
                        : 0)

                +

                (hospitalBill.getOtherCharges()
                        != null
                        ? hospitalBill
                                .getOtherCharges()
                        : 0);

        hospitalBill.setTotalAmount(total);

        // ---------------------------------------------------------
        // SAVE
        // ---------------------------------------------------------

        return hospitalBillRepository.save(
                hospitalBill);
    }

    @Override
    public HospitalBill getHospitalBill(
            Long billId) {

        return hospitalBillRepository
                .findById(billId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hospital bill not found"));
    }

    @Override
    public HospitalBill completeHospitalPayment(
            Long billId) {

        HospitalBill bill =
                getHospitalBill(billId);

        bill.setPaymentStatus("PAID");

        return hospitalBillRepository.save(bill);
    }

    @Override
    public HospitalBill completeFinalBilling(
            Long billId) {

        HospitalBill bill =
                getHospitalBill(billId);

        bill.setPaymentStatus("PAID");

        return hospitalBillRepository.save(bill);
    }

    // =========================================================
    // HELPER
    // =========================================================

    private Appointment getAppointment(
            Long appointmentId) {

        return appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found"));
    }
    @Override
    public List<Admission> getPatientAdmissions(Long patientId) {

        return admissionRepository.findByPatientId(patientId);
    }
}