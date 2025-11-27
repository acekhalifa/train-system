package com.esl.academy.api.integration.tests.base;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@TestConfiguration
@Aspect
@Component
public class TestClockConfiguration {

    private static Clock currentClock = Clock.systemUTC();

    @Bean
    public Clock clock() {
        return currentClock;
    }

    /**
     * AOP interceptor for OffsetDateTime.now() method
     */
    @Around("execution(* java.time.OffsetDateTime.now(..))")
    public OffsetDateTime interceptNow(ProceedingJoinPoint joinPoint) {
        return OffsetDateTime.now(currentClock);
    }

    /**
     * Set a fixed time for testing OTP scenarios
     * @param instant The fixed time to set
     */
    public void setFixedTime(Instant instant) {
        currentClock = Clock.fixed(instant, ZoneOffset.UTC);
    }

    /**
     * Simulate time passing for OTP expiry testing
     * @param seconds Number of seconds to advance the clock
     */
    public void advanceTime(long seconds) {
        currentClock = Clock.offset(currentClock, Duration.ofSeconds(seconds));
    }

    /**
     * Set the clock to a time that would cause OTP expiry
     * @param otpGenerationTime The time when OTP was originally generated
     * @param otpValiditySeconds The validity period of the OTP in seconds
     */
    public void setTimeAfterOtpExpiry(Instant otpGenerationTime, long otpValiditySeconds) {
        Instant expiryTime = otpGenerationTime.plus(Duration.ofSeconds(otpValiditySeconds + 1));
        currentClock = Clock.fixed(expiryTime, ZoneOffset.UTC);
    }

    /**
     * Reset to system clock
     */
    public void resetToSystemClock() {
        currentClock = Clock.systemUTC();
    }
}