package the_monitor.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import the_monitor.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "clients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String name;

    @Column(name = "client_manager_name", nullable = false)
    private String managerName;

    @Column(name = "client_logo", nullable = true)
    private String logo;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ClientMailRecipient> clientMailRecipients = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ClientMailCC> clientMailCCs = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();

    @Builder
    public Client(String name,
                  String managerName,
                  String logo,
                  Account account) {


        this.name = name;
        this.managerName = managerName;
        this.logo = logo;
        this.account = account;
        this.categories = new ArrayList<>();

    }

    public void setCategory(Category category) {
        this.categories.add(category); // 카테고리 추가
    }

    // clientMailRecipients 설정 메서드
    public void setClientMailRecipients(List<ClientMailRecipient> clientMailRecipients) {
        this.clientMailRecipients = clientMailRecipients;
    }

    // clientMailCCs 설정 메서드
    public void setClientMailCCs(List<ClientMailCC> clientMailCCs) {
        this.clientMailCCs = clientMailCCs;
    }

}
