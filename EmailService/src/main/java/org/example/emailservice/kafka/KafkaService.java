package org.example.emailservice.kafka;

import org.example.emailservice.dto.KafkaTicketDTO;
import org.example.emailservice.dto.ScheduleDTO;
import org.example.emailservice.service.EmailInterface;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    private final EmailInterface emailInterface;

    public KafkaService(EmailInterface emailInterface){
        this.emailInterface = emailInterface;
    }

    @KafkaListener(topics = "ticket.booked")
    public void sendConformationEmail(KafkaTicketDTO kafkaTicketDTO){
        emailInterface.sendConformationEmail(kafkaTicketDTO);
    }

    @KafkaListener(topics = "ticket.cancelled")
    public void sendCancellationEmail(KafkaTicketDTO kafkaTicketDTO){
        emailInterface.sendCancellationEmail(kafkaTicketDTO);
    }

    @KafkaListener(topics = "schedule.added")
    public void scheduledAddedEmail(ScheduleDTO scheduleDTO){
        emailInterface.scheduledAddedEmail(scheduleDTO);
    }
}
