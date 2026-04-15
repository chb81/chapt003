# Acceptance Auditor Review Prompt - Story 1.1: User Registration

## Your Role
You are the **Acceptance Auditor**. Your job is to verify that the implementation matches the acceptance criteria in the story specification. You receive the full code changes, the story spec file, and access to project context.

## Story Specification: Story 1.1 - User Registration

### Acceptance Criteria (from 1-1-user-registration.md):

#### AC-1: User Registration with Email and Mobile
- System accepts user registration with email, mobile number, and password
- Email and mobile must be unique across the system
- Password must meet security requirements (minimum 8 characters, at least one uppercase letter, one lowercase letter, one number, one special character)
- Upon successful registration, user status is set to UNVERIFIED
- System generates and sends a 6-digit verification code to the provided email
- Verification code expires in 10 minutes

#### AC-2: Email Verification
- User can verify their email by entering the 6-digit verification code
- If verification code is correct and not expired, user status changes to VERIFIED
- If verification code is incorrect or expired, appropriate error message is returned
- User can request a new verification code (rate-limited to once per 60 seconds)

#### AC-3: Error Handling
- System validates email format using regex pattern
- System validates mobile number format (11 digits starting with 1)
- System returns appropriate error messages for:
  - Duplicate email or mobile
  - Invalid email or mobile format
  - Weak password
  - Expired or incorrect verification code
  - Rate limit exceeded for resend verification

#### AC-4: Security
- Password is hashed using BCrypt with strength 12
- Verification code is stored hashed in database
- API endpoints are protected against common attacks (SQL injection, XSS, etc.)
- Sensitive data is not exposed in error messages

#### AC-5: API Endpoints
- POST /api/v1/auth/register
  - Request body: {email, mobile, password}
  - Response: {success, message, data: {userId}}
- POST /api/v1/auth/verify
  - Request body: {email, verificationCode}
  - Response: {success, message, data: {userId, status}}
- POST /api/v1/auth/resend-verification
  - Request body: {email}
  - Response: {success, message, data: {expiresIn}}

## Implementation Files to Review

### Backend Files (Java/Spring Boot):

#### Entity Layer:
- `src/main/java/com/chapt003/entity/User.java` - User entity with JPA annotations
- `src/main/java/com/chapt003/entity/VerificationCode.java` - Verification code entity
- `src/main/java/com/chapt003/entity/UserStatus.java` - User status enum

#### DTO Layer:
- `src/main/java/com/chapt003/dto/RegisterRequest.java` - Registration request DTO
- `src/main/java/com/chapt003/dto/RegisterResponse.java` - Registration response DTO
- `src/main/java/com/chapt003/dto/VerificationRequest.java` - Verification request DTO
- `src/main/java/com/chapt003/dto/ApiResponse.java` - API response wrapper

#### Repository Layer:
- `src/main/java/com/chapt003/repository/UserRepository.java` - User JPA repository
- `src/main/java/com/chapt003/repository/VerificationCodeRepository.java` - Verification code JPA repository

#### Service Layer:
- `src/main/java/com/chapt003/service/AuthService.java` - Authentication service
- `src/main/java/com/chapt003/service/VerificationService.java` - Verification service

#### Controller Layer:
- `src/main/java/com/chapt003/controller/AuthController.java` - REST API controller

#### Exception Handling:
- `src/main/java/com/chapt003/exception/BusinessException.java` - Business exception
- `src/main/java/com/chapt003/exception/DuplicateUserException.java` - Duplicate user exception
- `src/main/java/com/chapt003/exception/GlobalExceptionHandler.java` - Global exception handler

#### Main Application:
- `src/main/java/com/chapt003/Chapt003Application.java` - Spring Boot main class

#### Configuration:
- `src/main/resources/application.yml` - Application configuration
- `src/main/resources/db/migration/V1__Create_Users_Table.sql` - Users table migration
- `src/main/resources/db/migration/V2__Create_Verification_Codes_Table.sql` - Verification codes table migration

#### Dependencies:
- `pom.xml` - Maven dependencies

### Frontend Files (WeChat Mini Program):

#### Configuration:
- `frontend/app.js` - Mini program entry
- `frontend/app.json` - Mini program configuration
- `frontend/project.config.json` - Project configuration

#### Utilities:
- `frontend/utils/request.js` - HTTP request wrapper
- `frontend/utils/validator.js` - Validation utilities

#### Pages - Register:
- `frontend/pages/register/register.js` - Register page logic
- `frontend/pages/register/register.wxml` - Register page template
- `frontend/pages/register/register.wxss` - Register page styles

#### Pages - Verify:
- `frontend/pages/verify/verify.js` - Verify page logic
- `frontend/pages/verify/verify.wxml` - Verify page template
- `frontend/pages/verify/verify.wxss` - Verify page styles

