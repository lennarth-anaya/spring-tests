package org.lrth.springtests.controller;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.lrth.springtests.dto.request.SaveAllRequest;
import org.lrth.springtests.dto.request.StudentRequest;
import org.lrth.springtests.dto.request.SubjectRequest;
import org.lrth.springtests.dto.request.TopicRequest;
import org.lrth.springtests.enums.DifficultyEnum;
import org.lrth.springtests.exception.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BeanValidationController.class)
@WithMockUser
@Import(GlobalExceptionHandler.class)
public class BeanValidationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void saveAll_invalid_requestRootValidations() throws JsonProcessingException, Exception {
        // GIVEN
        final String path = "/someResource/addAll";
        final SaveAllRequest saveAllRequest = new SaveAllRequest();
        
        // WHEN, THEN
        mockMvc.perform(post(path)
                .content(objectMapper.writeValueAsString(saveAllRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors['student']", is("student is mandatory")));
    }
    
    @Test
    public void saveAll_invalid_studentValidations() throws JsonProcessingException, Exception {
        // GIVEN
        final String path = "/someResource/addAll";
        final SaveAllRequest saveAllRequest = new SaveAllRequest();
        final StudentRequest student = new StudentRequest();
        saveAllRequest.setStudent(student);
        
        // WHEN, THEN
        MvcResult res = mockMvc.perform(post(path)
                .content(objectMapper.writeValueAsString(saveAllRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors['student.name']", is("name is mandatory")))
                .andExpect(jsonPath("$.errors['student.subjects']", is("student must have at least one subject")))
                .andReturn();
        System.out.println("response: " + res.getResponse().getContentAsString());
    }

    @Test
    public void saveAll_invalid_subjectValidations() throws JsonProcessingException, Exception {
        // GIVEN
        final String path = "/someResource/addAll";
        final SaveAllRequest saveAllRequest = new SaveAllRequest();
        final StudentRequest student = new StudentRequest();
        final SubjectRequest subject = new SubjectRequest();

        saveAllRequest.setStudent(student);
        student.setName("Salma");
        student.setSubjects(Set.of(subject));

        // WHEN, THEN
        MvcResult res = mockMvc.perform(post(path)
                .content(objectMapper.writeValueAsString(saveAllRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors['student.subjects[].name']", is("name is mandatory")))
                .andExpect(jsonPath("$.errors['student.subjects[].introduction']", is("introduction is mandatory")))
                .andExpect(jsonPath("$.errors['student.subjects[].topics']", is("subject must have at least one topic")))
                .andExpect(jsonPath("$.errors['student.subjects[].difficulty']", is("difficulty is mandatory")))
                .andReturn();
        System.out.println("response: " + res.getResponse().getContentAsString());
    }

    @Test
    public void saveAll_invalid_topicsValidation() throws JsonProcessingException, Exception {
        // GIVEN
        final String path = "/someResource/addAll";
        final SaveAllRequest saveAllRequest = new SaveAllRequest();
        final StudentRequest student = new StudentRequest();
        final SubjectRequest subject = new SubjectRequest();
        final TopicRequest topic = new TopicRequest();

        saveAllRequest.setStudent(student);

        student.setName("Salma");
        student.setSubjects(Set.of(subject));

        subject.setName("History of Colombia");
        subject.setIntroduction("a very long introduction...");
        subject.setTopics(Set.of(topic));
        subject.setDifficulty(DifficultyEnum.EASY);

        // WHEN, THEN
        MvcResult res = mockMvc.perform(post(path)
                .content(objectMapper.writeValueAsString(saveAllRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.errors['student.subjects[].topics[].name']", is("must not be blank")))
                .andReturn();
        System.out.println("response: " + res.getResponse().getContentAsString());
    }

    @Test
    public void saveAll_success() throws JsonProcessingException, Exception {
        // GIVEN
        final String path = "/someResource/addAll";
        final SaveAllRequest saveAllRequest = new SaveAllRequest();
        final StudentRequest student = new StudentRequest();
        final SubjectRequest subject = new SubjectRequest();
        final TopicRequest topic = new TopicRequest();

        saveAllRequest.setStudent(student);

        student.setName("Salma");
        student.setSubjects(Set.of(subject));

        subject.setName("History of Colombia");
        subject.setIntroduction("a very long introduction...");
        subject.setTopics(Set.of(topic));
        subject.setDifficulty(DifficultyEnum.EASY);

        topic.setName("Expedicion Botanica del Comun");
        
        // WHEN, THEN
        MvcResult res = mockMvc.perform(post(path)
                .content(objectMapper.writeValueAsString(saveAllRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.message", is("nothing was really done")))
                .andReturn();
        System.out.println("response: " + res.getResponse().getContentAsString());
    }

}
