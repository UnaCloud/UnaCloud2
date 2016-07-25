#UnaCloud
##Overview
UnaCloud is a project developed by the research group COMIT (Comunicaciones y Tecnología de Información) from Universidad de los Andes, Colombia. It is an open source opportunistic cloud computing Infrastructure as a Service (IaaS) implementation which provides basic computing resources (processing, storage, and networking) to run arbitrary software, including operating systems and applications.

UnaCloud is able to execute single instances and/or clusters of virtual machines. Its execution is mostly supported by off-the-shelf, volatile, non-dedicated, distributed, and heterogeneous computing resources (such as desktops) that belong to a variety of administrative domains on a university campus.

##Features
UnaCloud uses virtualization as a strategy to enable on-demand deployments of customized execution environments. These environments can meet complex software and hardware requirements from several research projects. UnaCloud uses type-2 hypervisors to isolate the end-user environment from another one based on, and dedicated to, harvesting idle computing resources.

In addition, UnaCloud executes virtual machines as independent idle-priority processes that run in background. This strategy allows the operating system to assign CPU time slices to higher-priority processes (e.g. foreground normal- priority processes executed by end-users). Both deployment strategies, virtualization and idle- priority execution, enable harvesting idle computing resources opportunistically, that is, virtual machines execute when an end-user is utilizing the desktop or when it is fully idle (e.g. at night or during weekends when the computer laboratories are closed to the public).

It is important to clarify that instead of volunteering their desktops, end-users in UnaCloud are unaware of the opportunistic use of desktops available in computer laboratories. Indeed, UnaCloud is always ready to stealthily execute virtual machines on demand. As a result, the design specifications of UnaCloud strongly consider slowdown, since it executes on laboratories that are mainly used by university students to do their daily work. The proposed solution was implemented and tested through the deployment of an opportunistic IaaS model, showing high efficiency in supporting academic and scientific projects.

##Requeriments
####UnaCloud Server
Specifications | Content
------------ | -------------
Number of machines	| 1 to 5 virtual or physical machines to deploy components
CPU	| 2 Cores Machine
Memory | 4GB
Free Disk	| 1 GB for UnaCloud Server and at least 80 GB of hard disk for Virtual Machines
OS	| Windows Server 2003, Windows 7, Ubuntu 14, Debian (6,7,8)
Supporting Features | Java JDK SE 7

####UnaCloud Agents
Specifications | Content
------------ | -------------
CPU	| 2 Cores Machine
Memory | At least 200 MB of free RAM.
Free Disk	| 50 MB for UnaCloud client and at least 20 GB of hard disk for Virtual Machines.
OS	| Windows XP, 7 or 8
Supporting Features | <ul><li>Java JRE SE 7</li><li>At least one of the following hypervisors:  VMware Workstation 6 or better (if you use VMWare Player, you must install VMware Player and VMware VIX together)</li><li>Oracle VM VirtualBox 4.2.14 or 4.3</li></ul>

##Download
The project could be download from [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/~unacloud/dokuwiki/doku.php?id=recursos:descargas) You could find three different options, manual installation, installation using scripts (Ubuntu or Debian) and using Vagrant (VirtualBox).

##Pre-Configuration
First step after download project is modify configuration file config.properties, this file is used in all installation options.

Set properties:

