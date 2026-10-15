package com.urbanassist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanassist.dto.request.LoginRequest;
import com.urbanassist.dto.request.RegisterRequest;
import com.urbanassist.entity.Role;
import com.urbanassist.entity.User;
import com.urbanassist.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ComprehensiveSecurityAndRoleTest — Verifies all Phase 11 security, role, and authentication requirements.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ComprehensiveSecurityAndRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("1. Valid citizen registration")
    void testValidCitizenRegistration() throws Exception {
        String email = "valid_reg_" + System.currentTimeMillis() + "@urbanassist.org";
        RegisterRequest req = new RegisterRequest("Sunita Rao", email, "9876543210", "StrongPass@123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.role").value("ROLE_CITIZEN"));
    }

    @Test
    @DisplayName("2. Invalid registration input rejected")
    void testInvalidRegistrationInput() throws Exception {
        RegisterRequest req = new RegisterRequest("", "bad-email", "123", "short");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("3. Duplicate email registration rejected")
    void testDuplicateEmailRejected() throws Exception {
        String email = "dup_" + System.currentTimeMillis() + "@urbanassist.org";
        RegisterRequest req1 = new RegisterRequest("User One", email, "9876543210", "Pass@12345");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated());

        // Try registering again with same email
        RegisterRequest req2 = new RegisterRequest("User Two", email, "9876543211", "Pass@67890");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    @DisplayName("4. Password hashing verified (not plaintext in DB)")
    void testPasswordHashing() throws Exception {
        String email = "hash_check_" + System.currentTimeMillis() + "@urbanassist.org";
        String plainPassword = "MySecretPassword!2026";
        RegisterRequest req = new RegisterRequest("Hash Tester", email, "9876543210", plainPassword);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(email).orElseThrow();
        assertThat(user.getPasswordHash()).isNotEqualTo(plainPassword);
        assertThat(passwordEncoder.matches(plainPassword, user.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("5. Public registration cannot create staff or admin accounts")
    void testPublicRegistrationAlwaysAssignsRoleCitizen() throws Exception {
        String email = "role_attempt_" + System.currentTimeMillis() + "@urbanassist.org";
        // Attempting to send custom fields; RegisterRequest has no role field and server sets ROLE_CITIZEN
        String payload = "{\"fullName\":\"Infiltrator\",\"email\":\"" + email + "\",\"password\":\"Pass@12345\",\"role\":\"ROLE_ADMIN\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.role").value("ROLE_CITIZEN"));

        User user = userRepository.findByEmail(email).orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_CITIZEN);
    }

    @Test
    @DisplayName("6. Valid citizen login")
    void testValidCitizenLogin() throws Exception {
        String email = "citizen_login_" + System.currentTimeMillis() + "@urbanassist.org";
        RegisterRequest regReq = new RegisterRequest("Citizen Login", email, "9876543210", "Pass@12345");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = new LoginRequest(email, "Pass@12345");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.role").value("ROLE_CITIZEN"));
    }

    @Test
    @DisplayName("7. Valid staff login")
    void testValidStaffLogin() throws Exception {
        LoginRequest loginReq = new LoginRequest("staff@urbanassist.gov", "Staff@123456");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.role").value("ROLE_STAFF"));
    }

    @Test
    @DisplayName("8. Valid admin login")
    void testValidAdminLogin() throws Exception {
        LoginRequest loginReq = new LoginRequest("admin@urbanassist.gov", "Admin@123456");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("9. Invalid password rejected")
    void testInvalidPasswordRejected() throws Exception {
        LoginRequest loginReq = new LoginRequest("admin@urbanassist.gov", "WrongPassword!999");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("10. JWT token validation on protected endpoint")
    void testJwtTokenValidation() throws Exception {
        // Unauthenticated request to /api/auth/me should fail
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());

        // Authenticated request with valid token succeeds
        LoginRequest loginReq = new LoginRequest("admin@urbanassist.gov", "Admin@123456");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@urbanassist.gov"));

        // Malformed token fails
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid.token.payload"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("11. Citizen role authorization: Citizen cannot access admin or staff endpoints")
    void testCitizenForbiddenOnAdminAndStaffEndpoints() throws Exception {
        String email = "citizen_rbac_" + System.currentTimeMillis() + "@urbanassist.org";
        RegisterRequest regReq = new RegisterRequest("RBAC Citizen", email, "9876543210", "Pass@12345");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        // Admin endpoint forbidden
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        // Staff endpoint forbidden
        mockMvc.perform(get("/api/staff/queue")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("12. Staff role authorization: Staff can access staff queue but not admin user management")
    void testStaffCanAccessStaffQueueButNotAdmin() throws Exception {
        LoginRequest loginReq = new LoginRequest("staff@urbanassist.gov", "Staff@123456");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // Staff queue allowed
        mockMvc.perform(get("/api/staff/queue")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Admin user management forbidden
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("13. Admin role authorization: Admin can access admin user management and staff queue")
    void testAdminCanAccessAdminAndStaff() throws Exception {
        LoginRequest loginReq = new LoginRequest("admin@urbanassist.gov", "Admin@123456");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // Admin endpoint allowed
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Staff queue allowed (Admin has oversight)
        mockMvc.perform(get("/api/staff/queue")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("14. Unauthenticated protected API access rejected")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/complaints/my"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/staff/queue"))
                .andExpect(status().isUnauthorized());
    }
}
