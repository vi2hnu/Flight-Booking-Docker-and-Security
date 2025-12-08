package org.example.flightservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    public static class TestDto {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Age cannot be null")
        private Integer age;

        public TestDto() {} // Default constructor required

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @RestController
    static class DummyController {
        @GetMapping("/cityNotFound")
        public void city() {
            throw new CityNotFoundException("city not found");
        }

        @GetMapping("/flightNotFound")
        public void flight() {
            throw new FlightNotFoundException("flight not found");
        }

        @GetMapping("/scheduleConflict")
        public void conflict() {
            throw new ScheduleConflictException("conflict");
        }

        @GetMapping("/invalidScheduleTime")
        public void invalidTime() {
            throw new InvalidScheduleTimeException("invalid time");
        }

        @GetMapping("/scheduleNotFound")
        public void schedule() {
            throw new ScheduleNotFoundException("schedule not found");
        }

        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("generic");
        }

        @PostMapping("/validate")
        public void validate(@Valid @RequestBody TestDto dto) {
            // Will trigger validation
        }
    }

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Test
    void testValidationException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        TestDto invalidDto = new TestDto();

        mockMvc.perform(post("/validate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.age").value("Age cannot be null"));
    }

    @Test
    void testValidationException_PartialInvalid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        TestDto dto = new TestDto();
        dto.setAge(25);

        mockMvc.perform(post("/validate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.age").doesNotExist());
    }

    @Test
    void testCityNotFoundException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/cityNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("city not found"));
    }

    @Test
    void testFlightNotFoundException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/flightNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("flight not found"));
    }

    @Test
    void testScheduleConflictException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/scheduleConflict"))
                .andExpect(status().isConflict())
                .andExpect(content().string("conflict"));
    }

    @Test
    void testInvalidScheduleTimeException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/invalidScheduleTime"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid time"));
    }

    @Test
    void testScheduleNotFoundException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/scheduleNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("schedule not found"));
    }

    @Test
    void testGenericException() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("generic"));
    }

    @Test
    void testGlobalErrorBranch() throws Exception {
        Object target = new Object();

        BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new ObjectError("object", "This is a global error"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationException(ex);

        assertEquals("This is a global error", response.getBody().get("globalError"));
    }
}