package org.torquebox.ruby.core.runtime.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.torquebox.ruby.core.runtime.metadata.PoolMetaData;
import org.torquebox.ruby.core.runtime.metadata.PoolingMetaData;
import org.torquebox.ruby.enterprise.web.rack.metadata.RubyRackApplicationMetaData;

public class DefaultPoolingDeployer extends AbstractDeployer {
	
	public static final String[] DEFAULT_POOLS = { "web", "jobs", "queues", "endpoints" };

	public DefaultPoolingDeployer() {
		setInput(RubyRackApplicationMetaData.class);
		addInput(PoolingMetaData.class);
		setStage( DeploymentStages.POST_PARSE );
	}
	
	@Override
	public void deploy(DeploymentUnit unit) throws DeploymentException {
		PoolingMetaData pooling = unit.getAttachment( PoolingMetaData.class );
		
		if ( pooling == null ) {
			pooling = new PoolingMetaData();
			unit.addAttachment( PoolingMetaData.class, pooling );
		}
		
		deploy( pooling );
	}
	
	protected void deploy(PoolingMetaData pooling) throws DeploymentException {
		for ( String name : DEFAULT_POOLS ) {
			if ( pooling.getPool( name ) == null ) {
				PoolMetaData pool = new PoolMetaData();
				pool.setName( name );
				pool.setGlobal();
				pooling.addPool( pool );
			}
		}
	}

}
