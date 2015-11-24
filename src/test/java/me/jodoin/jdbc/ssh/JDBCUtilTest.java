package me.jodoin.jdbc.ssh;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.Driver;

public class JDBCUtilTest {

	@BeforeClass
	public static void setup() throws ClassNotFoundException {
		Class.forName(Driver.class.getName());
	}

	@Test
	public void testWithoutPort() throws SQLException, URISyntaxException {
		SSHInfo ssHinfo = JDBCUtil.getSSHinfo("jdbc:ssh:mysql://localhost/feedback", new Properties());

		assertEquals("localhost", ssHinfo.getRemoteHost());
		assertEquals(3306, ssHinfo.getRemotePort());
	}

	@Test
	public void testWithPort() throws SQLException, URISyntaxException {
		SSHInfo ssHinfo = JDBCUtil.getSSHinfo("jdbc:ssh:mysql://toto.com:3333/feedback", new Properties());

		assertEquals("toto.com", ssHinfo.getRemoteHost());
		assertEquals(3333, ssHinfo.getRemotePort());
	}

	@Test
	public void testWithSshInfo() throws SQLException, URISyntaxException {
		SSHInfo ssHinfo = JDBCUtil.getSSHinfo("jdbc:ssh:mysql://toto.com:3333/feedback?sshUser=test&sshHost=test.com",
				new Properties());

		assertEquals("test.com", ssHinfo.getSshHost());
		assertEquals("test", ssHinfo.getSshUser());
	}

}
