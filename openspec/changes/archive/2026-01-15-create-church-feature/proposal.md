# Create Church Feature

## Why
The church creation feature exists in the codebase but does not implement the required business validation rules and address optionality. This change ensures the feature meets the specified requirements for input validation, congregation setup, and error handling.

## What Changes
- Add comprehensive validation to church creation inputs
- Make address optional in church creation
- Ensure main congregation is automatically created
- Improve error handling with appropriate exception types

## Scope
- Update CreateChurchDto with proper validation annotations
- Modify Church.create method to include validation logic
- Update ChurchService to handle validation and error responses
- Ensure main congregation is created with correct inheritance
- Add comprehensive tests for validation scenarios

## Impact
- Existing API contract changes (address becomes optional)
- New validation rules enforced
- Improved error handling for invalid inputs