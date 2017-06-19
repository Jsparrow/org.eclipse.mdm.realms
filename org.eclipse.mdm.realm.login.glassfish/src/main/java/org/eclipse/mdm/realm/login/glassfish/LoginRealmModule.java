/*******************************************************************************
  * Copyright (c) 2016 Gigatronik Ingolstadt GmbH
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Sebastian Dirsch - initial implementation
  *******************************************************************************/

package org.eclipse.mdm.realm.login.glassfish;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.glassfish.internal.api.Globals;
import org.glassfish.security.common.Group;

import com.sun.enterprise.security.PrincipalGroupFactory;
import com.sun.enterprise.security.auth.login.common.PasswordCredential;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.Realm;

/**
 * Login Module for Glassfish
 * 
 * @author Sebastian Dirsch, Gigatronik Ingolstadt GmbH
 *
 */
public class LoginRealmModule implements LoginModule {

	private final static String MDM_GROUP = "MDM";

	private Subject subject;
	private String username = null;
	private String password = null;
	private String realm = null;

	private Principal principal = null;
	private Group group = null;

	private boolean phase1Succeeded = false;
	private boolean phase2Succeeded = false;

	private Object connector = null;
	private Object entityManagerList = null;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.subject = subject;
	}

	@Override
	public boolean login() throws LoginException {
		try {
			PasswordCredential passwordCredential = lookupPasswordCredential(this.subject);

			this.realm = passwordCredential.getRealm();
			this.username = passwordCredential.getUser();
			this.password = String.valueOf(passwordCredential.getPassword());

			Object connectorService = getConnector();
			Method connectionMethod = connector.getClass().getMethod("connect", String.class, String.class);
			this.entityManagerList = connectionMethod.invoke(connectorService, username, password);
			this.phase1Succeeded = true;

		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | RuntimeException e) {
			throw new LoginException(e.getMessage());
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean commit() throws LoginException {

		try {

			if (!this.phase1Succeeded) {
				return false;
			}

			if (this.entityManagerList != null && ((List<Object>) this.entityManagerList).size() > 0) {

				PrincipalGroupFactory factory = Globals.getDefaultHabitat().getService(PrincipalGroupFactory.class);

				this.principal = createPrincipal(factory);
				this.group = createGroup(factory);

				Set<Principal> principalSet = this.subject.getPrincipals();

				if (!principalSet.contains(this.principal)) {
					principalSet.add(this.principal);
				}

				if (!principalSet.contains(this.group)) {
					principalSet.add(this.group);
				}

				Object connectorService = getConnector();
				Method regConnectionMethod = connector.getClass().getMethod("registerConnections", Principal.class,
						List.class);
				regConnectionMethod.invoke(connectorService, this.principal, this.entityManagerList);

				this.phase2Succeeded = true;
			} else {
				throw new LoginException("unable to connect '" + this.username + "'");
			}

			return true;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | RuntimeException e) {
			throw new LoginException(e.getMessage());
		} finally {
			this.username = null;
			this.password = null;
		}
	}

	@Override
	public boolean abort() throws LoginException {

		if (!this.phase1Succeeded) {
			return false;
		} else if (this.phase2Succeeded && !this.phase2Succeeded) {
			this.phase1Succeeded = false;
			this.username = null;
			this.password = null;
			this.principal = null;
			this.group = null;
		} else {
			logout();
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean logout() throws LoginException {

		try {

			Object connectorService = getConnector();
			Method disconnectMethod = connectorService.getClass().getMethod("disconnect", Principal.class);
			disconnectMethod.invoke(connectorService, this.principal);

			((List<Object>) this.entityManagerList).clear();

			this.subject.getPrincipals().clear();
			this.subject.getPublicCredentials().clear();
			this.subject.getPrivateCredentials().clear();

			this.username = null;
			this.password = null;
			this.principal = null;
			this.group = null;

			this.phase1Succeeded = false;
			this.phase2Succeeded = false;

			return true;

		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | RuntimeException e) {
			throw new LoginException(e.getMessage());
		}
	}

	private Principal createPrincipal(PrincipalGroupFactory factory) throws LoginException {
		try {
			Realm realmInstance = Realm.getInstance(this.realm);
			return factory.getPrincipalInstance(this.username, realmInstance.getName());
		} catch (NoSuchRealmException e) {
			throw new LoginException(e.getMessage());
		}
	}

	private Group createGroup(PrincipalGroupFactory factory) throws LoginException {
		try {
			Realm realmInstance = Realm.getInstance(this.realm);
			return factory.getGroupInstance(MDM_GROUP, realmInstance.getName());
		} catch (NoSuchRealmException e) {
			throw new LoginException(e.getMessage());
		}
	}

	private PasswordCredential lookupPasswordCredential(Subject subject) throws LoginException {
		Set<Object> privateCredentials = subject.getPrivateCredentials();
		for (Object privateCredential : privateCredentials) {
			if (privateCredential instanceof PasswordCredential) {
				return ((PasswordCredential) privateCredential);
			}
		}
		throw new LoginException("password credential not found!");
	}

	private Object getConnector() throws LoginException {
		try {
			if (this.connector == null) {
				String JNDIName = "java:global/org.eclipse.mdm.nucleus/ConnectorService!org.eclipse.mdm.connector.boundary.ConnectorService";
				InitialContext initialContext = new InitialContext();
				this.connector = initialContext.lookup(JNDIName);
			}
			return this.connector;
		} catch (NamingException e) {
			throw new LoginException(e.getMessage());
		}
	}

}