*	MAIN_REPOSITORY: storage path for virtual machines files in server.
*	DEFAULT_USER_PASSWORD: default password for admin user, it should be update after first login.
*	QUEUE_IP: IP address where is running RabbitMQ application. In case of use installation using scripts or Vagrant, use local address.
*	QUEUE_PORT: access port to RabbitMQ, by default is 5672.
*	QUEUE_USER: user with granted access to read and write for queue messages in RabbitMQ. In case of use installation using scripts or Vagrant, defined user in this variable will be created with privilegies granted.
*	QUEUE_PASS: RabbitMQ user password.
*	DB_USERNAME: user with granted access to read and write in MySQL database server. In case of use installation using scripts or Vagrant, user should be root.
*	DB_PASS: MySQL user password. 
*	DB_IP: IP address where is running database server. In case of use installation using scripts or Vagrant, use local address.
*	DB_PORT: access port to MySQL server, by default is 3306. 
*	DB_NAME: name of production database. 
*	WEB_SERVER_URL: Web server url. This url is composed by IP address, port and application name (UnaCloud). In case of use installation using scripts or Vagrant, use local address follows by port 8080 and application name UnaCloud: ip:8080/UnaCloud. Don't forget protocol.
*	AGENT_VERSION: initial version for agent.
*	CONTROL_SERVER_IP: IP address where CloudControl application will run. In case of use installation using scripts or Vagrant, use local address.
*	CONTROL_MANAGE_PM_PORT: CloudControl application port to receive control messages from agents. We recommend port range 10025 to 10035.
*	CONTROL_MANAGE_VM_PORT: CloudControl application port to receive control messages from agents with information about virtual machine executions. We recommend port range 10025 to 10035.
*	AGENT_PORT: Agent port to receive messages from CloudControl application. We recommend port range 10025 to 10035.
*	WEB_FILE_SERVER_URL: File manager url web application. This url is composed by IP address, port and application name (FileManager). In case of use installation using scripts or Vagrant, use local address follows by port 8080 and application name FileManager: ip:8080/FileManager. Don't forget protocol.
*	FILE_SERVER_PORT: FileManager application port to receive requests from agents to send files. We recommend port range 10025 to 10035.
*	FILE_SERVER_IP: IP address where FileManager application will run. In case of use installation using scripts or Vagrant, use local address.
*	VERSION_MANAGER_PORT: FileManager application port to receive messages from AgentUpdater application to manage update agent process. We recommend port range 10025 to 10035.
*	dev_url: this variable is used only in development environment, leave default value. 
*	dev_username: this variable is used only in development environment, leave default value. 
*	dev_password: this variable is used only in development environment, leave default value. 
*	test_username: this variable is used only in development environment, leave default value. 
*	test_password: this variable is used only in development environment, leave default value. 
*	test_url: this variable is used only in development environment, leave default value. 


##Installation
Users can choose Quick or Manual Installation depending on their needs to install the environment.

###Quick Installation using scripts
This kind of installation features a great velocity and not distributed components. Download package to install UnaCloud using scripts, scripts are designed to run in Ubuntu (12 or better) or Debian (6 or better), don't forget check requeriments.
* Install SSH server to allow access to server
* Unzip package in path of your preference.
* Choose repository folder. We recommend a folder with execution privilegies restricted.
* Update config.properties file. Check pre-configuration section.
* Set environment variable PATH_CONFIG pointed to config.properties file path.
* Execute file install.sh
```
bash install.sh
```
* The script will install in machine:
	* Apache Tomcat 7
	* UnaCloud web UI
	* MySQL Database
	* RabbitMQ
* Access in your browser to url http://IP:port/UnaCloud
* Log in with user defined in config.properties file.

###Quick Installation using Vagrant
This kind of installation features a great velocity, not distributed components and it is not needed a previous configured machine for server. Download package to install UnaCloud using Vagrant, this installation will run in a virtual machine, therefore your physical machine should accomplish requeriments.
* Install Vagrant from https://www.vagrantup.com/
* Install VirtualBox 4.3 or better.
* Unzip package in path of your preferences.
* Execute in terminal vagrantfile located in folder using command:
```
vagrant up
```
* Can access to virtual machine using command:
```
vagrant ssh
```
* Update config.properties file. Check pre-configuration section.
* Create environment variable PATH_CONFIG pointed to config.properties file path.
* Execute file install.sh
```
bash install.sh
```
* Vagrant will install will create machine with:
	* Apache Tomcat 7
	* UnaCloud web UI
	* MySQL Database
	* RabbitMQ
* Access in your browser to url http://IP:port/UnaCloud
* Log in with user defined in config.properties file.


###Manual Installation
This kind of installation package is designed to be distribuited and required from 1 to 5 fives machines. You can allocate server components in different execution nodes or in the same one.

####Node for MySQL server
* Install MySQL server
* Validate communication with MySQL port.
* Set database port in config.properties file.
* Create a database.
* Create an user with read and write privilegies on database.
* Set database name and user credentials in config.properties file.

####Node for RabbitMQ
* Install RabbitMQ
* Configure user with read and write queues privilegies.
* Validate communication with RabbitMQ port.
* Set RabbitMQ port and user credentials in config.properties file.

