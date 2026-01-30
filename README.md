# Minded - Community Q&A Platform

A community-driven question-and-answer platform built with Java EE, JSP, and MySQL for knowledge sharing on programming topics.

## Overview

Minded is a web-based Q&A platform that enables users to ask questions, share knowledge, and engage in discussions around various programming topics. Built using traditional Java EE technologies, it provides a robust foundation for community-driven learning.

## Features

- User registration and authentication
- Question posting with topic categorization
- Answer and comment system
- Upvote/downvote voting mechanism
- Topic-based content discovery
- User profiles and activity tracking
- Admin panel for content management

## Tech Stack

- **Backend:** Java 8, Servlets, JSP
- **Database:** MySQL 5.7+
- **Server:** Apache Tomcat 8.5+
- **Build Tools:** Apache Ant, Maven
- **Testing:** JUnit, Mockito
- **CI/CD:** GitHub Actions

## Setup

### Prerequisites

- JDK 8 or higher
- Apache Tomcat 8.5+
- MySQL 5.7+
- Maven 3.6+

### Database Setup

```sql
-- Create database
CREATE DATABASE minded CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Build and Run

```bash
# Build with Maven
mvn clean package

# Deploy WAR to Tomcat
cp target/minded.war $TOMCAT_HOME/webapps/
```

## Testing

```bash
# Run tests
mvn test

# Generate coverage report
mvn jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`

## Project Structure

```
Minded/
├── src/java/          # Java source code
│   ├── connectdb/     # Database connection layer
│   ├── servlet/       # Servlet controllers
│   └── util/          # Utility classes
├── web/               # JSP views and static assets
├── test/              # Test files
└── db/                # Database schema
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Submit a pull request

## License

MIT License - see LICENSE file for details

## Contact

For questions or support, please open an issue on GitHub.
