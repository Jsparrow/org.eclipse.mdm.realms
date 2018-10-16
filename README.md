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
 
## available realms



## 1. Glassfish Login Realm (org.eclipse.mdm.realm.login.glassfish)

The command 'gradlew install' at org.eclipse.mdm.realms creates the jar file for this login module.

The jar file will be generated at **org.eclipse.mdm.realms/org.eclipse.mdm.realm.login.glassfish/build/libs/org.eclipse.mdm.realm.login.glassfish-1.0.0.jar**

1. **Install MDM Login Realm plugin at Glassfish**

* **copy** the jar file **org.eclipse.mdm.realm.login.glassfish-1.0.0.jar** to **GLASSFISH4_ROOT/glassfish/domains/domain1/lib**
   
* **open** the Glassfish login **configuration file** at **GLASSFISH4_ROOT/glassfish/domains/domain1/config/login.conf**
   
* **add** MDM realm module entry to this config file
        
      >MDMLoginRealm {
        org.eclipse.mdm.realm.login.glassfish.LoginRealmModule required;
      };    
      
* **start** Glassfish and **open administration website** (e.g. localhost:4848)
 
* **open site**: Configurations/server-config/Security/Realms
  
* **create** new **realm** ("New" Button)
  
	**Name:** MDMLoginRealm, **Class Name:** org.eclipse.mdm.realm.login.glassfish.LoginRealm _(choose a specific class name (with radio button))_
	
	**add Property:** **Name:** jaas-context, **Value:** MDMLoginRealm   

* **activate single sign on** at Configurations/server-config/Virtual Servers/server (SSO = Enabled)      
  
* **save and restart** the Glassfish application server