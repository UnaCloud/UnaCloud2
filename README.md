# UnaCloud
## Overview
UnaCloud is a project developed by the research group COMIT (Comunicaciones y Tecnología de Información) from Universidad de los Andes, Colombia. It is an open source opportunistic cloud computing Infrastructure as a Service (IaaS) implementation which provides basic computing resources (processing, storage, and networking) to run arbitrary software, including operating systems and applications.

UnaCloud is able to execute single instances and/or clusters of virtual machines. Its execution is mostly supported by off-the-shelf, volatile, non-dedicated, distributed, and heterogeneous computing resources (such as desktops) that belong to a variety of administrative domains on a university campus.

UnaCloud is a Client-Server application which can be used currently in desktops with Windows or Linux (Debian, Ubuntu) operating systems. 

## Features
UnaCloud uses virtualization as a strategy to enable on-demand deployments of customized execution environments. These environments can meet complex software and hardware requirements from several research projects. UnaCloud uses type-2 hypervisors to isolate the end-user environment from another one based on, and dedicated to, harvesting idle computing resources.

In addition, UnaCloud executes instances as independent idle-priority processes that run in background. This strategy allows the operating system to assign CPU time slices to higher-priority processes (e.g. foreground normal- priority processes executed by end-users). Both deployment strategies, virtualization and idle- priority execution, enable harvesting idle computing resources opportunistically, that is, execution instances are executed when an end-user is using the desktop or when it is fully idle (e.g. at night or during weekends when the computer laboratories are closed to the public).

It is important to clarify that instead of volunteering their desktops, end-users in UnaCloud are unaware of the opportunistic use of machines available in computer laboratories. Indeed, UnaCloud is always ready to stealthily execute instances on demand. As a result, the design specifications of UnaCloud strongly consider slowdown, since it is executed on laboratories that are mainly used by university students working on their daily activities. The proposed solution was implemented and tested through the deployment of an opportunistic IaaS model, showing high efficiency in supporting academic and scientific projects.

Among its features, UnaCloud allows the user to deploy a large number of instances (eg. 100) using one from two different available protocols, TCP or P2P.

## Requeriments
#### UnaCloud Server

| Specifications | Content |
| ------------ | -------------
| Number of machines	| 1 to 5 virtual or physical machines to deploy components
| CPU	| 2 Cores Machine
| Memory | 4GB
| Free Disk	| 1 GB for UnaCloud Server and at least 80 GB hard disk for image files
| OS	| UnaCloud server has been mainly tested in Ubuntu Server (14 to 16).
| Supporting Features | Java JDK SE 8

#### UnaCloud Agents

| Specifications | Content
| ------------ | -------------
| CPU	| 2 Cores Machine
| Memory | At least 200 MB of free RAM.
| Free Disk	| 50 MB for UnaCloud client and at least 20 GB hard disk for image files.
| OS	| UnaCloud Agent has been tested mainly in Windows: XP, 7, 8 or 10. and Linux: Debian (6 to 8) and Ubuntu (10 to 14)
| Supporting Features | <ul><li>Java JRE SE 8</li><li>At least one of the following platforms:  VMware Workstation 6 to 10 (if you use VMWare Player, you must install VMware Player and VMware VIX together)</li><li>Oracle VM VirtualBox 4.2, 4.3 or 5.* Unacloud has been mainly tested using Oracle VM VirtualBox 4.2 and 5.</li></ul>

## Download
The project can be downloaded from this repository in scripts/Install_packages folder or from [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/iniciativas/unacloud/es/inicio/). You can find three different options: Manual Installation, Script-based Installation (Ubuntu or Debian) or Vagrant Installation(VirtualBox).

## Pre-Configuration
After downloading project, modify configuration file config.properties (for all installation options).
Note: Vagrant Installation Option configuration file has default values currently, we recommend at least to change passwords in file and IP_FOR_UNACLOUD word in WEB_FILE_SERVER_URL, CONTROL_SERVER_IP y FILE_SERVER_IP variables by IP address defined for UnaCloud Server.

