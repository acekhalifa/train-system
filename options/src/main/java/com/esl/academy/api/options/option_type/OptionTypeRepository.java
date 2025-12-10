package com.esl.academy.api.options.option_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionTypeRepository extends JpaRepository<OptionType, UUID> {
    Optional<OptionType> findByName(String name);
}
