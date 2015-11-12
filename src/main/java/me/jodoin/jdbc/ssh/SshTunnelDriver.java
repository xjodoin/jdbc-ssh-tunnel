package me.jodoin.jdbc.ssh;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshTunnelDriver implements Driver {

	/**
	 * Root for the JDBC URL that the Phoenix accepts accepts.
	 */
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

	private final static DriverPropertyInfo[] EMPTY_INFO = new DriverPropertyInfo[0];

	public Connection connect(String url, Properties info) throws SQLException {
		String strSshHost = "10.130.1.120"; // hostname or ip or SSH
		String strSshUser = "xjodoin";
		
		int nSshPort = 22; // remote SSH host port number
		String strRemoteHost = "10.130.3.221"; // hostname or ip
//		jdbc:mysql://10.130.3.221:3306/api_cake								// of your
																// database
																// server
		int nLocalPort = getRandomPort(); // local port number use to bind SSH tunnel
		int nRemotePort = 3306; // remote port number of your database

		
		try {
			SshTunnelDriver.doSshTunnel(strSshUser, strSshHost, nSshPort, strRemoteHost, nLocalPort,
					nRemotePort);
		} catch (JSchException e) {
			throw new RuntimeException(e);
		}

		try {
			Driver driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
			return driver.connect("jdbc:mysql://localhost:" + nLocalPort+"/api_cake", info);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
//		return DriverManager.getConnection("jdbc:mysql://localhost:" + nLocalPort+"/api_cake", strDbUser, strDbPassword);
	}

	private static void doSshTunnel(String strSshUser, String strSshHost, int nSshPort, String strRemoteHost,
			int nLocalPort, int nRemotePort) throws JSchException {
		final JSch jsch = new JSch();
		jsch.addIdentity("/home/xjodoin/.ssh/id_rsa");
		Session session = jsch.getSession(strSshUser, strSshHost, 22);
		
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		session.connect();
		session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
	}

	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(JDBC_PROTOCOL);
	}

	private static int getRandomPort() {
		try (ServerSocket server = new ServerSocket(0)) {
			return server.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
			return 6667;
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
