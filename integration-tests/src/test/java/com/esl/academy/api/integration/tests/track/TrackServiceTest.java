//package com.esl.academy.api.integration.tests.track;
//
//import com.esl.academy.api.orchestrator.TrackLifeCycleService;
//import com.esl.academy.api.track.TrackDto;
//import com.esl.academy.api.track.TrackRepository;
//import com.esl.academy.api.track.TrackService;
//import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static com.esl.academy.api.track.TrackDto.AddTrackDto;
//
//public class TrackServiceTest extends BaseIntegrationTest {
//    @Autowired
//    private TrackService trackService;
//
//    @Autowired
//    private TrackRepository trackRepository;
//
//    @Autowired
//    private TrackLifeCycleService trackLifeCycleService;
//
//    @BeforeEach
//    void setup() {
//        trackRepository.deleteAll();
//    }
//
//    @Test
//    void addTrack_shouldAddTrackAndLearningResourcePlaceholders() {
//        AddTrackDto newTrackDto = new AddTrackDto(
//            "Backend",
//            "Powering scalable and high-performant apps",
//            5,
//            "restful apis, and database"
//        );
//
//        TrackDto resultDto = trackLifeCycleService.createTrackWithResources(newTrackDto);
//
//        Assertions.assertThat(resultDto).isNotNull();
//        Assertions.assertThat(resultDto.trackId()).isNotNull();
//        Assertions.assertThat(resultDto.name()).isEqualTo("Backend");
//        Assertions.assertThat(resultDto.duration()).isEqualTo(5);
//    }
//}
