package org.churchcrm.churchcrmapi.organization.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(length = 200)
    private String line1;

    @Column(length = 200)
    private String line2;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String postalCode;
}
