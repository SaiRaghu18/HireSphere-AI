package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.DashboardStats;
import com.hirsphere.backend.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserDTO updateProfile(Long id, UserDTO dto);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByRole(String role);
    void deleteUser(Long id);
    UserDTO updateUserStatus(Long id, String status);
    DashboardStats getJobSeekerStats(Long userId);
    DashboardStats getRecruiterStats(Long recruiterId);
    DashboardStats getAdminStats();
}
