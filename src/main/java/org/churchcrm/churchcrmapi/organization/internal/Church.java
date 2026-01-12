package org.churchcrm.churchcrmapi.organization.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.churchcrm.churchcrmapi.crosscutting.auditing.Audit;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "churches", schema = "organization")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Church {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200, unique = true)
    private String hostName;

    @OneToMany(mappedBy = "church", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Congregation> congregations = new ArrayList<>();

    @Embedded
    private Audit audit = new Audit();

    static Church create(CreateChurchDto request, ChurchMapper mapper) {
        var church = mapper.toChurch(request);
        
        var mainCongregation = Congregation.createMain(church, mapper.toAddress(request.address()));
        church.congregations.add(mainCongregation);

        return church;
    }
}
