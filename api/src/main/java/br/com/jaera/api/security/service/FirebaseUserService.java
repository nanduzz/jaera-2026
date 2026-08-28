package br.com.jaera.api.security.service;

import br.com.jaera.api.security.domain.JaeraPrincipal;
import br.com.jaera.api.security.domain.Role;
import br.com.jaera.api.security.domain.UserRole;
import br.com.jaera.api.security.repository.RoleRepository;
import br.com.jaera.api.security.repository.UserRoleRepository;
import br.com.jaera.api.users.domain.User;
import br.com.jaera.api.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    public JaeraPrincipal loadOrCreateUser(String firebaseUid, String email) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> createUser(firebaseUid, email));

        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        return JaeraPrincipal.builder()
                .id(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .email(user.getEmail())
                .username(user.getUsername())
                .authorities(authorities)
                .build();
    }

    private User createUser(String firebaseUid, String email) {
        log.info("Auto-provisioning new user for Firebase UID: {}", firebaseUid);

        User newUser = User.builder()
                .firebaseUid(firebaseUid)
                .email(email)
                .username(email)
                .build();

        User savedUser = userRepository.save(newUser);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found in database"));

        userRoleRepository.save(UserRole.builder()
                .userId(savedUser.getId())
                .roleId(userRole.getId())
                .build());

        return savedUser;
    }
}
