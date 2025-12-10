package com.esl.academy.api.certification;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CertificateData(
     String internName,
     String trackName,
     String supervisorName,
     LocalDate startDate,
     LocalDate endDate,
     LocalDate completionDate
    ){}
