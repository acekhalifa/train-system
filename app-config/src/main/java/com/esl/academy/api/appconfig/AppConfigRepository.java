package com.esl.academy.api.appconfig;

import com.esl.academy.api.appconfig.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, String> {

}
