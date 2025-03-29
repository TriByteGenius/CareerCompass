package com.tribytegenius.CareerCompass.controller;

import com.tribytegenius.CareerCompass.dto.UserDTO;
import com.tribytegenius.CareerCompass.model.AppRole;
import com.tribytegenius.CareerCompass.model.Role;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.repository.RoleRepository;
import com.tribytegenius.CareerCompass.repository.UserRepository;
import com.tribytegenius.CareerCompass.security.UserDetailsImpl;
import com.tribytegenius.CareerCompass.security.dto.LoginRequest;
import com.tribytegenius.CareerCompass.security.dto.LoginResponse;
import com.tribytegenius.CareerCompass.security.dto.MessageResponse;
import com.tribytegenius.CareerCompass.security.dto.SignupRequest;
import com.tribytegenius.CareerCompass.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private Role userRole;
    private Role adminRole;
    private UserDetailsImpl userDetails;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;

    @BeforeEach
    public void setup() {
        // Setup test data
        userRole = new Role(AppRole.ROLE_USER);
        userRole.setId(1);

        adminRole = new Role(AppRole.ROLE_ADMIN);
        adminRole.setId(2);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Collections.singleton(userRole));

        Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(AppRole.ROLE_USER.name())
        );

        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@example.com",
                "encodedPassword",
                authorities
        );

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");
    }

    @Test
    public void testAuthenticateUser_ValidCredentials_ReturnsLoginResponse() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateTokenFromUsername(any(UserDetailsImpl.class))).thenReturn("test.jwt.token");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals(1L, loginResponse.getId());
        assertEquals("test@example.com", loginResponse.getUsername());
        assertEquals("test.jwt.token", loginResponse.getJwtToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateTokenFromUsername(userDetails);
    }

    @Test
    public void testRegisterUser_NewUser_ReturnsSuccessMessage() {
        // Arrange
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("User registered successfully", messageResponse.getMessage());

        verify(userRepository).existsByUserName("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByRoleName(AppRole.ROLE_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExists_ReturnsBadRequest() {
        // Arrange
        when(userRepository.existsByUserName(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Error: Username is already taken!", messageResponse.getMessage());

        verify(userRepository).existsByUserName("newuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists_ReturnsBadRequest() {
        // Arrange
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Error: Email is already in use!", messageResponse.getMessage());

        verify(userRepository).existsByUserName("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_WithAdminRole_AssignsCorrectRoles() {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        signupRequest.setRole(roles);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(AppRole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userRepository).existsByUserName("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(roleRepository).findByRoleName(AppRole.ROLE_ADMIN);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testCurrentUserDetails_ReturnsUserDTO() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<UserDTO> response = authController.currentUserDetails(authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO userDTO = response.getBody();
        assertEquals(1L, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getEmail());

        verify(authentication).getPrincipal();
        verify(userRepository).findById(1L);
    }

    @Test
    public void testSignOutUser_ReturnsSuccessMessage() {
        // Act
        ResponseEntity<?> response = authController.signOutUser();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("You've been logged out successfully", messageResponse.getMessage());
    }
}