package com.esl.academy.api.user.supervisor;

import com.esl.academy.api.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.esl.academy.api.user.supervisor.SupervisorMapper.INSTANCE;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisorService {
    private final SupervisorRepository repository;

    public SupervisorDto getSupervisorById(UUID id){
        final var supervisor = repository.findById(id).orElseThrow(() ->
            new NotFoundException("Supervisor not found"));
        return INSTANCE.map(supervisor);
    }
}
