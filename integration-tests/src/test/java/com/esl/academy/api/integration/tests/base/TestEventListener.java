package com.esl.academy.api.integration.tests.base;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class TestEventListener<T extends ApplicationEvent> {

    final List<T> events = new ArrayList<>();

    @EventListener
    public void onEvent(T event) {
        events.add(event);
    }

    public void reset() {
        events.clear();
    }
}