Set following properties:

*	MAIN_REPOSITORY: Server storage path for image files.
*	DEFAULT_USER_PASSWORD: default password for admin user, it should be updated after first login.
*	QUEUE_IP: RabbitMQ application IP address. In case of Script-based or Vagrant Installation, use local address.
*	QUEUE_PORT: RabbitMQ access port, by default is 5672.
*	QUEUE_USER: user with granted access to read and write for queue messages in RabbitMQ. In case of Script-based or Vagrant Installation, defined user in this variable will be created with granted privileges.
*	QUEUE_PASS: RabbitMQ user password.
*	DB_USERNAME: user with granted access to read and write in MySQL database server. In case of Script-based or Vagrant Installation, user should be root.
*	DB_PASS: MySQL user password. 
*	DB_IP: MySQL server IP address. In case of Script-based or Vagrant Installation, use local address.
*	DB_PORT: MySQL server access port, by default is 3306. 
*	DB_NAME: production database name. 
*	WEB_SERVER_URL: Web server url. This url is composed by IP address, port and application name (UnaCloud). In case of Script-based installation use local address followed by port 8080 and application name UnaCloud: ip:8080/UnaCloud, don't forget protocol. In case vagrant-based installation replace current IP address by one defined for UnaCloud Server follows by port 8080; ip:8080/UnaCloud, don't forget protocol.
*	AGENT_VERSION: initial version for agent.
*	CONTROL_SERVER_IP: CloudControl application IP address. In case of Script-based or use host IP address. In case of Vagrant-based installation use IP address defined for UnaCloud Server.
*	CONTROL_MANAGE_PM_PORT: CloudControl application port to receive control messages from agents. We recommend port range 10025 to 10035.
*	CONTROL_MANAGE_VM_PORT: CloudControl application port to receive control messages from agents with information about execution instance. We recommend port range 10025 to 10035.
*	AGENT_PORT: Agent port to receive messages from CloudControl application. We recommend port range 10025 to 10035.
*	WEB_FILE_SERVER_URL: FileManager web application url. This url is composed by IP address, port and application name (FileManager). In case of Script-based installation use local address followed by port 8080 and application name FileManager: ip:8080/FileManager, don't forget protocol. In case vagrant-based installation replace current IP address by one defined for UnaCloud Server follows by port 8080; ip:8080/FileManager, don't forget protocol.
*	FILE_SERVER_PORT: FileManager application port to receive requests from agents to send files. We recommend port range 10025 to 10035.
*	FILE_SERVER_IP: FileManager application IP address. In case of Script-based use host IP address. In case of Vagrant-based installation use IP address defined for UnaCloud Server.
*	VERSION_MANAGER_PORT: FileManager application port to receive messages from AgentUpdater application to manage update agent process. We recommend port range 10025 to 10035.
*	TORRENT_CLIENT_PORTS: Five ports used by UnaCloud agents to share files using P2P protocol. We recommend port range 10025 to 10035. These ports should be delimited by commas (eg. 10031,10032,10033,10034,10035).
*	FILE_SERVER_TORRENT_PORT: Is the port used by the UnaCloud server in case of a P2P deployment request.
*	dev_url: this variable is used only in development environment, leave default value. 
*	dev_username: this variable is used only in development environment, leave default value. 
*	dev_password: this variable is used only in development environment, leave default value. 
*	test_username: this variable is used only in development environment, leave default value. 
*	test_password: this variable is used only in development environment, leave default value. 
*	test_url: this variable is used only in development environment, leave default value. 

## Installation
Users can choose Quick or Manual Installation depending on their needs to install the environment.

