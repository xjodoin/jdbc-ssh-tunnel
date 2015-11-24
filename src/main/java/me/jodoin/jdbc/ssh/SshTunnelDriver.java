package me.jodoin.jdbc.ssh;

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
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshTunnelDriver implements Driver {

	/**
	 * Key used to retreive the hostname value from the properties instance
	 * passed to the driver.
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

	private LoadingCache<SSHInfo, SSHSession> sessions = CacheBuilder.newBuilder()
			.build(new CacheLoader<SSHInfo, SSHSession>() {
				public SSHSession load(SSHInfo key) throws JSchException {
					return createSSHTunel(key);
				}

			});

	private final static DriverPropertyInfo[] EMPTY_INFO = new DriverPropertyInfo[0];

	public Connection connect(String url, Properties info) throws SQLException {

		try {
			SSHInfo ssHinfo = JDBCUtil.getSSHinfo(url, info);
			SSHSession sshSession = sessions.get(ssHinfo);
			Driver underlyingDriver = ssHinfo.getUnderlyingDriver();
			URI originalUri = ssHinfo.getOriginalUri();
			URI sshTunnelUrl = new URI(originalUri.getScheme(), originalUri.getUserInfo(), sshSession.getLocalHost(),
					sshSession.getLocalPort(), originalUri.getPath(), originalUri.getQuery(),
					originalUri.getFragment());

			return underlyingDriver.connect("jdbc:" + sshTunnelUrl.toString(), info);

		} catch (URISyntaxException | ExecutionException e) {
			throw new SQLException(e);

		}
	};

	private SSHSession createSSHTunel(SSHInfo key) throws JSchException {

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
	}

	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(JDBC_PROTOCOL);
	}

	private static int getRandomPort() {
		try (ServerSocket server = new ServerSocket(0)) {
			return server.getLocalPort();
		} catch (IOException e) {
			throw Throwables.propagate(e);
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
