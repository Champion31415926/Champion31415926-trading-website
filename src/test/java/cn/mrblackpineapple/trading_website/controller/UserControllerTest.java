package cn.mrblackpineapple.trading_website.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    // 1) 注册用例
    @Test
    @Order(1)
    public void testRegister() throws Exception {
        String body = """
                {
                  "userAccount": "test_user_ci",
                  "userPwd": "password123",
                  "userEmail": "test_ci@example.com",
                  "userName": "Test CI"
                }
                """;

        String resp = mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(resp);
        assertThat(node.get("code").asInt()).isEqualTo(200);
    }


    @Test
    @Order(2)
    public void testLoginAndAccessProtected() throws Exception {
        String loginBody = """
                {
                  "userAccount": "test_user_ci",
                  "userPwd": "password123"
                }
                """;

        String loginResp = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode loginNode = objectMapper.readTree(loginResp);
        assertThat(loginNode.at("/data/token").isMissingNode()).isFalse();
        String token = loginNode.at("/data/token").asText();

        String protectedResp = mockMvc.perform(post("/api/trading-website/v1.0/interaction/queryUser")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode protectedNode = objectMapper.readTree(protectedResp);
        assertThat(protectedNode.get("code").asInt()).isEqualTo(200);
    }
}