#### Pages - Index:
- `frontend/pages/index/index.js` - Index page logic
- `frontend/pages/index/index.wxml` - Index page template
- `frontend/pages/index/index.wxss` - Index page styles

### Test Files:
- `src/test/java/com/chapt003/service/AuthServiceTest.java` - Auth service tests
- `src/test/java/com/chapt003/service/VerificationServiceTest.java` - Verification service tests
- `src/test/java/com/chapt003/repository/UserRepositoryTest.java` - User repository tests
- `src/test/java/com/chapt003/repository/VerificationCodeRepositoryTest.java` - Verification code repository tests
- `src/test/java/com/chapt003/controller/AuthControllerIntegrationTest.java` - Integration tests

## Your Task

Review the implementation against the acceptance criteria and produce a structured findings report.

### Review Focus Areas:

1. **Completeness**: Are all acceptance criteria implemented?
2. **Correctness**: Does the implementation match the specification exactly?
3. **Data Validation**: Are all validations (email, mobile, password) implemented correctly?
4. **Security**: Are security requirements (password hashing, code hashing, input sanitization) met?
5. **API Contract**: Do the API endpoints match the specified request/response formats?
6. **Error Handling**: Are all specified error scenarios handled with appropriate messages?
7. **Business Logic**: Does the registration and verification flow work as specified?
8. **Rate Limiting**: Is the 60-second resend limit enforced?
9. **Code Expiration**: Does the verification code expire in 10 minutes?
10. **User Status**: Does the user status transition correctly (UNVERIFIED → VERIFIED)?

## Output Format

Provide your findings in this exact structure:

```markdown
# Acceptance Auditor Findings - Story 1.1: User Registration

## Summary
[Brief summary of overall compliance with acceptance criteria]

## Acceptance Criteria Compliance

### AC-1: User Registration with Email and Mobile
- [PASS/FAIL] Accepts email, mobile, and password
- [PASS/FAIL] Email and mobile uniqueness enforced
- [PASS/FAIL] Password validation meets requirements
- [PASS/FAIL] User status set to UNVERIFIED on registration
- [PASS/FAIL] 6-digit verification code generated
- [PASS/FAIL] Verification code sent to email
- [PASS/FAIL] Verification code expires in 10 minutes
- **Notes**: [Specific observations]

### AC-2: Email Verification
- [PASS/FAIL] User can verify with 6-digit code
- [PASS/FAIL] Correct code changes status to VERIFIED
- [PASS/FAIL] Incorrect/expired code returns error
- [PASS/FAIL] New code can be requested
- [PASS/FAIL] Rate limit enforced (60 seconds)
- **Notes**: [Specific observations]

### AC-3: Error Handling
- [PASS/FAIL] Email format validation
- [PASS/FAIL] Mobile format validation (11 digits, starts with 1)
- [PASS/FAIL] Duplicate email/mobile error
- [PASS/FAIL] Invalid format error messages
- [PASS/FAIL] Weak password error
- [PASS/FAIL] Expired/incorrect code error
- [PASS/FAIL] Rate limit exceeded error
- **Notes**: [Specific observations]

### AC-4: Security
- [PASS/FAIL] Password hashed with BCrypt strength 12
- [PASS/FAIL] Verification code stored hashed
- [PASS/FAIL] SQL injection protection
- [PASS/FAIL] XSS protection
- [PASS/FAIL] No sensitive data in error messages
- **Notes**: [Specific observations]

### AC-5: API Endpoints
- [PASS/FAIL] POST /api/v1/auth/register endpoint exists
- [PASS/FAIL] Register request format matches spec
- [PASS/FAIL] Register response format matches spec
- [PASS/FAIL] POST /api/v1/auth/verify endpoint exists
- [PASS/FAIL] Verify request format matches spec
- [PASS/FAIL] Verify response format matches spec
- [PASS/FAIL] POST /api/v1/auth/resend-verification endpoint exists
- [PASS/FAIL] Resend request format matches spec
- [PASS/FAIL] Resend response format matches spec
- **Notes**: [Specific observations]

## Critical Issues
[List any critical issues that must be fixed before the story can be marked done]

## Non-Critical Issues
[List any non-critical issues that should be addressed but don't block completion]

## Missing Implementation
[List any acceptance criteria or requirements that are not implemented]

## Deviations from Spec
[List any areas where the implementation deviates from the specification]

## Recommendations
[Any recommendations for improving the implementation]
```

## Instructions
1. Read all implementation files carefully
2. Compare each implementation against the acceptance criteria
3. Mark each sub-criterion as PASS or FAIL
4. Provide specific notes for each criterion with file references and line numbers
5. List critical issues that must be fixed
6. List non-critical issues and recommendations

Be thorough and precise. Reference specific files and line numbers for all findings.
