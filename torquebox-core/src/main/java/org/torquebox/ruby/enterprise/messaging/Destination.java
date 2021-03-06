package org.torquebox.ruby.enterprise.messaging;

import org.hornetq.jms.server.JMSServerManager;
import org.jboss.logging.Logger;

public abstract class Destination {
	
	protected  Logger log;
	
	private JMSServerManager server;
	private String name;

	public Destination() {
		log = Logger.getLogger( getClass() );
	}
	
	public void setServer(JMSServerManager server) {
		this.server = server;
	}
	
	public JMSServerManager getServer() {
		return this.server;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public abstract void start() throws Exception;
	
	public abstract void stop() throws Exception;
}
