FROM maven:3.8.1-openjdk-17

# Set the working directory in the container
WORKDIR /app

# Copy the source code into the container
COPY . /app


# Build the application
RUN mvn package

# Expose the port that the Spring Boot application listens on
EXPOSE 8080

# Set the command to run the Spring Boot application when the container starts
CMD ["sh", "-c", "java -jar $(find target -name '*.jar' | grep -v original)"]

