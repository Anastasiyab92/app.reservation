# Environment Setup

## Required Environment Variables

### Database Configuration
Set the following environment variable for your database password:

```bash
export DB_PASSWORD=your_actual_password
```

### For Development
You can also create a local configuration file `application-local.properties` (not committed to Git) with your local database settings.

## Running the Application

### With Environment Variable
```bash
export DB_PASSWORD=your_password
./mvnw spring-boot:run
```

### With Local Profile
```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

### For Production
```bash
export DB_PASSWORD=production_password
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## Security Notes
- Never commit passwords to Git
- Use environment variables for sensitive data
- The `application-local.properties` file is ignored by Git
- Production passwords should be managed securely (e.g., through CI/CD secrets)
