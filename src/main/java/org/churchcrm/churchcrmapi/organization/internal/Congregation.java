package org.churchcrm.churchcrmapi.organization.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "congregations", schema = "organization")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Congregation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Boolean isMain = false;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Embedded
    private Audit audit = new Audit();

    public static Congregation createMain(Church church, Address address) {
        var congregation = new Congregation();
        congregation.setName("Main Congregation");
        congregation.setIsMain(true);
        congregation.setAddress(address);
        congregation.setChurch(church);
        return congregation;
    }
}
