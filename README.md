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

##Installation
Users can choose Quick or Manual Installation depending on their needs to install the environment.

###Quick Installation
Download Files
```
Commands
```
Execute Files
```
Batch
```
The script will install the machine with:
* Apache Tomcat 7
* UnaCloud web UI
* Database...
* RabbitMQ
* Others

###Manual Installation
Download Files
```
Commands
```
####Components
#####Component 1
Download Files
```
Commands
```
Instructions
#####Component 2
Download Files
```
Commands
```
Instructions
#####Component n
Download Files
```
Commands
```
Instructions
####Deployments
#####Agent Deployment
Instructions

#####Server Deployment
Instructions

##Configuration
###Agent

###Server

##Using UnaCloud
Unacloud has three main functional entities: Clusters, Images and Virtual Machines. Virtual Machine Images are the AMI equivalent in the AWS EC2, and each one represents the virtual machine files that will be copied and deployed in the physical machines. A cluster is an image aggrupation, which allows making deployments of different images at once (like needed in a master-slave cluster schema). Finally, a virtual machine is an image already in execution.
### Clusters
How we test that is working?
### Images
How we test that is working?
### Virtual Machines
How we test that is working?
##Research
UnaCloud is based on research publications, that were made by members of investigation group COMMIT from Universidad de los Andes, which develop, analyze and expose the features of implementation in order to involve the better policies to service of UnaCloud.

* [UnaCloud: Opportunistic Cloud Computing Infrastructure as a Service](http://www.thinkmind.org/download.php?articleid=cloud_computing_2011_7_40_20055)
* [UnaGrid: On Demand Opportunistic Desktop Grid](http://dx.doi.org/10.1109/CCGRID.2010.79)
* [Harvesting Idle CPU Resources for Desktop Grid computing while Limiting the Slowdown Generated to End-users](http://link.springer.com/article/10.1007%2Fs10586-015-0482-4)
* [Supporting e-Science Applications through the On-Demand Execution of Opportunistic and Customizable Virtual Clusters](http://www.naun.org/multimedia/NAUN/computers/17-258.pdf)
* [Running MPI Applications over an Opportunistic Infrastructure](http://link.springer.com/chapter/10.1007/978-3-662-45483-1_8)
* [Desktop Grids and Volunteer Computing Systems](http://www.igi-global.com/chapter/desktop-grids-volunteer-computing-systems/58739)

##License
¿License: [GNU GPL v2](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)?
