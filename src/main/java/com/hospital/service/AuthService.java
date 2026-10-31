
package com.hospital.service;

import java.util.List;
import com.hospital.entity.User;

public interface AuthService {

    User registerPatient(User user);

    User registerWalkInPatient(User user);

    User registerStaff(User user);

    String login(String email, String password);

    List<User> getAllUsers();

    User updatePatientProfile(
            String loggedInEmail,
            String fullName,
            String email,
            String oldPassword,
            String newPassword,
            String confirmNewPassword
    );
}
