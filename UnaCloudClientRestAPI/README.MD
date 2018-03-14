# Current libraries:
+ java 1.8
+ gson-2.8.0.jar
# Components
## UnaCloudConnection 
UnaCloudConnection is currently the only way to connect directly to the REST API developed for UnaCloud. It receives the base URL in the format <IP>:<PORT>/<root> ("http://157.253.236.113:8080/UnaCloud" for instance) and the API Key of the user who is gonna access the service. Besides it possesses methods for comunicating with the REST API through the use of HTTP verbs.
## Managers
Each manager is specialized in some resource and uses UnaCLoudConnection for connecting to the API and doing the required actions for each resource.
## VO
Classes used for mapping between REST requests and responses using gson
## Example (UnaCloudClientRestAPI/src/Example/)
Classes for showcasing complex examples of current operations supported by this library
# Important notes regarding the current functionality
+ It is assumed that for any deployment the user must know the id of the cluster and the id of the nodes (or images)
+ It is assumed that for any operation regarding lab processing (cache cleaning and getting machines for instance) the user knows what is the id of the given lab
# Current functions
+ Get my active deployments (DeploymentManager.getDeployments())
+ Get deployment by id (DeploymentManager.getDeployment(int id))
+ Create deployment with params (DeploymentManager.deployWithParams(DeploymentRequest deploymentRequest))
+ Stop executions of a deployment (DeploymentManager.stopExecutions (DeploymentStopRequest deploymentStopRequest))
+ Gets a given execution by id (DeploymentManager.getExecutionById(int idDeployment, int idExecution))
+ Gets the executions from a deployed image (DeploymentManager.getExecutionsByDeployedImageId(int idDeployment,int imageId))
+ Cleans the cache of given machines inside a lab (LaboratoryManager.cleanCache(LaboratoryUpdateRequest laboratoryUpdateRequest))
+ Gets the machines of a laboratory with the given id (LaboratoryManager.getLaboratoryMachines(int id))
# Code examples (UnaCloudClientRestAPI/src/Example/)
Currently there are two code examples showcasing UnaCloudConnection functionalities:
## SimpleDeployment
+ With a given UnaCloudConnection it creates a deployment with a given time and cluster id. It also adds one image for deploying. After the deployment request is finished and sent to the REST API it returns an id for further identification of the created deployment. The example code can be run in the main of the class.
## DeploymentTimeTesting
### deploymentTimeTesting 
+ As such, this method is complex in the sense that it automatizes the testing of deployment times passing through three steps: Cache cleaning of a number of machines inside a deployment lab, deployment of a given hardware profile (assumed as small) with some params and the stop of all executions used for such deployment.
+ The parameters of such method are the number of iterations that are needed, the given quantities for deploying a single image, a limit of machines for cache cleaning and a significant name for the host in order to make further data recollection through the database.
+ First, it starts cleaning the cache of the number of machines given as a limit in an ascending order (using the id as main ordering criteria)
+ After that it gets stuck in a loop until every single machine is either ON or OFF and not PROCESSING the cache cleaning order.
+ Then, there is a deployment with some given params (mainly the quantity of images of the only node, and the host name)
+ After that it is stuck once again in a loop (with the method finishDeployment) until all executions have either been deployed or rejected.
+ If a given execution has been rejected it will throw an exception at the end of the code warning the programmer that some tests have failed.
+ Finally, there is a request for stopping all the involved executions in the process of deploying the given quantity of a single image.
+ The example code can be run in the main of the class.

### deploymentTimeTestingWithPostCacheCleaning
+ As such, this method is complex in the sense that it automatizes the testing of deployment times passing through three steps: Deployment of a given hardware profile (assumed as small) with some params, the stop of all executions used for such deployment and finally the cache cleaning of a number of machines inside a deployment lab.
+ The parameters of such method are the number of iterations that are needed, the given quantities for deploying a single image and a significant name for the host in order to make further data recollection through the database.
+ First, there is a deployment with some given params (mainly the quantity of images of the only node, and the host name)
+ After that it is stuck once again in a loop (with the method finishDeployment) until all executions have either been deployed or rejected.
+ If a given execution has been rejected it will throw an exception at the end of the code warning the programmer that some tests have failed.
+ Then, there is a request for stopping all the involved executions in the process of deploying the given quantity of a single image.
+ Finally, it starts cleaning the cache of the number of machines given as a limit in an ascending order (using the id as main ordering criteria)
+ After that it gets stuck in a loop until every single machine is either ON or OFF and not PROCESSING the cache cleaning order.
+ The example code can be run in the main of the class.
