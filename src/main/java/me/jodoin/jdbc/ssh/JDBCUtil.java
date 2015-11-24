package me.jodoin.jdbc.ssh;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.MoreObjects;

public class JDBCUtil {

	public static SSHInfo getSSHinfo(String jdbcUrl, Properties properties) throws SQLException, URISyntaxException {

		String originalJdbc = jdbcUrl.replace(":ssh", "");

		Driver underlyingDriver = getUnderlyingDriver(originalJdbc);

		URI uri = new URI(originalJdbc.replace("jdbc:", ""));

		DriverPropertyInfo[] propertyInfo = underlyingDriver.getPropertyInfo(originalJdbc, properties);

		Integer port = null;
		String host = null;

		for (DriverPropertyInfo driverPropertyInfo : propertyInfo) {
			switch (driverPropertyInfo.name) {
			case "PORT":
				port = Integer.parseInt(driverPropertyInfo.value);
				break;
			case "HOST":
				host = driverPropertyInfo.value;
				break;
			}
		}

		SSHInfo sshInfo = new SSHInfo(underlyingDriver, uri);
		sshInfo.setRemoteHost(MoreObjects.firstNonNull(host, uri.getHost()));
		sshInfo.setRemotePort(MoreObjects.firstNonNull(port, uri.getPort()));

		Map<String, String> queryParams = new HashMap<>();
		// ssh infos
		String query = uri.getQuery();

		if (query != null) {
			String[] params = query.split("&");

			for (String param : params) {
				String[] split = param.split("=");
				queryParams.put(split[0], split[1]);
			}
		}

		sshInfo.setSshHost(queryParams.get("sshHost"));
		sshInfo.setSshUser(
				queryParams.get("sshUser") != null ? queryParams.get("sshUser") : System.getProperty("user.name"));

		sshInfo.setPrivateKey(queryParams.get("sshKey") != null ? queryParams.get("sshKey")
				: System.getProperty("user.home") + "/.ssh/id_rsa");
		sshInfo.setPassphrase(queryParams.get("sshPassphrase"));

		return sshInfo;

	}

	/**
	 * Given a <code>jdbc:ssh</code> type URL, find the underlying real driver
	 * that accepts the URL.
	 * 
	 * @param url
	 *            JDBC connection URL.
	 * 
	 * @return Underlying driver for the given URL. Null is returned if the URL
	 *         is not a <code>jdbc:ssh</code> type URL or there is no underlying
	 *         driver that accepts the URL.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs.
	 */
	private static Driver getUnderlyingDriver(String url) throws SQLException {

		Enumeration e = DriverManager.getDrivers();

		Driver d;
		while (e.hasMoreElements()) {
			d = (Driver) e.nextElement();

			if (d.acceptsURL(url)) {
				return d;
			}
		}
		return null;
	}

}
