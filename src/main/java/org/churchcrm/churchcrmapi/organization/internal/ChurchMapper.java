package org.churchcrm.churchcrmapi.organization.internal;

import org.churchcrm.churchcrmapi.organization.AddressDto;
import org.churchcrm.churchcrmapi.organization.ChurchCreated;
import org.churchcrm.churchcrmapi.organization.ChurchDto;
import org.churchcrm.churchcrmapi.organization.CreateChurchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
interface ChurchMapper {

    Address toAddress(AddressDto dto);

    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "congregations", ignore = true)
    @Mapping(target = "id", ignore = true)
    Church toChurch(CreateChurchDto dto);

    @Mapping(target = "mainAddress", source = "church", qualifiedByName = "getMainAddress")
    @Mapping(target = "createdBy", source = "audit.createdBy")
    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "updatedBy", source = "audit.updatedBy")
    @Mapping(target = "updatedAt", source = "audit.updatedAt")
    ChurchDto toDto(Church church);

    @Mapping(target = "churchId", source = "church.id")
    @Mapping(target = "name", source = "church.name")
    @Mapping(target = "userName", source = "dto.userName")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "fullName", source = "dto.fullName")
    ChurchCreated toChurchCreated(Church church, CreateChurchDto dto);

    AddressDto toAddressDto(Address address);

    @Named("getMainAddress")
    default AddressDto getMainAddress(Church church) {
        return church.getCongregations().stream()
                .filter(Congregation::getIsMain)
                .findFirst()
                .map(Congregation::getAddress)
                .map(this::toAddressDto)
                .orElse(null);
    }
}