### Quick Script-based Installation
This kind of installation is very fast and does not use distributed components. Download package for Script-based Installation, scripts are designed to run in Ubuntu (14 or later), don't forget to check system requeriments. In case of using a virtual machine in NAT don't forget to configure port forwarding using correct protocol (UDP for CONTROL ports).
* Install SSH server to allow access to server
* Unzip package in path of your preference.
* Choose repository folder. We recommend a folder with restricted execution privileges.
* Update config.properties file. Check pre-configuration section.
* Set environment variable PATH_CONFIG pointed to config.properties file path and set it in third line in install.sh script file.
* Execute file install.sh
```
bash install.sh
```
* The script will install in machine:
	* Java 8
	* Apache Tomcat 8
	* UnaCloud server components
	* MySQL Database
	* RabbitMQ
* Execute file start.sh
* Access in your browser to url http://IP:port/UnaCloud
* Log in with user defined in config.properties file.

### Quick Installation using Vagrant
This kind of installation is very fast, does not use distributed components and does not need a previous configured machine for server. Download package for Vagrant Installation, this installation will run in a virtual machine therefore your physical machine should meet system requeriments.
* Install Vagrant from https://www.vagrantup.com/
* Install VirtualBox 4.3 or better.
* Unzip package in path of your preference.
* Replace IP_FOR_UNACLOUD word in vagrantfile by the IP address you defined for UnaCloud Server
* Replace IP_FOR_UNACLOUD word in fields WEB_SERVER_URL, WEB_FILE_SERVER_URL, CONTROL_SERVER_IP and FILE_SERVER_IP in config.properties, by the IP address you defined for UnaCloud Server
* Execute in terminal vagrantfile located in folder using command:
```
vagrant up
```
* Can access to virtual machine using command:
```
vagrant ssh
```
* Vagrant will configure machine with:
	* Java 8
	* Apache Tomcat 8
	* UnaCloud Server components
	* MySQL Database
	* RabbitMQ
* Access in your browser to url http://IP_FOR_UNACLOUD:8080/UnaCloud
* Log in with user admin and change password in profile segment


### Manual Installation
This kind of installation package is designed to be distribuited and requires between 1 and 5 fives machines. You can allocate server components in different execution nodes or in the same one.

#### Node for MySQL server
* Install and configure MySQL server
* Modify the `my.cnf` file in the route `/etc/mysql/my.cnf` commenting the line:
```
#bind-address = 127.0.0.1
```
* Enter the MySQL console and execute the following commands:
```
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;
 FLUSH PRIVILEGES;
 ```
* Validate communication with MySQL port.
* Set database port in config.properties file.
* Create a database.
* Create an user with read and write privileges on database.
* Set database name and user credentials in config.properties file.

#### Node for RabbitMQ
* Install RabbitMQ
* Configure user with read and write queues privileges.
* Validate communication with RabbitMQ port.
* Configure RabbitMQ service to run in startup machine process
* Set RabbitMQ port and user credentials in config.properties file.

#### Node for CloudControl application
* Install Java 8
* Allow communication by TCP and UDP in two different ports of your preference.
* Set ports in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Allocate CloudControl.jar in path of your preference.
* Execute file by command:
```
java –jar CloudControl.jar
```
* Configure application to run when machine starts.

#### Node for FileManager application
* Install Java 8
* Install and configure Tomcat 8
* Allow communication by TCP in two different ports of your preference.
* Allow communication by HTTP in configured port for Tomcat.
* Allocate file FileManager.war in webapps Tomcat folder.
* Create a folder which works as repository for image files. 
* Set ports, repository path, and URL in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Start Tomcat server
* Access in your browser to url http://IP:port/FileManager
* Configure Tomcat to run when machine starts.

#### Node for UnaCloud application
* Install Java 7
* Install and configure Tomcat 8
* Allow communication by HTTP in configured port for Tomcat.
* Allocate file UnaCloud.war in webapps Tomcat folder.
* Set URL in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Start Tomcat server
* Access in your browser to url http://IP:port/UnaCloud
* Configure Tomcat to run when machine starts.
* Log in with user "Admin" and default password setted in config.properties file.

Note: It is necessary that all nodes have same datetime configured. We recommend use a NTP server in your local net for this purpose.

