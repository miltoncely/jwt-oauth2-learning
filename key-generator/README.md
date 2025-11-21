# Key Generator Module

This module is responsible for generating RSA 2048-bit key pairs (Private and Public) and distributing them to the `auth-service` and `resource-service` modules.

## Purpose
- **Private Key**: Used by `auth-service` to sign JWTs.
- **Public Key**: Used by `resource-service` (and optionally `auth-service`) to verify JWT signatures.

## How to Run

You can run this module as a standard Java application. It does not require Spring.

### Using Gradle
```bash
./gradlew :key-generator:run
```
*(Note: You might need to configure a `run` task in build.gradle if not using the application plugin, or just run the Main class from your IDE)*

### From IDE
Run `com.learning.keygenerator.Main.main()`

## Output
The keys are generated and saved to:
- `../auth-service/src/main/resources/keys/private.pem`
- `../auth-service/src/main/resources/keys/public.pem`
- `../resource-service/src/main/resources/keys/public.pem`

## Verification
Check the files in the directories above. They should start with `-----BEGIN PRIVATE KEY-----` or `-----BEGIN PUBLIC KEY-----`.
