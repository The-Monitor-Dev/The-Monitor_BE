package the_monitor.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@Table(name = "accounts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "account_email", nullable = false)
    private String email;

    @Column(name = "account_password", nullable = false)
    private String password;

    @Column(name = "account_company_name", nullable = false)
    private String companyName;

    @Column(name = "account_manager_name", nullable = false)
    private String managerName;

    @Column(name = "account_manager_phone")
    private String managerPhone;

    @Column(name = "account_agreement", nullable = false)
    private boolean agreement;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Client> clients;

    @Builder
    public Account(String email,
                   String password,
                   String companyName,
                   String managerName,
                   String managerPhone,
                   boolean agreement ) {

        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.agreement = agreement;

    }

    public void resetPassword(String password) {
        this.password = password;
    }

}
