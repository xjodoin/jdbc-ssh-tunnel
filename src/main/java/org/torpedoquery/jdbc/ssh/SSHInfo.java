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

import java.net.URI;
import java.sql.Driver;

public class SSHInfo {

	private String privateKey;

	private String passphrase;

	private String sshUser;

	private String sshHost;

	private int sshPort;

	private String remoteHost;

	private int remotePort;

	private Driver underlyingDriver;

	private URI originalUri;

	public SSHInfo(Driver underlyingDriver, URI originalUri) {
		this.setOriginalUri(originalUri);
		this.setUnderlyingDriver(underlyingDriver);
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}


	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public Driver getUnderlyingDriver() {
		return underlyingDriver;
	}

	public void setUnderlyingDriver(Driver underlyingDriver) {
		this.underlyingDriver = underlyingDriver;
	}

	public URI getOriginalUri() {
		return originalUri;
	}

	public void setOriginalUri(URI originalUri) {
		this.originalUri = originalUri;
	}

	public String getSshUser() {
		return sshUser;
	}

	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}

	public String getSshHost() {
		return sshHost;
	}

	public void setSshHost(String sshHost) {
		this.sshHost = sshHost;
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((originalUri == null) ? 0 : originalUri.hashCode());
		result = prime * result + ((passphrase == null) ? 0 : passphrase.hashCode());
		result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
		result = prime * result + ((remoteHost == null) ? 0 : remoteHost.hashCode());
		result = prime * result + remotePort;
		result = prime * result + ((sshHost == null) ? 0 : sshHost.hashCode());
		result = prime * result + sshPort;
		result = prime * result + ((sshUser == null) ? 0 : sshUser.hashCode());
		result = prime * result + ((underlyingDriver == null) ? 0 : underlyingDriver.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SSHInfo other = (SSHInfo) obj;
		if (originalUri == null) {
			if (other.originalUri != null)
				return false;
		} else if (!originalUri.equals(other.originalUri))
			return false;
		if (passphrase == null) {
			if (other.passphrase != null)
				return false;
		} else if (!passphrase.equals(other.passphrase))
			return false;
		if (privateKey == null) {
			if (other.privateKey != null)
				return false;
		} else if (!privateKey.equals(other.privateKey))
			return false;
		if (remoteHost == null) {
			if (other.remoteHost != null)
				return false;
		} else if (!remoteHost.equals(other.remoteHost))
			return false;
		if (remotePort != other.remotePort)
			return false;
		if (sshHost == null) {
			if (other.sshHost != null)
				return false;
		} else if (!sshHost.equals(other.sshHost))
			return false;
		if (sshPort != other.sshPort)
			return false;
		if (sshUser == null) {
			if (other.sshUser != null)
				return false;
		} else if (!sshUser.equals(other.sshUser))
			return false;
		if (underlyingDriver == null) {
			if (other.underlyingDriver != null)
				return false;
		} else if (!underlyingDriver.equals(other.underlyingDriver))
			return false;
		return true;
	}

	
}
