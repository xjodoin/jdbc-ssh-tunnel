/**
 * Copyright © 2018 Xavier Jodoin (xavier@jodoin.me)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.torpedoquery.jdbc.ssh;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshTunnelDriver implements Driver {

	/**
	 * Key used to retreive the hostname value from the properties instance passed
	 * to the driver.
	 */
	public static final String HOST_PROPERTY_KEY = "HOST";

	/**
	 * Key used to retreive the port number value from the properties instance
	 * passed to the driver.
	 */
	public static final String PORT_PROPERTY_KEY = "PORT";

	public final static String JDBC_PROTOCOL = "jdbc:ssh";

	public static final SshTunnelDriver INSTANCE;

	static {
		try {
			DriverManager.registerDriver(INSTANCE = new SshTunnelDriver());
		} catch (SQLException e) {
			throw new IllegalStateException(
					"Unable to register " + SshTunnelDriver.class.getName() + ": " + e.getMessage());
		}
	}

	private ConcurrentMap<SSHInfo, SSHSession> sessions = new ConcurrentHashMap<>();

	private final static DriverPropertyInfo[] EMPTY_INFO = new DriverPropertyInfo[0];

	public Connection connect(String url, Properties info) throws SQLException {

		try {
			SSHInfo ssHinfo = JDBCUtil.getSSHinfo(url, info);
			SSHSession sshSession = sessions.computeIfAbsent(ssHinfo, this::createSSHTunel);
			Driver underlyingDriver = ssHinfo.getUnderlyingDriver();
			URI originalUri = ssHinfo.getOriginalUri();
			URI sshTunnelUrl = new URI(originalUri.getScheme(), originalUri.getUserInfo(), sshSession.getLocalHost(),
					sshSession.getLocalPort(), originalUri.getPath(), originalUri.getQuery(),
					originalUri.getFragment());

			return underlyingDriver.connect("jdbc:" + sshTunnelUrl.toString(), info);

		} catch (URISyntaxException e) {
			throw new SQLException(e);

		}
	};

	private SSHSession createSSHTunel(SSHInfo key) {

		try {
			final JSch jsch = new JSch();

			SSHSession sshSession = new SSHSession(getRandomPort());

			if (key.getPrivateKey() != null) {
				jsch.addIdentity(key.getPrivateKey(), key.getPassphrase());
			}

			Session session = jsch.getSession(key.getSshUser(), key.getSshHost(), key.getSshPort());

			final Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
			session.setPortForwardingL(sshSession.getLocalPort(), key.getRemoteHost(), key.getRemotePort());

			sshSession.setSession(session);

			return sshSession;
		} catch (JSchException e) {
			throw new RuntimeException("Can't etasblish ssh connection ", e);
		}
	}

	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(JDBC_PROTOCOL);
	}

	private static int getRandomPort() {
		try (ServerSocket server = new ServerSocket(0)) {
			return server.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return EMPTY_INFO;
	}

	public int getMajorVersion() {
		return 4;
	}

	public int getMinorVersion() {
		return 4;
	}

	public boolean jdbcCompliant() {
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
