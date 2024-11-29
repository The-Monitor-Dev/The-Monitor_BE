package the_monitor.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

@Entity
@Getter
@Table(name = "client_mail_recipients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientMailRecipient extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_mail_recipient_id")
    private Long id;

    @Column(name = "client_mail_recipient_address", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;

    @Builder
    public ClientMailRecipient(String address,
                               Client client) {

        this.address = address;
        this.client = client;

    }

}
