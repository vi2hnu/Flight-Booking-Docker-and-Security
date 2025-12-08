package org.example.emailservice.service.implementation;

import org.example.emailservice.dto.KafkaTicketDTO;
import org.example.emailservice.dto.ScheduleDTO;
import org.example.emailservice.service.EmailInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailImplementation implements EmailInterface {

    private final JavaMailSender javaMailSender;

    public EmailImplementation(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendConformationEmail(KafkaTicketDTO kafkaTicketDTO) {
        ScheduleDTO scheduleDTO = kafkaTicketDTO.scheduleDTO();

        StringBuilder passengers = new StringBuilder();
        kafkaTicketDTO.passengers().forEach(p ->
                passengers.append(p.name())
                        .append(" - Seat ")
                        .append(p.seatPos())
                        .append("\n")
        );

        String body =
                "Your ticket is booked.\n\n" +
                        "Trip:\n" +
                        "PNR: " + kafkaTicketDTO.pnr()+"\n"+
                        "From: " + scheduleDTO.fromCityId() + "\n" +
                        "To: " + scheduleDTO.toCityId() + "\n" +
                        "Date: " + scheduleDTO.departureDate() + "\n" +
                        "Time: " + scheduleDTO.departureTime() + "\n" +
                        "Price: " + scheduleDTO.price() + "\n\n" +
                        "Passengers:\n" +
                        passengers +
                        "\nThank you.";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(sender);
        mail.setTo(kafkaTicketDTO.user().getEmail());
        mail.setSubject("Booking Confirmation");
        mail.setText(body);
        javaMailSender.send(mail);
    }


    @Override
    public void sendCancellationEmail(KafkaTicketDTO kafkaTicketDTO) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom(sender);
        mail.setTo(kafkaTicketDTO.user().getEmail());
        mail.setText("Your booking has been Cancelled for ticket with pnr: "+kafkaTicketDTO.pnr());
        mail.setSubject("Booking Cancelled");
        javaMailSender.send(mail);
    }

    @Override
    public void scheduledAddedEmail(ScheduleDTO scheduleDTO) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom(sender);
        mail.setTo(sender); //should be admin here, once auth and access control has been learnt this can be implemented properly
        String body =
                "Schedule has been added\n\n" +
                        "FromID: " + scheduleDTO.fromCityId() + "\n" +
                        "ToID: " + scheduleDTO.toCityId() + "\n" +
                        "Date: " + scheduleDTO.departureDate() + "\n" +
                        "Time: " + scheduleDTO.departureTime() + "\n" +
                        "Price: " + scheduleDTO.price() + "\n\n";

        mail.setText(body);
        mail.setSubject("Schedule Created");
        javaMailSender.send(mail);
    }
}
