# Church Creation Implementation Tasks

1. **Update CreateChurchDto** - Make address field optional and add validation annotations for all required fields (name, hostName, userName, email lengths and formats)
2. **Add AddressDto validation** - Add validation annotations for address fields when provided
3. **Implement domain validation** - Add validation logic in Church.create factory method for business rules
4. **Update ChurchService** - Add hostname uniqueness check and proper error handling
5. **Update ChurchMapper** - Handle optional address mapping correctly
6. **Add unit tests** - Test validation scenarios for Church.create method
7. **Add integration tests** - Test full church creation flow with validation errors
8. **Update OpenAPI documentation** - Reflect address optionality in API docs
9. **Run ArchUnit tests** - Ensure all architectural rules still pass
10. **Test Keycloak integration** - Verify user creation still works with updated flow