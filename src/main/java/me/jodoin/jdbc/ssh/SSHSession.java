package me.jodoin.jdbc.ssh;

import com.jcraft.jsch.Session;

public class SSHSession {

	private int localPort;
	private Session session;

	public SSHSession(int localPort) {
		this.localPort = localPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}

	public String getLocalHost() {
		return "localhost";
	}
}
