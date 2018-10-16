package Example;

import Connection.*;
import VO.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for deployment time testing examples with different methods.
 * @author s.guzmanm
 */
public class DeploymentTimeTesting {
    //Given UnaCloudConnection
    private UnaCloudConnection uc;

    private static SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");

    //------------
    //Constructor and getter and setters
    //------------
    public DeploymentTimeTesting(UnaCloudConnection uc) {
        this.uc = uc;
    }

    public UnaCloudConnection getUc() {
        return uc;
    }

    public void setUc(UnaCloudConnection uc) {
        this.uc = uc;
    }

    /**
     * Complex example for testing the deployment time of several machines with the number of iterations, the array of quantity of machines and the max number of cleanable machines given by param.
     * It consists in cleaning cache, later deploying and finally stopping the executions in the given lab.
     * @param iterations Maximum number of iterations
     * @param quantities Quantities for iteration deployments
     * @param maxCleanableMachines Determines the maximum number of machines for cleaning the cache
     * @param significantHostName Defines the host name used for this set of tests
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public void deploymentTimeTesting(int iterations, int idCluster,int imageId, int[] quantities, int maxCleanableMachines, String significantHostName) throws Exception {
        Date limite=format.parse("2018/05/12 06:00:00");
        for (int j = 0; j < iterations; j++) {
            for (Integer qty : quantities) {
                //Clean the cache of the given machines or of given numbers.
                System.out.println("Cache");
                //The user must know the id of the lab to clean up and get machines from
                LaboratoryManager lab = new LaboratoryManager(uc);
                //Get lab physical machines for cleaning cache
                List<PhysicalMachineResponse> list = lab.getLaboratoryMachines(1);
                //Sort by id
                Collections.sort(list);
                //Clean cache of given machines until they reach a top desired by the user
                LaboratoryUpdateRequest laboratoryUpdateRequest = new LaboratoryUpdateRequest(1, TaskManagerState.CACHE);
                int i = 0;
                for (PhysicalMachineResponse phy : list) {
                    System.out.println("MACHINE " + phy.getId() + " " + phy.getName() + " " + phy.getIp().getId());
                    laboratoryUpdateRequest.addMachine(phy.getId());
                    i++;
                    if (i >= maxCleanableMachines)
                        break;
                }
                list = lab.getLaboratoryMachines(2);
                lab.cleanCache(laboratoryUpdateRequest);
                laboratoryUpdateRequest = new LaboratoryUpdateRequest(2, TaskManagerState.CACHE);
                i = 0;
                for (PhysicalMachineResponse phy : list) {
                    System.out.println("MACHINE " + phy.getId() + " " + phy.getName() + " " + phy.getIp().getId());
                    laboratoryUpdateRequest.addMachine(phy.getId());
                    i++;
                    if (i >= maxCleanableMachines)
                        break;
                }
                lab.cleanCache(laboratoryUpdateRequest);
                //Check that every machine has finished cleaning cache before deployment
                boolean terminaCache = false;
                long time=System.currentTimeMillis();
                while (!terminaCache) {
                    Thread.sleep(10000);
                    list = lab.getLaboratoryMachines(1);
                    list.addAll(lab.getLaboratoryMachines(2));
                    Collections.sort(list);
                    terminaCache = true;
                    i = 0;
                    for (PhysicalMachineResponse phy : list) {
                        System.out.println("MACHINE " + phy.getId() + " " + phy.getName() + " " + phy.getIp().getId());
                        if (phy.getState().getName().equals(LaboratoryManager.MACHINE_STATE.PROCESSING + "")) {
                            terminaCache = false;
                            break;
                        }
                        i++;
                        if (i >= maxCleanableMachines)
                            break;
                    }
                    if(System.currentTimeMillis()-time>=10*60000)
                        throw new Exception("It is stuck in PROCESSING bug "+significantHostName + ";" + (j + 1) + "_" + qty + ";");

                }
                DeploymentManager dep = new DeploymentManager(uc);
                //Post deployment with params
                DeploymentRequest deploymentRequest = new DeploymentRequest(1, idCluster);
                deploymentRequest.addNode(imageId, DeploymentManager.HW_SMALL, qty, significantHostName + ";" + (j + 1) + "_" + qty + ";", false);
                double deploymentId = dep.deployWithParams(deploymentRequest);
                System.out.println("ID DEPLOY" + deploymentId);

                //Get the current deployment
                DeploymentResponse deploy = dep.getDeployment((int) deploymentId);
                System.out.println(deploy.getStatus().getName() + "");
                //Get given deployment executions
                System.out.println("Get executions");
                //Signals if you need to throw an exception or not if there are executions that failed during the process
                boolean noErrors = finishExecutions(deploy, dep);
                System.out.println("Stop");
                DeploymentStopRequest deploymentStopRequest = new DeploymentStopRequest();
                //Cycle through all executions of the deployment and add them to the list for stopping
                for (ObjectId<Integer> id : deploy.getImages()) {
                    for (ExecutionResponse exec : dep.getExecutionsByDeployedImageId((int) deploymentId, id.getId())) {
                        System.out.println("ADD " + exec.getId() + " " + exec.getExecutionNode() + " " + exec.getState().getId());
                        deploymentStopRequest.addExecution(exec.getId());
                    }
                }
                //Stop executions
                dep.stopExecutions(deploymentStopRequest);
                //Throws exception if there is a failed instance of deployment
                //While the deployment is not done loop
                finishDeployment(deploy, dep);
                System.out.println(System.currentTimeMillis()+" "+significantHostName + ";" + (j + 1) + "_" + qty + ";");
                Thread.sleep(60000*5);
                if(new Date().compareTo(limite)>0)
                    throw new Exception(significantHostName + ";" + (j + 1) + "_" + qty + ";");
            }
        }
    }


    /**
     * Complex example for testing the deployment time of several machines with the number of iterations, the array of quantity of machines and the max number of cleanable machines given by param.
     * It consists in cleaning cache, later deploying and finally stopping the executions in the given lab.
     * @param iterations Maximum number of iterations
     * @param quantities Quantities for iteration deployments
     * @param maxCleanableMachines Determines the maximum number of machines for cleaning the cache
     * @param significantHostName Defines the host name used for this set of tests
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public void test(int iterations, int[] quantities, int maxCleanableMachines, String significantHostName) throws Exception
    {
        for(int j=0;j<iterations;j++)
        {
            for(Integer qty:quantities)
            {
                System.out.println("Las máquinas se empezaron a procesar en "+System.currentTimeMillis()+" "+significantHostName+";"+(j+1)+"_"+qty );
                //Clean the cache of the given machines or of given numbers.
                System.out.println("Cache");
                //The user must know the id of the lab to clean up and get machines from
                LaboratoryManager lab=new LaboratoryManager(uc);
                //Get lab physical machines for cleaning cache
                List<PhysicalMachineResponse> list=lab.getLaboratoryMachines(1);
                //Sort by id
                Collections.sort(list);
                //Clean cache of given machines until they reach a top desired by the user
                LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(1, TaskManagerState.CACHE);
                int i=0;
                for(PhysicalMachineResponse phy:list)
                {
                    laboratoryUpdateRequest.addMachine(phy.getId());
                    i++;
                    if(i>=maxCleanableMachines)
                        break;
                }
                lab.cleanCache(laboratoryUpdateRequest);
                //Check that every machine has finished cleaning cache before deployment
                boolean terminaCache=false;
                double time=System.currentTimeMillis();
                while(!terminaCache)
                {
                    Thread.sleep(10000);
                    list=lab.getLaboratoryMachines(1);
                    Collections.sort(list);
                    terminaCache=true;
                    i=0;
                    for(PhysicalMachineResponse phy:list)
                    {
                        if(phy.getState().getName().equals(LaboratoryManager.MACHINE_STATE.PROCESSING+""))
                        {
                            terminaCache=false;
                            break;
                        }
                        i++;
                        if(i>=maxCleanableMachines)
                            break;
                    }
                    if(System.currentTimeMillis()-time>60000*5)
                        throw new Exception("Las máquinas se quedaron procesando iniciando en "+System.currentTimeMillis()+" "+significantHostName+";"+(j+1)+"_"+qty );
                }
                DeploymentManager dep= new DeploymentManager(uc);
                //Post deployment with params
                DeploymentRequest deploymentRequest=new DeploymentRequest(3600000,61);
                deploymentRequest.addNode(74,DeploymentManager.HW_SMALL,qty,significantHostName+";"+(j+1)+"_"+qty+";",false);
                double deploymentId=dep.deployWithParams(deploymentRequest);

                //Get the current deployment
                DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
                //Get given deployment executions
                //Signals if you need to throw an exception or not if there are executions that failed during the process
                boolean noErrors= finishExecutions(deploy,dep);
                DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
                //Cycle through all executions of the deployment and add them to the list for stopping
                for(ObjectId<Integer> id:deploy.getImages())
                {
                    for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                    {
                        deploymentStopRequest.addExecution(exec.getId());
                    }
                }
                //Stop executions
                dep.stopExecutions(deploymentStopRequest);
                //While the deployment is not done loop
                finishDeployment(deploy,dep);
                System.out.println("Las máquinas se terminaron de procesar en "+time+" "+significantHostName+";"+(j+1)+"_"+qty );
            }
        }

    }


    /**
     * Complex example for testing the deployment time of several machines with the number of iterations, and the array of quantity of machines given by param.
     * Since the cache cleaning is done at the end, it consists in deploying, stopping the executions in the given lab and finally cleaning the cache of the used machines.
     * @param iterations Maximum number of iterations
     * @param quantities Quantities for iteration deployments
     * @param significantHostName Defines the host name used for this set of tests
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public void deploymentTimeTestingWithPostCacheCleaning(int iterations, int[] quantities, String significantHostName) throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        for(int j=0;j<iterations;j++)
        {
            for(Integer qty:quantities)
            {
                //Post deployment with params
                DeploymentRequest deploymentRequest=new DeploymentRequest(1,61);
                deploymentRequest.addNode(74,DeploymentManager.HW_SMALL,qty,significantHostName+";"+(j+1)+"_"+qty+";",false);
                double deploymentId=dep.deployWithParams(deploymentRequest);
                System.out.println("ID DEPLOY"+deploymentId);

                //Get the current deployment
                DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
                System.out.println(deploy.getStatus().getName()+"");

                //Assume we have executions
                System.out.println("Get executions");
                //Signals if you need to throw an exception or not
                boolean noErrors= finishExecutions(deploy,dep);
                ArrayList<Integer> machines=new ArrayList<>();
                System.out.println("Stop");
                DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
                //Cycle through all executions of the deployment and add them to the list for stopping
                for(ObjectId<Integer> id:deploy.getImages())
                {
                    for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                    {
                        System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                        deploymentStopRequest.addExecution(exec.getId());
                        machines.add(exec.getExecutionNode().getId());
                    }
                }

                //Stop executions
                dep.stopExecutions(deploymentStopRequest);

                //Throws exception if there is a failed instance of deployment
                if(!noErrors)
                    throw new Exception("There are failed deployment instances");

                //Clean the cache of the given machines or of given numbers.
                System.out.println("Cache");

                //The user must know the id of the lab to clean up
                LaboratoryManager lab=new LaboratoryManager(uc);
                LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(2, TaskManagerState.CACHE);
                for(Integer i:machines)
                {
                    System.out.println("MACHINE "+i);
                    laboratoryUpdateRequest.addMachine(i);
                }
                lab.cleanCache(laboratoryUpdateRequest);
                //While the deployment is not done loop
                finishDeployment(deploy,dep);


            }
        }


    }

    /**
     * Complex example for testing the deployment time of several machines with the number of iterations, and the array of quantity of machines given by param.
     * Since the cache cleaning is done at the end, it consists in deploying, stopping the executions in the given lab and finally cleaning the cache of the used machines.
     * @param iterations Maximum number of iterations
     * @param quantities Quantities for iteration deployments
     * @param significantHostName Defines the host name used for this set of tests
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public void energyTestingPerAlgorithm(int iterations, int[] quantities, int time, int idCluster, Map<Integer,HardwareProfileNodeList> quantitiesPerNode, String significantHostName, int[] labIds, String fileName) throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        PrintWriter pw=new PrintWriter(new File(fileName));
        HashMap<Integer,Boolean> machineWithUser=new HashMap<>();
        for(int j=0;j<iterations;j++)
        {
            for(Integer qty:quantities)
            {
                LaboratoryManager lab=new LaboratoryManager(uc);
                //Get lab physical machines for cleaning cache
                for(Integer labId:labIds)
                {
                    List<PhysicalMachineResponse> list=lab.getLaboratoryMachines(labId);
                    //Hash to get if the machine had an user
                    for(PhysicalMachineResponse phy:list)
                    {
                        machineWithUser.put(phy.getId(),phy.isWithUser());
                    }
                }
                //Post deployment with params
                DeploymentRequest deploymentRequest=new DeploymentRequest(time,idCluster);
                for(Integer i:quantitiesPerNode.get(qty).getProfiles().keySet())
                {
                    if(notHardwareProfile(i)) throw new Exception("The given hardware profile does not exist in the system");
                    int quantity=Integer.parseInt(quantitiesPerNode.get(qty).getProfiles().get(i).get("quantity"));
                    if(quantity>0)
                        deploymentRequest.addNode(Integer.parseInt(quantitiesPerNode.get(qty).getProfiles().get(i).get("image")),i,quantity,quantitiesPerNode.get(qty).getProfiles().get(i).get("hostname")+":"+(j+1)+"_"+quantity+";",false);
                }


                double deploymentId=dep.deployWithParams(deploymentRequest);
                System.out.println("ID DEPLOY"+deploymentId);
                //Get the current deployment
                DeploymentResponse deploy=dep.getDeployment((int)deploymentId);
                System.out.println(deploy.getStatus().getName()+"");
                //Identify given machines

                HashMap<Integer,List<Integer>> vmsInPms=new HashMap<>();
                for(ObjectId<Integer> id:deploy.getImages())
                {
                    for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                    {
                        if(vmsInPms.get(id.getId())==null) vmsInPms.put(id.getId(),new ArrayList<>());
                        vmsInPms.get(id.getId()).add(exec.getExecutionNode().getId());
                    }
                }

                //Assume we have executions
                System.out.println("Get executions");
                //Signals if you need to throw an exception or not
                boolean noErrors= finishExecutions(deploy,dep);
                DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
                //Cycle through all executions of the deployment and add them to the list for stopping
                for(ObjectId<Integer> id:deploy.getImages())
                {
                    for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                    {
                        System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                        deploymentStopRequest.addExecution(exec.getId());
                    }
                }
                if(!noErrors)
                    throw new Exception("The system has failed executions");
                System.out.println("The system is waiting 15 minutes to stop executions");
                Thread.sleep(15*60000);
                System.out.println("Stop");
                //Stop executions
                dep.stopExecutions(deploymentStopRequest);
                pw.println(significantHostName+";"+(j+1)+"_"+qty+";");
                pw.println("ID IMAGEN DESPLEGADA,ID MÁQUINA FÍSICA");
                for(Integer i:vmsInPms.keySet())
                {
                    for(Integer exe:vmsInPms.get(i))
                    {
                        pw.println(i+","+exe);
                    }
                }
                pw.println("ID MÁQUINA FÍSICA,USUARIO");
                int usadas=0;
                for(Integer i:machineWithUser.keySet())
                {
                    if(vmsInPms.values().contains(i))
                    {
                        pw.println(i+","+((machineWithUser.get(i))?"1":"0"));
                        usadas++;
                    }

                }
                pw.println("MÁQUINA FÍSICA USADA");
                pw.println(usadas);
            }
        }
        pw.close();


    }

    /**
     * Example for testing the deployment of 10 machines at the same time for the given user. The type of lab dependends on the string given by param since it is restricted to the user access on the web interface.
     * @param labname Name of the lab to be used
     * @param qty Number of machines to be deployed
     * @oaram labId Id of the given lab
     * @param significantHostName Defines the host name used for this set of tests
     * @param path Path for the file to be written on
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public void netLabDeploymentTesting(String labname,int labId,int clusterId,int qty, String significantHostName, String path) throws Exception {
        DeploymentManager dep = new DeploymentManager(uc);
        int iterations = 10;
        long[] time=new long[iterations];
        HashMap<Integer,String>[] failures=new HashMap[iterations];
        for(int i=0;i<failures.length;i++)
        {
            failures[i]=new HashMap<>();
        }
        boolean cacheCleaned = false;
        PrintWriter pw = new PrintWriter(new File(path));
        pw.println("Deployment testing with "+qty+" machines per iteration for lab " + labname);
        pw.println("Iteration,Successful,Failed, Time (s), Time (m)");
        for (int j = 0; j < iterations; j++) {

            time[j]=System.currentTimeMillis();

            //Post deployment with params
            DeploymentRequest deploymentRequest = new DeploymentRequest(1, clusterId);
            deploymentRequest.addNode(74, DeploymentManager.HW_SMALL, qty, significantHostName + ";" + (j + 1) + ";", false);
            double deploymentId = dep.deployWithParams(deploymentRequest);
            System.out.println("ID DEPLOY" + deploymentId);

            //Get the current deployment
            DeploymentResponse deploy = dep.getDeployment((int) deploymentId);
            System.out.println(deploy.getStatus().getName() + "");

            //Assume we have executions
            System.out.println("Get executions");
            //Signals if you need to throw an exception or not
            boolean todoEstaDetenido = false;
            int state = 0;
            int success = 0;
            int failed = 0;
            while (!todoEstaDetenido) {
                success = 0;
                failed = 0;
                Thread.sleep(60000);
                todoEstaDetenido = true;
                for (ObjectId<Integer> id : deploy.getImages()) {
                    for (ExecutionResponse exec : dep.getExecutionsByDeployedImageId((int) deploymentId, id.getId())) {
                        state = exec.getState().getId();
                        System.out.println("EXEC " + exec.getId() + " " + exec.getExecutionNode().getId() + " " + exec.getState().getId());
                        if (state != DeploymentManager.DEPLOYED && state != DeploymentManager.FAILED) {
                            todoEstaDetenido = false;
                            break;
                        }
                        //Get the count of successful deployments versus failed ones
                        if (state == DeploymentManager.DEPLOYED)
                            success++;
                        else if (state == DeploymentManager.FAILED)
                        {
                            failed++;
                            failures[j].put(exec.getExecutionNode().getId(),exec.getMessage()+","+exec.getLastReport());
                        }
                    }
                }
            }
            time[j]=System.currentTimeMillis()-time[j];

            ArrayList<Integer> machines=new ArrayList<>();
            System.out.println("Stop");
            //Cycle through all executions of the deployment and add them to the list for stopping and if necessary, cleaning cache
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                {
                    System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                    machines.add(exec.getExecutionNode().getId());
                }
            }

            System.out.println("Stop");
                DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
                //Cycle through all executions of the deployment and add them to the list for stopping
                for(ObjectId<Integer> id:deploy.getImages())
                {
                    for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                    {
                        System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                        deploymentStopRequest.addExecution(exec.getId());
                        machines.add(exec.getExecutionNode().getId());
                    }
                }
                //Stop executions
                dep.stopExecutions(deploymentStopRequest);
                //While the deployment is not done loop
                finishDeployment(deploy,dep);
                Thread.sleep(20000);
                if(failed>=0.4*(success+failed) && !cacheCleaned)
                {
                    if (!cacheCleaned)
                    {
                        LaboratoryManager lab = new LaboratoryManager(uc);
                        //Clean the cache of the given machines or of given numbers.
                        System.out.println("Cache");
                        //The user must know the id of the lab to clean up
                        LaboratoryUpdateRequest laboratoryUpdateRequest=new LaboratoryUpdateRequest(labId, TaskManagerState.CACHE);
                        for(Integer i:machines)
                        {
                            System.out.println("MACHINE "+i);
                            laboratoryUpdateRequest.addMachine(i);
                        }
                        lab.cleanCache(laboratoryUpdateRequest);
                        Thread.sleep(1000*60*5);
                        cacheCleaned=true;
                        j--;
                        continue;
                    }
                    else
                        throw new Exception("There are "+failed+" instances on the deployment "+j);
                }
                else
                    cacheCleaned=false;
                pw.println((j + 1) + "," + success + "," + failed+","+time[j]/1000+","+time[j]/1000/60);

        }
        double avg=0;
        for(Long j:time)
            avg+=j;
        avg/=iterations;
        pw.println("Average deployment time (s)"+avg/1000);
        pw.println("Average deployment time (m) "+avg/1000/60);
        pw.println();
        pw.println("Iteration,Failed machines,Message, Last report");
        for(int i=0;i<failures.length;i++)
        {
            for(Integer s:failures[i].keySet())
            {
                pw.println((i+1)+","+s+","+failures[i].get(s));
            }
        }
        pw.close();

    }

    /**
     * Example for testing the deployment of 10 machines at the same time for the given user. The type of lab dependends on the string given by param since it is restricted to the user access on the web interface.
     * @param labname Name of the lab to be used
     * @param qty Number of machines to be deployed
     * @oaram labId Id of the given lab
     * @param significantHostName Defines the host name used for this set of tests
     * @throws Exception If there is any HTTPException or if there are any failed deployments.
     */
    public HashMap<Integer,String>[] midwayShutDownTesting(String labname,int labId,int clusterId, int[] nodes, int iterations, int qty, String significantHostName, int numberHours) throws Exception {
        DeploymentManager dep = new DeploymentManager(uc);
        HashMap<Integer, String>[] failures = new HashMap[iterations];
        for (int i = 0; i < failures.length; i++) {
            failures[i] = new HashMap<>();
        }
        for (int j = 0; j < iterations; j++) {
            //Post deployment with params
            DeploymentRequest deploymentRequest = new DeploymentRequest(numberHours, clusterId);
            for (int i=0;i< nodes.length;i++)
            {
                deploymentRequest.addNode(nodes[i], DeploymentManager.HW_SMALL, qty, significantHostName + ";" + (j + 1) + ";", false);
                System.out.println("Node "+nodes[i]);
            }
            double deploymentId = dep.deployWithParams(deploymentRequest);
            System.out.println("ID DEPLOY" + deploymentId);

            //Get the current deployment
            DeploymentResponse deploy = dep.getDeployment((int) deploymentId);
            System.out.println(deploy.getStatus().getName() + "");

            //Assume we have executions
            System.out.println("Get executions");
            //Signals if you need to throw an exception or not
            boolean todoEstaDetenido = false;

            int state = 0;
            int success = 0;
            int failed = 0;
            long tiempoTotal = numberHours * 60000 * 60;
            ArrayList<Integer> list = new ArrayList<>();
            while (!todoEstaDetenido) {
                success = 0;
                failed = 0;
                Thread.sleep(60000);
                todoEstaDetenido = true;
                list = new ArrayList<>();
                for (ObjectId<Integer> id : deploy.getImages()) {
                    for (ExecutionResponse exec : dep.getExecutionsByDeployedImageId((int) deploymentId, id.getId())) {
                        state = exec.getState().getId();
                        System.out.println("EXEC " + exec.getId() + " " + exec.getExecutionNode().getId() + " " + exec.getState().getId());
                        if (state != DeploymentManager.DEPLOYED && state != DeploymentManager.FAILED) {
                            todoEstaDetenido = false;
                            break;
                        }
                        //Get the count of successful deployments versus failed ones
                        if (state == DeploymentManager.DEPLOYED)
                            success++;
                        else if (state == DeploymentManager.FAILED) {
                            failed++;
                        }
                        list.add(exec.getExecutionNode().getId());
                    }
                }
            }
            boolean[] inscritos;
            while (tiempoTotal > 0) {
                inscritos = new boolean[list.size()];
                failed = 0;
                Thread.sleep(60000);
                tiempoTotal -= 60000;
                for (ObjectId<Integer> id : deploy.getImages()) {
                    for (ExecutionResponse exec : dep.getExecutionsByDeployedImageId((int) deploymentId, id.getId())) {
                        state = exec.getState().getId();
                        System.out.println("TIME " + exec.getId() + " " + exec.getExecutionNode().getId() + " " + exec.getState().getId());
                        //Get the count of successful deployments versus failed ones
                        if (state == DeploymentManager.FAILED) {
                            failed++;
                            failures[j].put(exec.getExecutionNode().getId(), exec.getMessage() + "," + exec.getLastReport());
                        }
                        inscritos[list.indexOf(exec.getExecutionNode().getId())] = true;
                    }
                }
            }

            ArrayList<Integer> machines = new ArrayList<>();
            System.out.println("Stop");
            DeploymentStopRequest deploymentStopRequest=new DeploymentStopRequest();
            //Cycle through all executions of the deployment and add them to the list for stopping
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId((int)deploymentId,id.getId()))
                {
                    System.out.println("ADD "+exec.getId()+" "+exec.getExecutionNode()+" "+exec.getState().getId());
                    deploymentStopRequest.addExecution(exec.getId());
                    machines.add(exec.getExecutionNode().getId());
                }
            }
            //Stop executions
            dep.stopExecutions(deploymentStopRequest);

