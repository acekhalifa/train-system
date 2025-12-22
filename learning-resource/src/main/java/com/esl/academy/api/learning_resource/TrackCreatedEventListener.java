package com.esl.academy.api.learning_resource;

import com.esl.academy.api.event.TrackCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TrackCreatedEventListener {

    private final LearningResourceService learningResourceService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(TrackCreatedEvent event) {
        learningResourceService.generateLearningResourceTemplates(
            event.track()
        );
    }
}
