# Microservice Accounts

This microservice handles account-related operations.

## Database Setup

The microservice requires a MySQL database to store account information. Follow the steps below to set up the initial database configuration:

1. Create the `accounts` schema:
    ```sql
    create schema accounts;
    ```

2. Create the `ms-accounts` user:
    ```sql
    create user 'ms-accounts';
    ```

3. Create the `accounts` table:
    ```sql
    create table accounts.accounts (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL UNIQUE,
        cvu CHAR(22) NOT NULL UNIQUE,
        alias VARCHAR(255) NOT NULL UNIQUE
    );
    ```

4. Grant privileges to the `ms-accounts` user:
    ```sql
    GRANT ALL PRIVILEGES ON accounts.* TO 'ms-accounts';
    ```

5. Update the `ms-accounts` user's password:
    ```sql
    ALTER USER 'ms-accounts' IDENTIFIED BY 'service';
    ```

Make sure you have a MySQL server installed and running before executing these SQL statements.

## Usage

blablalba

## Configuration

blablabla