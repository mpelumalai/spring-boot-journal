package com.spring;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.spring.SprintBootWebApplication;
import com.spring.domain.JournalEntry;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SprintBootWebApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class SprintBootWebApplicationTests
{

	private final String			SPRING_BOOT_MATCH	= "Spring Boot";
	private final String			CLOUD_MATCH			= "Cloud";
	private MediaType				contentType			= new MediaType(MediaType.APPLICATION_JSON.getType(),
	                                                                    MediaType.APPLICATION_JSON.getSubtype(),
	                                                                    Charset.forName("utf8"));
	// Loads a WebApplicationContext and provides a mock servlet environment.
	private MockMvc					mockMvc;

	@Value("${sample.prop}")
	private String					sample;

	@Autowired
	private WebApplicationContext	webApplicationContext;

	@Autowired
	private MappingJackson2HttpMessageConverter converter;
	

	@Before
	public void setup() throws Exception
	{
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		System.out.println("Property test value = " + sample);
	}

	@Test
	public void getAll() throws Exception
	{
		mockMvc.perform(get("/journal/all"))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andExpect(jsonPath("$", iterableWithSize(5)))
		       .andExpect(jsonPath("$[0]['title']", containsString(SPRING_BOOT_MATCH)));
	}

	@Test
	public void findByTitle() throws Exception
	{
		mockMvc.perform(get("/journal/findBy/title/" + CLOUD_MATCH))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType(contentType))
		       .andExpect(jsonPath("$", iterableWithSize(1)))
		       .andExpect(jsonPath("$[0]['title']", containsString(CLOUD_MATCH)));
	}

	@Test
	public void add() throws Exception
	{
		mockMvc.perform(post("/journal").content(this.toJsonString(new JournalEntry("Spring Boot Testing",
		                                                                            "Create Spring Boot Tests",
		                                                                            "18-09-2017")))
		                                .contentType(contentType))
		       .andExpect(status().isOk());
	}

	protected String toJsonString(Object obj) throws IOException
	{
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.converter.write(obj, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
