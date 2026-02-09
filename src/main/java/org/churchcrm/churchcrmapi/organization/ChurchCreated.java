package org.churchcrm.churchcrmapi.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class ChurchCreated {
    UUID churchId;
    String name;
    String userName;
    String email;
    String fullName;
}
