package the_monitor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.domain.model.Account;

@Getter
@NoArgsConstructor
public class AccountSignUpRequest {

    @NotBlank(message = "email은 필수입니다.")
    private String email;

    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @NotBlank(message = "기업명은 필수입니다.")
    private String companyName;

    @NotBlank(message = "담당자명은 필수입니다.")
    private String managerName;

    private String managerPhone;

    @NotNull(message = "이용 약관 및 개인정보 수집 동의는 필수입니다.")
    private boolean agreement;

    @Builder
    public AccountSignUpRequest(
            String email,
            String password,
            String companyName,
            String managerName,
            String managerPhone,
            boolean agreement) {

        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.agreement = agreement;

    }

    public Account toEntity() {

        return Account.builder()
                .email(email)
                .password(password)
                .companyName(companyName)
                .managerName(managerName)
                .managerPhone(managerPhone)
                .agreement(agreement)
                .build();

    }

}
