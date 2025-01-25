package com.example.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.backend.model.LoanEntity;
import com.example.backend.repository.LoanRepository;

@Service
public class NotificationService {

    private final LoanRepository loanRepository;
    private final EmailService emailService;

    public NotificationService(LoanRepository loanRepository, EmailService emailService) {
        this.loanRepository = loanRepository;
        this.emailService = emailService;
    }

    /**
     * Runs every day at 11:00 AM server time
     */
    @Scheduled(cron = "0 43 16 * * ?")
    public void sendDueDateReminders() {
        LocalDate today = LocalDate.now();

        // Find all loans that are due today or are overdue and not returned
        List<LoanEntity> dueToday = loanRepository.findAllDueToday(today);

        for (LoanEntity loan : dueToday) {
            String personEmail = loan.getPerson().getEmail();

            // Subject Line
            String subject = "Friendly Reminder: Please Return Your Borrowed Media";

            // Friendly Email Body
            String body = String.format(
                    "Hi %s,\n\n"
                            + "Just a quick note to remind you to return the media you borrowed, **\"%s\"**, to our library.\n\n"
                            + "If you've already returned it, no worries—thanks a bunch! But if you still have it, I'd love to have it back soon so others can enjoy it too.\n\n"
                            + "If you have any questions or need more time, just let us know. We're happy to work something out.\n\n"
                            + "Thanks again, and hope you're doing well!\n\n"
                            + "Best,\n"
                            + "Your Media Library Buddy \n"
                            +"AdamPos"
                    ,
                    loan.getPerson().getFirstName(),
                    loan.getMedia().getTitle()
            );

            // Send the email
            emailService.sendEmail(personEmail, subject, body);
        }
    }
}
