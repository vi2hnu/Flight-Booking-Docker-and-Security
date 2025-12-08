package org.example.emailservice.service;

import org.example.emailservice.dto.KafkaTicketDTO;
import org.example.emailservice.dto.ScheduleDTO;
import org.example.emailservice.kafka.KafkaService;
import org.example.emailservice.model.entity.Users;
import org.example.emailservice.model.enums.Gender;
import org.example.emailservice.model.enums.Meal;
import org.example.emailservice.dto.PassengerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    EmailInterface emailInterface;

    @InjectMocks
    KafkaService kafkaService;

    private ScheduleDTO schedule() {
        return new ScheduleDTO(1L, 1L, 2L, 3L, LocalDate.now(),
                LocalDateTime.now().plusHours(1), 500f, 10, 120);
    }

    private KafkaTicketDTO ticket() {
        Users users = new Users();
        users.setEmail("a@b.com");
        users.setName("Test");
        users.setGender(Gender.MALE);

        ArrayList<PassengerDTO> passengers = new ArrayList<>();
        passengers.add(new PassengerDTO("Passenger1", Gender.MALE, Meal.VEG, "1A"));

        return new KafkaTicketDTO(users, schedule(), null, passengers, "PNR1");
    }

    @Test
    void sendConformationEmail_callsService() {
        KafkaTicketDTO dto = ticket();
        kafkaService.sendConformationEmail(dto);
        verify(emailInterface).sendConformationEmail(dto);
    }

    @Test
    void sendCancellationEmail_callsService() {
        KafkaTicketDTO dto = ticket();
        kafkaService.sendCancellationEmail(dto);
        verify(emailInterface).sendCancellationEmail(dto);
    }

    @Test
    void scheduledAddedEmail_callsService() {
        ScheduleDTO s = schedule();
        kafkaService.scheduledAddedEmail(s);
        verify(emailInterface).scheduledAddedEmail(s);
    }
}
