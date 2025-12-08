package org.example.emailservice.service;
import org.example.emailservice.dto.KafkaTicketDTO;
import org.example.emailservice.dto.PassengerDTO;
import org.example.emailservice.dto.ScheduleDTO;
import org.example.emailservice.model.entity.Users;
import org.example.emailservice.model.enums.Gender;
import org.example.emailservice.model.enums.Meal;
import org.example.emailservice.service.implementation.EmailImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailImplementationTest {

    @Mock
    JavaMailSender javaMailSender;

    @InjectMocks
    EmailImplementation emailImplementation;

    private ScheduleDTO schedule() {
        return new ScheduleDTO(1L, 10L, 2L, 3L, LocalDate.now(),
                LocalDateTime.now().plusHours(1), 500f, 50, 120);
    }

    private Users user() {
        Users u = new Users();
        u.setId(1L);
        u.setName("TestUser");
        u.setEmail("test@mail.com");
        u.setGender(Gender.MALE);
        return u;
    }

    @Test
    void sendConformationEmail_sendsMail() {
        PassengerDTO p = new PassengerDTO("John Doe", Gender.MALE, Meal.VEG, "A1");

        KafkaTicketDTO dto = new KafkaTicketDTO(
                user(),
                schedule(),
                null,
                List.of(p),
                "PNR123"
        );

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailImplementation.sendConformationEmail(dto);

        verify(javaMailSender).send(captor.capture());

        String text = captor.getValue().getText();

        assertTrue(text.contains("PNR123"));
        assertTrue(text.contains("Seat A1"));
        assertTrue(text.contains("John Doe"));
    }

    @Test
    void sendCancellationEmail_sendsMail() {
        KafkaTicketDTO dto = new KafkaTicketDTO(
                user(),
                schedule(),
                null,
                List.of(),
                "PNR999"
        );

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailImplementation.sendCancellationEmail(dto);

        verify(javaMailSender).send(captor.capture());

        assertTrue(captor.getValue().getText().contains("PNR999"));
    }

    @Test
    void scheduledAddedEmail_sendsMail() {
        ScheduleDTO s = schedule();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailImplementation.scheduledAddedEmail(s);

        verify(javaMailSender).send(captor.capture());

        String text = captor.getValue().getText();

        assertTrue(text.contains("FromID: 2"));
        assertTrue(text.contains("ToID: 3"));
    }
}

