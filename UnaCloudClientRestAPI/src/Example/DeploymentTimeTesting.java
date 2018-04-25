package Example;

import Connection.*;
import VO.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class for deployment time testing examples with different methods.
 * @author s.guzmanm
 */
public class DeploymentTimeTesting {
    //Given UnaCloudConnection
    private UnaCloudConnection uc;
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
    public void deploymentTimeTesting(int iterations, int[] quantities, int maxCleanableMachines, String significantHostName) throws Exception {
        for (int j = 1; j < 2; j++) {
            for (Integer qty : quantities) {
                //Clean the cache of the given machines or of given numbers.
                System.out.println("Cache");
                //The user must know the id of the lab to clean up and get machines from
                LaboratoryManager lab = new LaboratoryManager(uc);
                //Get lab physical machines for cleaning cache
                List<PhysicalMachineResponse> list = lab.getLaboratoryMachines(2);
                //Sort by id
                Collections.sort(list);
                //Clean cache of given machines until they reach a top desired by the user
                LaboratoryUpdateRequest laboratoryUpdateRequest = new LaboratoryUpdateRequest(2, TaskManagerState.CACHE);
                int i = 0;
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
                while (!terminaCache) {
                    Thread.sleep(60000);
                    list = lab.getLaboratoryMachines(2);
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
                }
                DeploymentManager dep = new DeploymentManager(uc);
                //Post deployment with params
                DeploymentRequest deploymentRequest = new DeploymentRequest(1, 61);
                deploymentRequest.addNode(74, DeploymentManager.HW_SMALL, qty, significantHostName + ";" + (j + 1) + "_" + qty + ";", false);
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
                if (!noErrors)
                    throw new Exception("There are failed deployment instances "+significantHostName + ";" + (j + 1) + "_" + qty + "; "+System.currentTimeMillis());
                //While the deployment is not done loop
                finishDeployment(deploy, dep);
                Thread.sleep(60000*5);
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
                        System.out.println("MACHINE "+phy.getId()+" "+phy.getName()+" "+phy.getIp().getId());
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
                        if(System.currentTimeMillis()-time>120000)
                            throw new Exception("Las máquinas se quedaron procesando iniciando en "+time+" "+significantHostName+";"+(j+1)+"_"+qty );
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
    public void energyTestingPerAlgorithm(int iterations, int[] quantities,int time, int idCluster,Map<Integer,HardwareProfileNodeList> quantitiesPerNode,String significantHostName,int labId,String fileName) throws Exception
    {
        DeploymentManager dep= new DeploymentManager(uc);
        PrintWriter pw=new PrintWriter(new File(fileName));

        for(int j=0;j<iterations;j++)
        {
            for(Integer qty:quantities)
            {
                LaboratoryManager lab=new LaboratoryManager(uc);
                //Get lab physical machines for cleaning cache
                List<PhysicalMachineResponse> list=lab.getLaboratoryMachines(labId);
                //Hash to get if the machine had an user
                HashMap<Integer,Boolean> machineWithUser=new HashMap<>();
                for(PhysicalMachineResponse phy:list)
                {
                    machineWithUser.put(phy.getId(),phy.isWithUser());
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
        while(!todoEstaDetenido)
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
                        break;
                    }
                    if(state== DeploymentManager.FAILED)
                        return false;
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
                System.out.println("CURRENT DEP STATUS WITH IMAGE "+image.getId());
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
        //First method for making deployment time testing
       // deploymentTimeTesting.deploymentTimeTesting(5,new int[]{3,4,5,6,7,8,9,10},50,"P2PSmallTestWaira2SeriesApril");


        Map<Integer,HardwareProfileNodeList> quantitiesPerNode=new HashMap<>();
        Map<Integer,Map<String,String>> node=new HashMap<>();
        Map<String,String> interno=new HashMap<>();
        List<String> hardwareProfiles = Arrays.asList(new String[]{"small","medium","large","xlarge"});

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
        //deploymentTimeTesting.deploymentTimeTestingWithPostCacheCleaning(1,new int[]{1},"UnaCloudConnectionTest");

    }



}
