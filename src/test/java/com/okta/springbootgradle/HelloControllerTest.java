package com.okta.springbootgradle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HelloControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void homeShouldFail() throws Exception {
        this.mockMvc
            .perform(get("/"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void homeShouldPass() throws Exception {
        this.mockMvc
            .perform(get("/").with(user("Mister Tester")))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello Mister Tester!")));
    }

    @Test
    public void anonymous() throws Exception {
        this.mockMvc
            .perform(get("/allow-anonymous"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello whoever!")));;
    }

}