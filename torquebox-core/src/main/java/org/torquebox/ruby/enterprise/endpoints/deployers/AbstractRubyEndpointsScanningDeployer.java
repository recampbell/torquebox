/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.torquebox.ruby.enterprise.endpoints.deployers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;
import org.torquebox.ruby.core.deployers.AbstractRubyScanningDeployer;
import org.torquebox.ruby.core.util.StringUtils;
import org.torquebox.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;
import org.torquebox.ruby.enterprise.endpoints.metadata.RubyEndpointsMetaData;

public class AbstractRubyEndpointsScanningDeployer extends AbstractRubyScanningDeployer {

	private static final String SUFFIX = "_endpoint.rb";
	
	private static final SuffixMatchFilter ENDPOINT_CLASS_FILTER = new SuffixMatchFilter(SUFFIX);

	@SuppressWarnings("unused")
	private static final String DEFAULT_TRUST_STORE = "auth/truststore.jks";

	public AbstractRubyEndpointsScanningDeployer() {
		addOutput(RubyEndpointsMetaData.class);
		setFilter( ENDPOINT_CLASS_FILTER );
	}

	protected void deploy(VFSDeploymentUnit unit, VirtualFile rubyFile, String relativePath) throws DeploymentException {
		log.info( "deploy " + relativePath );
		String rubyFileName  = relativePath;
		String name          = rubyFileName.substring(0, rubyFileName.length() - SUFFIX.length());
		String wsdlLocation  = name + ".wsdl";
		
		log.info( "name [" + name + "]" );
		log.info( "rubyFileName [" + rubyFileName + "]" );
		log.info( "wsdlLocation [" + wsdlLocation + "]" );
		log.info( "className [" + getEndpointClassName(name) + "]" );
		
		try {
			VirtualFile wsdlFile = unit.getRoot().getChild( getPath() ).getChild( wsdlLocation );
			log.info( "wsdlFile " + wsdlFile );
			
			RubyEndpointMetaData metaData = new RubyEndpointMetaData();

			metaData.setName(name);
			metaData.setEndpointClassName(getEndpointClassName(name));
			
			if ( wsdlFile != null ) {
				log.info( "defaulting to WSDL at " + wsdlFile );
				metaData.setWsdlLocation(wsdlFile.toURL());
			}
			
			String classLocation = rubyFileName.substring(0, rubyFileName.length() - 3 );
			metaData.setClassLocation(classLocation);
			
			RubyEndpointsMetaData endpoints = unit.getAttachment( RubyEndpointsMetaData.class );
			
			if ( endpoints == null ) {
				endpoints = new RubyEndpointsMetaData();
				unit.addAttachment( RubyEndpointsMetaData.class, endpoints );
			}
			endpoints.addEndpoint( metaData );
		} catch (IOException e) {
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			throw new DeploymentException(e);
		}
	}

	private String getEndpointClassName(String name) {
		return StringUtils.camelize(name + "Endpoint");
	}

}
