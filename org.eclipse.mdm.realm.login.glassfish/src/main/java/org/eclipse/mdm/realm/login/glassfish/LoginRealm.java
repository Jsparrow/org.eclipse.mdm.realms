/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


package org.eclipse.mdm.realm.login.glassfish;

import java.util.Enumeration;
import java.util.Properties;

import com.sun.appserv.security.AppservRealm;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;

/**
 * Login Ream for Glassfish
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 */
public class LoginRealm extends AppservRealm {

	private final static String JAAS_CONTEXT = "jaas-context";
	private final static String AUTH_TYPE = "WebAuthorization";

	@Override
	public String getAuthType() {
		return AUTH_TYPE;
	}

	@Override
	public Enumeration<?> getGroupNames(String username) throws InvalidOperationException, NoSuchUserException {
		return null;
	}

	@Override
	protected void init(Properties props) throws BadRealmException, NoSuchRealmException {
		if (!props.containsKey(JAAS_CONTEXT)) {
			throw new BadRealmException("mandatory property with name '" + JAAS_CONTEXT + "' not found!");
		}
		String jaasContext = props.getProperty(JAAS_CONTEXT);
		setProperty(JAAS_CONTEXT, jaasContext);
	}

}