####Node for CloudControl application
* Install Java 7
* Allow communication by TCP and UDP in two different ports of your preference.
* Set ports in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Allocate CloudControl.jar in path of your preferences.
* Execute file by command:
```
java –jar CloudControl.jar
```
* Configure that application runs when machine starts.

####Node for FileManager application
* Install Java 7
* Install and configure Tomcat 8
* Allow communication by TCP in two different ports of your preference.
* Allow communication by HTTP in configured port for Tomcat.
* Allocate file FileManager.war in webapps Tomcat folder.
* Create a folder which works as repository for virtual machine files. 
* Set ports, repository path, and URL in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Start Tomcat server
* Access in your browser to url http://IP:port/FileManager
* Configure that Tomcat runs when machine starts.

####Node for UnaCloud application
* Install Java 7
* Install and configure Tomcat 8
* Allow communication by HTTP in configured port for Tomcat.
* Allocate file UnaCloud.war in webapps Tomcat folder.
* Set URL in config.properties file.
* Allocate config.properties file in path of your preference.
* Set an environment variable PATH_CONFIG pointing to configuration file.
* Start Tomcat server
* Access in your browser to url http://IP:port/UnaCloud
* Configure that Tomcat runs when machine starts.
* Log in with user "Admin" and default password setted in config.properties file.

##Configuration
###Agent
To add an Agent to UnaCloud you should create a PhysicalMachine in UnaCloud. 
* Access to UnaCloud with admin user.
* Create a Laboratory if you don't have one.
* Add a new PhysicalMachine with values from real PhysicalMachine including IP address, hostname and MAC address.
* Machine will appear with a red label with value OFF.

Access to menu Configuration > Agent management and select Download from Download Agent Files section. This package contains:
* ClienUpdater.jar: application to download and update agent from server.
* Global.properties: configuration file with variables to conect with server services.
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
* Create a text file like the next one that will include commands in order to change the path and execute the client updater jar. Save it as a .bat file.
```
startUnacloud.bat

cd C:\UnaCloud\

java –jar ClientUpdater.jar 1
```
* Open the Local Group Policy Editor (Type gpedit.msc from the start menu)
* In the console tree, click Scripts (Startup/Shutdown). The path is Computer Configuration\Windows Settings\Scripts (Startup/Shutdown)
* Click Startup and then Add
* Insert the path of your .bat file on script name.
* Click ok and then ok. The next time that you restart the machine, it will start with UnaCloud Agent.

##Documentation
Unfortunately we only have detailed documentation in spanish, we hope to offer this documentation in english very soon. You can find it in [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/~unacloud/dokuwiki/doku.php?id=inicio)

Code documentation? please check docs folder in github project.

##Using UnaCloud
You can follow our user manual located [UnaCloud Wiki](https://sistemasproyectos.uniandes.edu.co/~unacloud/dokuwiki/doku.php?id=inicio)

##Research
UnaCloud is based on research publications, that were made by members of investigation group COMMIT from Universidad de los Andes, which develop, analyze and expose the features of implementation in order to involve the better policies to service of UnaCloud.

* [UnaCloud: Opportunistic Cloud Computing Infrastructure as a Service](http://www.thinkmind.org/download.php?articleid=cloud_computing_2011_7_40_20055)
* [UnaGrid: On Demand Opportunistic Desktop Grid](http://dx.doi.org/10.1109/CCGRID.2010.79)
* [Harvesting Idle CPU Resources for Desktop Grid computing while Limiting the Slowdown Generated to End-users](http://link.springer.com/article/10.1007%2Fs10586-015-0482-4)
* [Supporting e-Science Applications through the On-Demand Execution of Opportunistic and Customizable Virtual Clusters](http://www.naun.org/multimedia/NAUN/computers/17-258.pdf)
* [Running MPI Applications over an Opportunistic Infrastructure](http://link.springer.com/chapter/10.1007/978-3-662-45483-1_8)
* [Desktop Grids and Volunteer Computing Systems](http://www.igi-global.com/chapter/desktop-grids-volunteer-computing-systems/58739)

##License
License: [GNU GPL v2](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)
