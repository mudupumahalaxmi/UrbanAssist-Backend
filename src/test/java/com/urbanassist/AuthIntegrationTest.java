package com.urbanassist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanassist.dto.request.LoginRequest;
import com.urbanassist.dto.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCitizenRegistrationAndLoginWorkflow() throws Exception {
        String uniqueEmail = "testcitizen_" + System.currentTimeMillis() + "@urbanassist.org";

        // 1. Register new citizen
        RegisterRequest registerReq = new RegisterRequest("Test Citizen", uniqueEmail, "9876543210", "Password@123");
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(uniqueEmail))
                .andReturn();

        // 2. Login with registered credentials
        LoginRequest loginReq = new LoginRequest(uniqueEmail, "Password@123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.role").value("ROLE_CITIZEN"));
    }

    @Test
    void testRejectInvalidLoginCredentials() throws Exception {
        LoginRequest loginReq = new LoginRequest("nonexistent@urbanassist.org", "WrongPassword");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRejectInvalidRegistrationPayload() throws Exception {
        // Missing name and password too short
        RegisterRequest invalidReq = new RegisterRequest("", "invalid-email", "123", "123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void testGetCurrentUserWithValidToken() throws Exception {
        String uniqueEmail = "profile_" + System.currentTimeMillis() + "@urbanassist.org";
        RegisterRequest registerReq = new RegisterRequest("Profile Test", uniqueEmail, "9998887777", "Password@123");

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.role").value("ROLE_CITIZEN"));
    }
}
