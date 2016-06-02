<!--
Copyright (c) 2016 Gigatronik Ingolstadt GmbH
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
-->
 
## available realms

## 1. Glassfish Login Realm (org.eclipse.mdm.realm.login.glassfish)

Before you can install and build the realm, you have to checkout and install: (gradlew install)
* org.eclipse.mdm.api.base
* org.eclipse.mdm.api.default
* org.eclipse.mdm.api.odsadapter
* org.eclipse.mdm.nucleus (see also README.md)
  
The command 'gradlew installRealm' at org.eclipse.mdm.realms/org.eclipse.mdm.realm.login.glassfish creates the jar file for this login module.

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
  
* **save and restart** the Glassfish application server