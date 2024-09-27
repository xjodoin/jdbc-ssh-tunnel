# JDBC SSH Tunnel

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.torpedoquery/jdbc-ssh-tunnel/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.torpedoquery/jdbc-ssh-tunnel)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)

## Overview

**JDBC SSH Tunnel** is a Java library that allows you to tunnel JDBC connections over SSH seamlessly. It enables secure database connectivity through SSH without requiring changes to your application code.

## Features

- **Seamless Integration**: Works with any JDBC-compliant database driver.
- **Secure Connection**: Leverages SSH tunneling for secure database connections.
- **Easy Configuration**: Minimal setup by just modifying the JDBC URL.

## Table of Contents

- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [Prerequisites](#prerequisites)
- [Usage](#usage)
  - [Basic Usage](#basic-usage)
  - [Advanced Configuration](#advanced-configuration)
  - [SSH Authentication with Private Key](#ssh-authentication-with-private-key)
- [Configuration Options](#configuration-options)
- [Contributing](#contributing)
- [License](#license)

## Getting Started

### Installation

Include the library in your project by adding the following dependency:

**Maven**

```xml
<dependency>
    <groupId>org.torpedoquery</groupId>
    <artifactId>jdbc-ssh-tunnel</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle**

```groovy
implementation 'org.torpedoquery:jdbc-ssh-tunnel:1.0.0'
```

### Prerequisites

- Java 8 or higher
- SSH access to the database server
- The underlying JDBC driver for your database

## Usage

### Basic Usage

Simply modify your JDBC URL to include the `jdbc:ssh` protocol:

```java
String url = "jdbc:ssh:mysql://remote.com/database";
Connection connection = DriverManager.getConnection(url, username, password);
```

### Advanced Configuration

Specify additional SSH parameters directly in the JDBC URL:

```java
String url = "jdbc:ssh:mysql://remote.com:3306/database?sshUser=ssh_username&sshHost=ssh_host&sshPort=2222";
Connection connection = DriverManager.getConnection(url, username, password);
```

### SSH Authentication with Private Key

Use a private key and passphrase for SSH authentication:

```java
String url = "jdbc:ssh:mysql://remote.com/database"
           + "?sshUser=ssh_username"
           + "&sshHost=ssh_host"
           + "&privateKey=/path/to/private_key"
           + "&passphrase=your_passphrase";
Connection connection = DriverManager.getConnection(url, username, password);
```

## Configuration Options

- `sshUser` (String): SSH username. Default is the current system user.
- `sshHost` (String): SSH server hostname or IP address. Default is the database host.
- `sshPort` (int): SSH server port. Default is `22`.
- `privateKey` (String): Path to the SSH private key file.
- `passphrase` (String): Passphrase for the private key, if applicable.
- `remoteHost` (String): Remote database host. Default is the host specified in the JDBC URL.
- `remotePort` (int): Remote database port. Default is the port specified in the JDBC URL.

## Contributing

We welcome contributions! Please see our [CONTRIBUTING](CONTRIBUTING.md) guidelines for details.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.txt](LICENSE.txt) file for details.
