 Copyright (c) 2016 Gigatronik Ingolstadt GmbH
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 <h3>available realms</h3>
 <h5>glassfish login realm: org.eclipse.mdm.realm.login.glassfish</h5>
   
- build:
    The command 'gradlew installRealm' creates the jar file for this login module.
    The jar file will be generated at org.eclipse.mdm.realms/org.eclipse.mdm.realm.login.glassfish/build/libs/org.eclipse.mdm.realm.login.glassfish-1.0.0.jar
    <br>
    <b>install login realm plugin at glassfish:</b>
    <ul>
    <li>copy the jar file org.eclipse.mdm.realm.login.glassfish-1.0.0.jar to GLASSFISH4_ROOT/glassfish/domains/domain1/lib</li>
    <li>open the glassfish login configuration file GLASSFISH4_ROOT/glassfish/domains/domain1/config/login.conf</li>
    <li>
        add MDM realm module entry to this config file
        <code>
        MDMLoginRealm {
	        org.eclipse.mdm.realm.login.glassfish.LoginRealmModule required;
        };
    </code>
    </li>   
    <li>start glassfish GLASSFISH4_ROOT/glassfish/bin/startserv.bat</li>
    <li>open glassfish server admin console e.g. at localhost:4848 (default)</li>
    <li>open site: Configurations/server-config/Security/Realms</li>
    <li>create new realm ("New" Button)
        <code>    
        <b>Name:</b>        <b><i>MDMLoginRealm</i></b> (realm-name defined at web.xml at org.eclipse.mdm.application/src/main/configuration)
        <b>Class Name:</b>  <b><i>org.eclipse.mdm.realm.login.glassfish.LoginRealm</i></b> (choose a specific class name (radio button))
        add Property:
        <b>Name:</b>       <b><i>jaas-context</i></b>
        <b>Value:</b>      <b><i>MDMLoginRealm</i></b> (name defined at glassfish login.conf)
        </code>
    </li>
    <li>open site: Configurations/server-config/Virtual Servers</li>
    <li>choose server and enable SSO (Value: Enabled)</li>
    <li>save restart the glassfish application server</li>
    
    
    