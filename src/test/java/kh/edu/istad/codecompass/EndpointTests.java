package kh.edu.istad.codecompass;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointTests {
    @Autowired
    private MockMvc mockMvc;

    // ---------------------------
    // Swagger & Public Docs
    // ---------------------------
    @Test
    void shouldPermitSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPermitApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Auth
    // ---------------------------
    @Test
    void shouldPermitRegister() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPermitLogin() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPermitRefresh() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/refresh"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/reset-password"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidNonAdminResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/reset-password"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorRequestResetPassword() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/auth/request-reset-password"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Badges
    // ---------------------------
    @Test
    void shouldPermitVerifiedBadges() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/badges/verified"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorAddBadgeToPackage() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/badges/add-to-package"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminUnverifiedBadges() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/badges/unverified"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousBadges() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/badges"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------
    // Creator Requests
    // ---------------------------
    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberCreateCreatorRequest() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/creator-requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminGetCreatorRequests() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/creator-requests"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Hints
    // ---------------------------
    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorPatchHint() throws Exception {
        mockMvc.perform(patch("/api/v1/code-compass/hints/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousPatchHint() throws Exception {
        mockMvc.perform(patch("/api/v1/code-compass/hints/1"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------
    // Submissions
    // ---------------------------
    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberRunBatch() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/submissions/run/batch"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorBatchSubmission() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/submissions/batch"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorSubmission() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/submissions"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Leaderboard
    // ---------------------------
    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberLeaderboardMe() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/leaderboard/me"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousLeaderboardMe() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/leaderboard/me"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------
    // Packages
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminPatchPackage() throws Exception {
        mockMvc.perform(patch("/api/v1/code-compass/packages/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberGetPackages() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/packages"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Problems
    // ---------------------------
    @Test
    void shouldPermitProblemVerified() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/problems/verified"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminUnverifiedProblems() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/problems/unverified"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldAllowAuthenticatedProblemMe() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/problems/123/me"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousProblemMe() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/problems/123/me"))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------
    // Roles
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAssignRole() throws Exception {
        mockMvc.perform(put("/api/v1/code-compass/roles/assign-role"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Solutions
    // ---------------------------
    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorPostSolution() throws Exception {
        mockMvc.perform(post("/api/v1/code-compass/solutions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberGetSolutionsByProblem() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/solutions/problem/1"))
                .andExpect(status().isOk());
    }

    // ---------------------------
    // Users
    // ---------------------------
    @Test
    @WithMockUser(roles = "CREATOR")
    void shouldAllowCreatorSearchUsers() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/users/search"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SUBSCRIBER")
    void shouldAllowSubscriberUpdateUser() throws Exception {
        mockMvc.perform(patch("/api/v1/code-compass/users/update/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectAnonymousUserSearch() throws Exception {
        mockMvc.perform(get("/api/v1/code-compass/users/search"))
                .andExpect(status().isUnauthorized());
    }
}
