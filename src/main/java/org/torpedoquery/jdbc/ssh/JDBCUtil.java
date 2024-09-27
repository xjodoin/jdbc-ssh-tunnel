/**
 * Copyright © 2024 Xavier Jodoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.
 */
package org.torpedoquery.jdbc.ssh;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.*;

/**
 * The {@code JDBCUtil} class provides utility methods for parsing JDBC URLs
 * and extracting SSH connection information for tunneling JDBC connections over SSH.
 */
public class JDBCUtil {

    /**
     * Parses the provided JDBC URL and properties to extract SSH connection details.
     *
     * @param jdbcUrl    the JDBC URL, potentially containing SSH tunneling information
     * @param properties additional connection properties
     * @return an {@link SSHInfo} object containing both SSH and JDBC connection details
     * @throws SQLException        if a database access error occurs
     * @throws URISyntaxException  if the JDBC URL has invalid syntax
     */
    public static SSHInfo getSSHinfo(String jdbcUrl, Properties properties) throws SQLException, URISyntaxException {

        // Remove ":ssh" to get the underlying JDBC URL
        String originalJdbcUrl = jdbcUrl.replace(":ssh", "");

        // Find the underlying JDBC driver
        Driver underlyingDriver = getUnderlyingDriver(originalJdbcUrl);
        if (underlyingDriver == null) {
            throw new SQLException("No suitable driver found for URL: " + originalJdbcUrl);
        }

        // Parse the original JDBC URL
        URI uri = new URI(originalJdbcUrl.replace("jdbc:", ""));

        // Get default host and port from driver property info
        DriverPropertyInfo[] propertyInfo = underlyingDriver.getPropertyInfo(originalJdbcUrl, properties);
        Optional<String> host = getDefaultHost(propertyInfo);
        Optional<Integer> port = getDefaultPort(propertyInfo);

        // Create SSHInfo object and set remote host and port
        SSHInfo sshInfo = new SSHInfo(underlyingDriver, uri);
        sshInfo.setRemoteHost(host.orElse(uri.getHost()));
        sshInfo.setRemotePort(port.orElse(getUriPort(uri)));

        // Parse SSH options from the query parameters
        Map<String, String> queryParams = parseQueryParams(uri.getQuery());

        sshInfo.setSshHost(queryParams.getOrDefault("sshHost", sshInfo.getRemoteHost()));
        sshInfo.setSshPort(Integer.parseInt(queryParams.getOrDefault("sshPort", "22")));
        sshInfo.setSshUser(queryParams.getOrDefault("sshUser", System.getProperty("user.name")));
        sshInfo.setPrivateKey(queryParams.getOrDefault("sshKey", System.getProperty("user.home") + "/.ssh/id_rsa"));
        sshInfo.setPassphrase(queryParams.get("sshPassphrase"));

        return sshInfo;
    }

    /**
     * Retrieves the default host from the driver's property info.
     *
     * @param propertyInfo array of {@link DriverPropertyInfo}
     * @return an {@link Optional} containing the host if available
     */
    private static Optional<String> getDefaultHost(DriverPropertyInfo[] propertyInfo) {
        for (DriverPropertyInfo dpi : propertyInfo) {
            if ("HOST".equalsIgnoreCase(dpi.name) && dpi.value != null && !dpi.value.isEmpty()) {
                return Optional.of(dpi.value);
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the default port from the driver's property info.
     *
     * @param propertyInfo array of {@link DriverPropertyInfo}
     * @return an {@link Optional} containing the port if available
     */
    private static Optional<Integer> getDefaultPort(DriverPropertyInfo[] propertyInfo) {
        for (DriverPropertyInfo dpi : propertyInfo) {
            if ("PORT".equalsIgnoreCase(dpi.name) && dpi.value != null && !dpi.value.isEmpty()) {
                try {
                    return Optional.of(Integer.parseInt(dpi.value));
                } catch (NumberFormatException e) {
                    // Ignore and continue
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the port from the URI, or default ports based on the scheme.
     *
     * @param uri the URI to extract the port from
     * @return the port number
     */
    private static int getUriPort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }
        switch (uri.getScheme().toLowerCase()) {
            case "mysql":
                return 3306;
            case "postgresql":
                return 5432;
            default:
                return -1; // Undefined port
        }
    }

    /**
     * Parses a query string into a map of key-value pairs.
     *
     * @param query the query string from a URI
     * @return a map containing query parameter names and values
     */
    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();

        if (query != null && !query.isEmpty()) {
            String[] params = query.split("&");

            for (String param : params) {
                String[] split = param.split("=", 2);
                if (split.length == 2) {
                    queryParams.put(split[0], split[1]);
                } else if (split.length == 1) {
                    queryParams.put(split[0], "");
                }
            }
        }

        return queryParams;
    }

    /**
     * Finds the underlying JDBC driver that accepts the given URL.
     *
     * @param url the JDBC URL
     * @return the underlying {@link Driver}, or {@code null} if none is found
     * @throws SQLException if a database access error occurs
     */
    private static Driver getUnderlyingDriver(String url) throws SQLException {
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                if (driver.acceptsURL(url)) {
                    return driver;
                }
            } catch (SQLException e) {
                // Ignore and continue searching
            }
        }

        return null;
    }
}
