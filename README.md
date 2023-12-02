# DeMoney

This Digital Wallet called DeMoney is a backend application built using a microservices architecture with various technologies including Java, Spring Framework, Spring Boot, and more.

## Table of Contents
- [Technologies](#technologies)
- [Architecture Diagram](#architecture-diagram)
- [Running Locally](#running-locally)
- [Testing](#testing)
- [Deployment](#deployment)

## <a name="technologies"></a>Technologies
The application is built utilizing the following technologies:

* Back-end:
* Java
* Spring Framework
* Spring Boot
* Spring Boot Security
* Spring Boot Mail
* Spring Boot JPA
* Spring Doc
* Hibernate ORM
* JWT
* Log4j
* Maven
* KeyCloak

* Database:
* MySQL

* Infrastructure:
* Terraform
* Kubernetes
* AWS (EC2, RDS MySQL, S3, Route 53)

## <a name="architecture-diagram"></a>Architecture Diagram
[Architecture Diagram](diagrama-infra-sprint1.jpg)

## <a name="running-locally"></a>Running Locally
To run the application locally, execute the following steps:

1. Clone the repository and navigate to the project directory.
2. Build the project using Maven with `mvn clean install`.
3. Run each microservice individually using `java -jar target/{microservice}.jar`.
4. Ensure your MySQL service is running.
5. Make sure you have the Keycloak server up and running.

Please make sure to update the application.properties file with your own configurations.

## <a name="testing"></a>Testing
The application was thoroughly tested using JUnit for unit testing and Postman for API testing. Manual testing was used for validating UI and transaction flows.

## <a name="deployment"></a>Deployment
The application uses GitLab CI/CD pipelines for deployments. Please refer to the .gitlab-ci.yml file in the repository for more information on the pipeline stages and jobs.