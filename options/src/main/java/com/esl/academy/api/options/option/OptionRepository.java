package com.esl.academy.api.options.option;

import com.esl.academy.api.options.option_type.OptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, UUID> {

    List<Option> findByOptionType_OptionTypeId(UUID optionTypeId);

    List<Option> findByOptionType(OptionType optionType);
}
