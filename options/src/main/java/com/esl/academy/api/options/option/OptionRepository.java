package com.esl.academy.api.options.option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, UUID> {

    List<Option> findByOptionTypeId(UUID optionTypeId);
    Optional<Option> findByOptionName(String name);
}