## Configuration
### Agent
To add an Agent to UnaCloud you should create a PhysicalMachine in UnaCloud. 
* Access to UnaCloud with admin user.
* Create a Laboratory if you don't have one.
* Add a new PhysicalMachine with values from real PhysicalMachine including IP address, hostname and MAC address.
* Machine will appear with a red label with value OFF.

Access to menu Configuration > Agent management and select Download from Download Agent Files section. This package contains:
* ClientUpdater.jar: application to download and update agent from server.
* Global.properties: configuration file with variables to connect with server services.
* Local.properties: configuration file to set local environment variables.

Unzip files in physical machine (host) and allocate them in path of your preference. Next modify file local.properties:
* VBOX_PATH: VBoxManage application path
* VMRUN_PATH: vmrun.exe application path
* VM_REPO_PATH: local repository path. Path of your preference.
* DATA_PATH: logs folder path. Path of your preference.

Use slash character before ":" and "\"
```
DATA_PATH=E\:\\
VM_REPO_PATH=E\:\\GRID
VMRUN_PATH=C\:\\Program Files (x86)\\VMware\\VMware VIX\\vmrun.exe
VBOX_PATH=C\:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe
```

Finally, when the configuration process is finished, you must add the ClientUpdater.jar file as a boot script, following these steps:

#### Windows
* Create a text file that includes following commands in order to change the path and execute the client updater jar. Save it as a .bat file, i.e startUnacloud.bat.
```
cd C:\UnaCloud\

java –jar ClientUpdater.jar 1
```
* Open the Local Group Policy Editor (Type gpedit.msc from the start menu)
* In the console tree, click Scripts (Startup/Shutdown). The path is Computer Configuration\Windows Settings\Scripts (Startup/Shutdown)
* Click Startup and then Add
* Insert the path of your .bat file on script name.
* Click ok and then ok. The next time that you restart the machine, it will start with UnaCloud Agent.

#### Linux
* Create a text file that includes following commands in order to change the path and execute the client updater jar. Save it as a .bash file, i.e startUnacloud.bash.
```
#!/bin/sh
cd /etc/UnaCloud/

java –jar ClientUpdater.jar 1
```
* Change priviligies on script only for root user
* Configure startup execution for script through rc.local file or /etc/init folder. This configuration depends of your operating system version, please check official manual.
* Check again if your Java is installed correctly using "java -version" command.

## Documentation
Unfortunately we only have detailed documentation in spanish, we hope to offer this documentation in english very soon. You can find it in [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/iniciativas/unacloud/es/inicio/)

Code documentation? please check docs folder in github project.

## Using UnaCloud
You can follow our user manual located in [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/iniciativas/unacloud/es/inicio/)

## Research
UnaCloud is based on research publications, that were made by members of investigation group COMMIT from Universidad de los Andes, which develop, analyze and expose the features of implementation, in order to improve UnaCloud service.

* [UnaCloud: Opportunistic Cloud Computing Infrastructure as a Service](http://www.thinkmind.org/download.php?articleid=cloud_computing_2011_7_40_20055)
* [UnaGrid: On Demand Opportunistic Desktop Grid](http://dx.doi.org/10.1109/CCGRID.2010.79)
* [Harvesting Idle CPU Resources for Desktop Grid computing while Limiting the Slowdown Generated to End-users](http://link.springer.com/article/10.1007%2Fs10586-015-0482-4)
* [Supporting e-Science Applications through the On-Demand Execution of Opportunistic and Customizable Virtual Clusters](http://www.naun.org/multimedia/NAUN/computers/17-258.pdf)
* [Running MPI Applications over an Opportunistic Infrastructure](http://link.springer.com/chapter/10.1007/978-3-662-45483-1_8)
* [Desktop Grids and Volunteer Computing Systems](http://www.igi-global.com/chapter/desktop-grids-volunteer-computing-systems/58739)

## License
License: [GNU GPL v2](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)
