package com.example.demo.service;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SystemUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ========================= REGISTER =========================

public void register(RegisterDto request) {

    if (repository.findByUsername(request.getUsername()).isPresent()) {
        throw new BusinessValidationException("Username already exists.");
    }

    if (repository.findByEmail(request.getEmail()).isPresent()) {
        throw new BusinessValidationException("Email already registered.");
    }

    if (request.getRole() == SystemUser.UserRole.COLLECTION_AGENT &&
            !"AGENT2026".equals(request.getInviteCode())) {

        throw new BusinessValidationException("Invalid Collection Agent Invite Code.");
    }

    if (request.getRole() == SystemUser.UserRole.RECYCLING_COORDINATOR &&
            !"COORD2026".equals(request.getInviteCode())) {

        throw new BusinessValidationException("Invalid Recycling Coordinator Invite Code.");
    }

    if (request.getRole() == SystemUser.UserRole.FACILITY_MANAGER &&
            !"FACILITY2026".equals(request.getInviteCode())) {

        throw new BusinessValidationException("Invalid Facility Manager Invite Code.");
    }

    if (request.getRole() == SystemUser.UserRole.ADMIN &&
            !"ADMIN2026".equals(request.getInviteCode())) {

        throw new BusinessValidationException("Invalid Admin Invite Code.");
    }

    SystemUser user = SystemUser.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();

    repository.save(user);
}

    public AuthResponseDto login(AuthRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        SystemUser user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        UserDetails userDetails = new User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .id(user.getId())
                .rewardPoints(user.getRewardPoints())
                .organicWaste(user.getOrganicWaste())
                .recyclableWaste(user.getRecyclableWaste())
                .nonRecyclableWaste(user.getNonRecyclableWaste())
                .hazardousWaste(user.getHazardousWaste())
                .collectedWasteBalance(user.getCollectedWasteBalance())
                .totalContributedKg(user.getTotalContributedKg())
                .pendingWasteKg(user.getPendingWasteKg())
                .build();
    }

    // ========================= PROFILE =========================

    public SystemUser getProfile(Long id) {

        return repository.findById(id).orElseThrow();
    }
}