            //While the deployment is not done loop
            finishDeployment(deploy, dep);
            Thread.sleep(20000);
            System.out.println("Cache");
            //The user must know the id of the lab to clean up
            LaboratoryUpdateRequest laboratoryUpdateRequest = new LaboratoryUpdateRequest(labId, TaskManagerState.CACHE);
            for (Integer i : machines) {
                System.out.println("MACHINE " + i);
                laboratoryUpdateRequest.addMachine(i);
            }
            System.out.println("Cache");
            LaboratoryManager lab = new LaboratoryManager(uc);
            lab.cleanCache(laboratoryUpdateRequest);
            Thread.sleep(1000 * 60 * 5);


        }
        return failures;

    }

    /**
     * Method for checking whether number represents a hardware profile id or not
     * @param i Id of hardware profile
     * @return Whether it is a hardware profile or not
     */
    private boolean notHardwareProfile(Integer i) {
        if(i==DeploymentManager.HW_LARGE || i==DeploymentManager.HW_MEDIUM||i==DeploymentManager.HW_SMALL||i==DeploymentManager.HW_XLARGE)
            return false;
        return true;
    }

    /**
     * Method for looping until the executions are finished. It returns whether there was a successful deployment or not.
     * @param deploy The deployment response for looking at the executions
     * @param dep Deployment manager for the given execution
     * @return Boolean that determines whether the deployment was finished successfully (true) or it had errors during launch (false)
     */
    public boolean finishExecutions(DeploymentResponse deploy, DeploymentManager dep) throws Exception
    {
        int deploymentId=deploy.getId();
        boolean todoEstaDetenido=false;
        int state=0;
        int times=10;
        while(!todoEstaDetenido && times>0)
        {
            Thread.sleep(60000);
            todoEstaDetenido=true;
            for(ObjectId<Integer> id:deploy.getImages())
            {
                for(ExecutionResponse exec:dep.getExecutionsByDeployedImageId(deploymentId,id.getId()))
                {
                    state=exec.getState().getId();
                    System.out.println("EXEC "+exec.getId()+" "+exec.getExecutionNode().getId()+" "+exec.getState().getId());
                    if(state!=DeploymentManager.DEPLOYED && state!=DeploymentManager.FAILED)
                    {
                        todoEstaDetenido=false;
                        times--;
                        break;
                    }
                    //if(state== DeploymentManager.FAILED)
                    //  return false;
                }
            }
        }
        return true;
    }
    /**
     * Method for looping until the deployment is officially inactive (No execution running).
     * @param deploy The deployment response for looking at the executions
     * @param dep Deployment manager for the given execution
     */
    public void finishDeployment(DeploymentResponse deploy, DeploymentManager dep) throws Exception
    {
        boolean finished=false;
        while(!finished)
        {
            finished=true;
            for(ObjectId<Integer> image:deploy.getImages())
            {
                if(!dep.getExecutionsByDeployedImageId(deploy.getId(),image.getId()).isEmpty())
                {
                    finished=false;
                    break;
                }
            }
        }
    }

    /**
     * Test for main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        UnaCloudConnection uc = new UnaCloudConnection("5ZVAZEP0Q7RQRYK2LXYON05T7LUA9GOI","http://157.253.236.113:8080/UnaCloud");
        DeploymentTimeTesting deploymentTimeTesting=new DeploymentTimeTesting(uc);
        HashMap<Integer, String>[] failures=deploymentTimeTesting.midwayShutDownTesting("Waira 1",1,61,new int[]{74},5,25,"pruebasApagado",1);

        for(int i=0;i<failures.length;i++)
        {
            System.out.println("Iteración "+(i+1));
            for(Integer j:failures[i].keySet())
            {
                System.out.println("Node "+j+" has failure "+failures[i].get(j));

            }
        }

/*
        //First method for making deployment time testing
        deploymentTimeTesting.deploymentTimeTesting(1,61,74,new int[]{15,25,50},100,"P2PTestMaySmall");
        deploymentTimeTesting.deploymentTimeTesting(1,62,75,new int[]{15,25,50},100,"P2PTestMayMedium");
        deploymentTimeTesting.deploymentTimeTesting(1,65,76,new int[]{15,25,50},100,"P2PTestMayLarge");




        Map<Integer,HardwareProfileNodeList> quantitiesPerNode=new HashMap<>();
        Map<Integer,Map<String,String>> node=new HashMap<>();
        Map<String,String> interno=new HashMap<>();
        List<String> hardwareProfiles = Arrays.asList(new String[]{"small","medium","large","xlarge"});
        //Cesar algorithm
        BufferedReader br=new BufferedReader(new FileReader("./archivoEntrada.csv"));
        String linea=br.readLine();
        linea=br.readLine();
        int clusterId=Integer.parseInt(linea.split(",")[0]);
        String [] datos=null;
        int categoria=0;
        int temp=0;
        while(linea!=null)
        {
            datos=linea.split(",");
            temp=Integer.parseInt(datos[1]);
            if(temp!=categoria)
            {
                categoria=temp;
                node=new HashMap<>();
                quantitiesPerNode.put(categoria,new HardwareProfileNodeList(node));
            }
            for(int i=0;i<4;i++)
            {
                int hw=hardwareProfiles.indexOf(datos[4+4*i]);
                interno=new HashMap<>();
                interno.put("quantity",(datos[hw*4+3]));
                interno.put("image",(datos[hw*4+2]));
                interno.put("hostname",datos[hw*4+5]);
                quantitiesPerNode.get(categoria).getProfiles().put(hw+1,interno);
            }
            linea=br.readLine();
        }
        br.close();
        for(Integer i:quantitiesPerNode.keySet())
        {
            System.out.println(i);
            for(Integer j:quantitiesPerNode.get(i).getProfiles().keySet())
            {
                System.out.println("HW"+j);
                for(String s:quantitiesPerNode.get(i).getProfiles().get(j).keySet())
                {
                    System.out.println("Key "+s+" "+quantitiesPerNode.get(i).getProfiles().get(j).get(s));
                }
            }
        }
        //deploymentTimeTesting.energyTestingPerAlgorithm(1,new int[]{40,50},1,clusterId,quantitiesPerNode,"test",1,"test.csv");

        //Second method for making deployment time testing with post-cache processing UNCOMENT NEXT LINE TÇO USE
        //deploymentTimeTesting.deploymentTimeTestingWithPostCacheCleaning(1,new int[]{1},"UnaCloudConnectionTest");*/

    }



}