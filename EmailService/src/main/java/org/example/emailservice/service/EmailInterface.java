package org.example.emailservice.service;

import org.example.emailservice.dto.KafkaTicketDTO;
import org.example.emailservice.dto.ScheduleDTO;

public interface EmailInterface {
    void sendConformationEmail(KafkaTicketDTO kafkaTicketDTO);
    void sendCancellationEmail(KafkaTicketDTO kafkaTicketDTO);
    void scheduledAddedEmail(ScheduleDTO scheduleDTO);
}
