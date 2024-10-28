package the_monitor.domain.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Entity
@Getter
@Table(name = "client_mail_cc")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientMailCC extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_mail_cc_id")
    private Long id;

    @Column(name = "client_mail_cc_address", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Builder
    public ClientMailCC(String address,
                        Client client) {

        this.address = address;
        this.client = client;

    }

}