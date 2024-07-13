package client.utils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.api.MailSenderController;
import server.services.MailSenderService;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MailSenderController.class)
public class MailRequestTest {
    private MockMvc mockMvc;

    @MockBean
    private MailSenderService service;

    private ServerUtils serverUtils;

    @Mock
    private Client mockClient;

    @Mock
    private WebTarget mockWebTarget = Mockito.mock(WebTarget.class);

    @Mock
    private Invocation.Builder mockBuilder = Mockito.mock(Invocation.Builder.class);

    @Mock
    private Response mockResponse = Mockito.mock(Response.class);

    @BeforeEach
    public void setUp() {
        System.setProperty("from.email", "splittyoopp@gmail.com");
        System.setProperty("from.password", "dqthehoktabtjdmq");
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MailSenderController(service)).build();
        this.serverUtils = new ServerUtils();
        this.serverUtils.setClient(mockClient);

        when(mockClient.target(Mockito.anyString()))
                .thenReturn(mockWebTarget);
        when(mockWebTarget.path(Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.queryParam(Mockito.anyString(), Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
    }

    @Test
    public void testSendMail() throws Exception {
        String mail = "test@gmail.com";
        String sub = "testSub";
        String bod = "testBod";
        String from = System.getProperty("from.email");
        String password = System.getProperty("from.password");
        when(service.sendNewMail(mail, sub, bod)).thenReturn(sub);
        MvcResult result = this.mockMvc.perform(
                        post("/api/mail")
                                .queryParam("mail", mail)
                                .queryParam("sub", sub)
                                .queryParam("bod", bod)
                                .queryParam("from", from)
                                .queryParam("password", password)
                                .contentType(APPLICATION_JSON)
                                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        when(mockBuilder.post(Entity.entity(sub, APPLICATION_JSON), String.class))
                .thenReturn(sub);
        assertEquals(content, serverUtils.sendMail(mail, sub, bod));
    }
}
