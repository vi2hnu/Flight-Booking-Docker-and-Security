package org.example.bookingservice.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalHandlerValidationTest {

    @RestController
    @Validated
    static class DummyController {

        @PostMapping("/validateUser")
        public void validate(@RequestBody @Valid UserDTO userDTO) {}

        @GetMapping("/userNotFound")
        public void user() { throw new UsersNotFoundException("user not found"); }

        @GetMapping("/ticketNotFound")
        public void ticket() { throw new TicketNotFoundException("ticket not found"); }

        @GetMapping("/invalidScheduleTime")
        public void invalidTime() { throw new InvalidScheduleTimeException("invalid schedule"); }

        @GetMapping("/seatNotAvailable")
        public void seat() { throw new SeatNotAvailableException("seat not available"); }

        @GetMapping("/generic")
        public void generic() { throw new RuntimeException("generic error"); }
    }

    static class UserDTO {
        @NotBlank(message = "name cannot be blank")
        private String name;

        @Size(min = 5, message = "password must be at least 5 characters")
        private String password;

        public UserDTO() {}
        public UserDTO(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Test
    void testAllExceptionHandlersIncludingValidation() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String invalidJson = "{ \"name\": \"\", \"password\": \"123\" }";
        mockMvc.perform(post("/validateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("name cannot be blank"))
                .andExpect(jsonPath("$.password").value("password must be at least 5 characters"));

        mockMvc.perform(get("/userNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found"));

        mockMvc.perform(get("/ticketNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ticket not found"));

        mockMvc.perform(get("/invalidScheduleTime"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("invalid schedule"));

        mockMvc.perform(get("/seatNotAvailable"))
                .andExpect(status().isConflict())
                .andExpect(content().string("seat not available"));

        mockMvc.perform(get("/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("generic error"));
    }
}
