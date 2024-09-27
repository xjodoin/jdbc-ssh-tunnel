/**
 * Copyright © 2024 Xavier Jodoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.
 */
package org.torpedoquery.jdbc.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * The {@code SshTunnelDriver} class implements the JDBC {@link Driver} interface
 * to provide SSH tunneling capabilities for JDBC connections.
 */
public class SshTunnelDriver implements Driver {

    /**
     * JDBC protocol prefix for SSH tunneling.
     */
    public static final String JDBC_PROTOCOL = "jdbc:ssh";

    /**
     * Singleton instance of the driver.
     */
    public static final SshTunnelDriver INSTANCE;

    static {
        try {
            INSTANCE = new SshTunnelDriver();
            DriverManager.registerDriver(INSTANCE);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to register SshTunnelDriver: " + e.getMessage(), e);
        }
    }

    private final ConcurrentMap<SSHInfo, SSHSession> sessions = new ConcurrentHashMap<>();
    private static final DriverPropertyInfo[] EMPTY_INFO = new DriverPropertyInfo[0];

    /**
     * Establishes a connection to the database through an SSH tunnel.
     *
     * @param url  the database URL
     * @param info a list of arbitrary string tag/value pairs as connection arguments
     * @return a {@link Connection} object that represents a connection to the URL
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        try {
            SSHInfo sshInfo = JDBCUtil.getSSHinfo(url, info);
            SSHSession sshSession = sessions.computeIfAbsent(sshInfo, this::createSSHTunnel);
            Driver underlyingDriver = sshInfo.getUnderlyingDriver();
            URI originalUri = sshInfo.getOriginalUri();

            URI sshTunnelUri = new URI(
                    originalUri.getScheme(),
                    originalUri.getUserInfo(),
                    sshSession.getLocalHost(),
                    sshSession.getLocalPort(),
                    originalUri.getPath(),
                    originalUri.getQuery(),
                    originalUri.getFragment()
            );

            return underlyingDriver.connect("jdbc:" + sshTunnelUri.toString(), info);
        } catch (URISyntaxException e) {
            throw new SQLException("Invalid URI syntax: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the driver can handle the given URL.
     *
     * @param url the URL of the database
     * @return {@code true} if the driver can handle the URL; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(JDBC_PROTOCOL);
    }

    /**
     * Retrieves the driver's major version number.
     *
     * @return the driver's major version number
     */
    @Override
    public int getMajorVersion() {
        return 1;
    }

    /**
     * Retrieves the driver's minor version number.
     *
     * @return the driver's minor version number
     */
    @Override
    public int getMinorVersion() {
        return 0;
    }

    /**
     * Reports whether this driver is a genuine JDBC Compliant driver.
     *
     * @return {@code false}, as this driver is not fully JDBC compliant
     */
    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    /**
     * Gets the parent logger for this driver.
     *
     * @return the parent logger
     * @throws SQLFeatureNotSupportedException if the driver does not use {@code java.util.logging}
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Logging is not supported.");
    }

    /**
     * Returns an array of {@link DriverPropertyInfo} objects describing possible properties.
     *
     * @param url  the URL of the database to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on connect open
     * @return an array of {@link DriverPropertyInfo} objects
     * @throws SQLException if a database access error occurs
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return EMPTY_INFO;
    }

    /**
     * Creates an SSH tunnel based on the provided {@link SSHInfo}.
     *
     * @param sshInfo the SSH information
     * @return an {@link SSHSession} representing the SSH tunnel
     */
    private SSHSession createSSHTunnel(SSHInfo sshInfo) {
        try {
            JSch jsch = new JSch();
            SSHSession sshSession = new SSHSession(getRandomPort());

            if (sshInfo.getPrivateKey() != null) {
                jsch.addIdentity(sshInfo.getPrivateKey(), sshInfo.getPassphrase());
            }

            Session session = jsch.getSession(sshInfo.getSshUser(), sshInfo.getSshHost(), sshInfo.getSshPort());
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();
            session.setPortForwardingL(sshSession.getLocalPort(), sshInfo.getRemoteHost(), sshInfo.getRemotePort());
            sshSession.setSession(session);

            return sshSession;
        } catch (JSchException e) {
            throw new RuntimeException("Cannot establish SSH connection: " + e.getMessage(), e);
        }
    }

    /**
     * Obtains a random available port on the local machine.
     *
     * @return a random available port number
     */
    private static int getRandomPort() {
        try (ServerSocket server = new ServerSocket(0)) {
            return server.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Unable to find an available port: " + e.getMessage(), e);
        }
    }
}
