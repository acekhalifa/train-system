package com.esl.academy.api.integration.tests.user;

import com.esl.academy.api.appconfig.AppConfigRepository;
import com.esl.academy.api.integration.tests.base.BaseIntegrationTest;
import com.esl.academy.api.intern.Intern;
import com.esl.academy.api.intern.InternRepository;
import com.esl.academy.api.intern.InternStatus;
import com.esl.academy.api.invitation.InvitationService;
import com.esl.academy.api.user.User;
import com.esl.academy.api.user.UserDto;
import com.esl.academy.api.user.UserRepository;
import com.esl.academy.api.user.UserService;
import com.esl.academy.api.user.UserStatus;
import com.esl.academy.api.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.esl.academy.api.core.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class UserServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InternRepository internRepository;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private AppConfigRepository appConfigRepository;

    private User adminUser;
    private User supervisorUser;
    private User internUser;
    private UUID internUserId;

    @BeforeEach
    void setup() {
        adminUser = userRepository.findByEmail("john@example.com")
            .orElseThrow(() -> new NotFoundException("Test data failed to load John Doe user."));

        internUser = userRepository.findByEmail("mikky@example.com")
            .orElseThrow(() -> new NotFoundException("Test data failed to load John Doe user."));

        internUserId = internUser.getUserId();
    }

    @Test
    void updateUser_shouldUpdateFields() {
        UUID userId = adminUser.getUserId();
        UserDto.UpdateUserDto dto = new UserDto.UpdateUserDto(
            "Jane",
            "Smith",
            "jane@example.com",
            "pic.png"
        );

        UserDto updated = userService.updateUser(userId, dto);

        assertThat(updated.firstName()).isEqualTo("Jane");
        assertThat(updated.lastName()).isEqualTo("Smith");
        assertThat(updated.email()).isEqualTo("jane@example.com");
        assertThat(updated.profilePictureLink()).isEqualTo("pic.png");
    }

    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        UUID invalidId = UUID.randomUUID();

        assertThatThrownBy(() ->
            userService.updateUser(invalidId, new UserDto.UpdateUserDto(null, null, null, null))
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void changeUserRole_shouldUpdateRole() {
        UserDto.UpdateUserTypeDto dto = new UserDto.UpdateUserTypeDto(UserType.SUPER_ADMIN);

        UserDto result = userService.changeUserRole(adminUser.getUserId(), dto);

        assertThat(result.userType()).isEqualTo(UserType.SUPER_ADMIN);
    }

    @Test
    void changeUserStatus_shouldUpdateStatus() {
        UserDto.UpdateUserStatusDto dto = new UserDto.UpdateUserStatusDto(UserStatus.INACTIVE);

        UserDto result = userService.changeUserStatus(adminUser.getUserId(), dto);

        assertThat(result.status()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto dto = userService.getUserById(adminUser.getUserId());

        assertThat(dto).isNotNull();
        assertThat(dto.userId()).isEqualTo(adminUser.getUserId());
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() ->
            userService.getUserById(id)
        ).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUser_shouldDeactivateUser_andUpdateInternStatus() {
        UUID userId = internUser.getUserId();
        userService.deleteUser(userId);

        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.INACTIVE);

        Intern updatedIntern = internRepository.findById(userId).orElseThrow();
        assertThat(updatedIntern.getInternStatus()).isEqualTo(InternStatus.DISCONTINUED);
    }

    @Test
    void getAllSupervisors_shouldReturnPage() {
        Page<UserDto> page = userService.getAllSupervisors(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void searchSupervisors_shouldFilterByName() {
        Page<UserDto> page = userService.searchSupervisors(
            null,
            null,
            null,
            "john",
            PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchSupervisors_shouldReturnEmpty_whenNoMatch() {
        Page<UserDto> page = userService.searchSupervisors(
            null,
            null,
            null,
            "no-match",
            PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(0);
    }

    @Test
    void searchSuperAdmins_shouldWorkWithEmailFilter() {
        Page<UserDto> page = userService.searchSuperAdmins(
            null,
            null,
            null,
            "john@example.com",
            PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(1);
    }
}

