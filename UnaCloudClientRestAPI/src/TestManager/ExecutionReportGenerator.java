package TestManager;

import Connection.DeploymentManager;
import Connection.ReportManager;
import Connection.UnaCloudConnection;
import Example.DeploymentTimeTesting;
import VO.DeploymentExecutionResponse;
import VO.ExecutionStateResponse;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExecutionReportGenerator {

    private static final int T_INICIAL=11;

    private static final int T_TRANS=10;

    private static final int T_FINAL=5;

    //Given UnaCloudConnection
    private UnaCloudConnection uc;
    //------------
    //Constructor and getter and setters
    //------------
    public ExecutionReportGenerator(UnaCloudConnection uc) {
        this.uc = uc;
    }

    public UnaCloudConnection getUc() {
        return uc;
    }

    public void setUc(UnaCloudConnection uc) {
        this.uc = uc;
    }

    public void generateExecutionReportForEnergyTesting(String fileName,String hostname, String iterationRegex, String machineRegex, int numIterations, int[]quantities) throws Exception
    {
        DeploymentManager deploymentmanager=new DeploymentManager(uc);
        ReportManager reportManager=new ReportManager(uc);
        PrintWriter pw=new PrintWriter(new File(fileName));
        SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        double fallidos=0;
        int numMaquina=0;
        for(Integer j:quantities)
        {
            numMaquina=1;
            fallidos=0;
            pw.println("Despliegues: "+j);
            pw.println(" , ");
            pw.println("Prueba,Numero,Name,T-Inicial,T-Final,T-Desplegado(s)");
            for(int i=0;i<numIterations;i++)
            {
                try
                {
                    System.out.println("REGEX "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    DeploymentExecutionResponse response=reportManager.getRelation(hostname+iterationRegex+(i+1)+machineRegex+j+";");
                    for(Integer executionId:response.getExecutions())
                    {
                        List<ExecutionStateResponse> list=deploymentmanager.getStates(response.getDeploymentId(),executionId);
                        String ini="";
                        String fin="";
                        for(ExecutionStateResponse executionStateResponse:list)
                        {
                            switch (executionStateResponse.getState().getId())
                            {
                                case(T_INICIAL):ini=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                case(T_FINAL):fin=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                            }
                        }
                        System.out.println(sf.parse(ini).getTime());
                        String tiempoFin="";
                        try {
                            double tFin = sf.parse(fin).getTime() - sf.parse(ini).getTime();
                            tiempoFin = tFin + "";
                        }
                        catch(Exception e)
                        {
                            fallidos++;
                        }
                        pw.println((i+1)+","+numMaquina+","+hostname+iterationRegex+(i+1)+machineRegex+j+","+ini+","+fin+","+tiempoFin);

                    }
                }
                catch(Exception e)
                {
                    System.out.println("There was an error during the HTTP Procedure in hostname "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    e.printStackTrace();
                }
                pw.println("");
                pw.println("FALLIDOS,"+fallidos);
                pw.println("");
            }
        }

        pw.close();
    }

    public void generateExecutionReportbyProtocol(String fileName,String hostname, String iterationRegex, String machineRegex, int numIterations, int[]quantities) throws Exception
    {
        DeploymentManager deploymentmanager=new DeploymentManager(uc);
        ReportManager reportManager=new ReportManager(uc);
        PrintWriter pw=new PrintWriter(new File(fileName));
        SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        double[] sumTrans=new double[numIterations];
        double[] sumFin=new double[numIterations];

        double fallidos=0;
        int numMaquina=0;
        for(Integer j:quantities)
        {
            double[] tiemposDespliegue=new double[j];
            double[] tiemposTransmision=new double[j];
            numMaquina=1;
            fallidos=0;
            pw.println("Despliegues: "+j);
            pw.println(" , ");
            pw.println("Prueba,Numero,Name,T-Inicial,T-Transmision,T-Final,T-Transmision(s),T-Desplegado(s)");
            for(int i=0;i<numIterations;i++)
            {
                try
                {
                    System.out.println("REGEX "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    DeploymentExecutionResponse response=reportManager.getRelation(hostname+iterationRegex+(i+1)+machineRegex+j+";");
                    for(Integer executionId:response.getExecutions())
                    {
                        List<ExecutionStateResponse> list=deploymentmanager.getStates(response.getDeploymentId(),executionId);
                        String ini="";
                        String trans="";
                        String fin="";
                        for(ExecutionStateResponse executionStateResponse:list)
                        {
                            switch (executionStateResponse.getState().getId())
                            {
                                case(T_INICIAL):ini=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                case(T_FINAL):fin=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                case(T_TRANS):trans=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                            }
                        }
                        System.out.println(sf.parse(ini).getTime());
                        String tiempoTrans="";
                        try
                        {
                            double tTrans=sf.parse(trans).getTime()-sf.parse(ini).getTime();
                            tiempoTrans=tTrans+"";
                            tiemposDespliegue[j]=tTrans;
                            sumTrans[i]+=tTrans/1000.0;
                        }
                        catch(Exception e)
                        {

                        }
                        String tiempoFin="";
                        try {
                            double tFin = sf.parse(fin).getTime() - sf.parse(ini).getTime();
                            tiempoFin = tFin + "";
                            tiemposDespliegue[j]=tFin;
                            sumFin[i] += tFin / 1000.0;
                        }
                        catch(Exception e)
                        {
                            fallidos++;
                        }

                        pw.println((i+1)+","+numMaquina+","+hostname+iterationRegex+(i+1)+machineRegex+j+","+ini+","+trans+","+fin+","+tiempoTrans+","+tiempoFin);
                        numMaquina++;

                    }
                    double cant=j;
                    double desvTrans=0;
                    double desvFin=0;
                    for(int k=0;k<tiemposTransmision.length;k++)
                    {
                        desvTrans+=Math.pow(sumTrans[i]/j-tiemposTransmision[k],2);
                        desvFin+=Math.pow(sumFin[i]/j-tiemposDespliegue[k],2);
                    }
                    int n=tiemposTransmision.length-1;
                    if(n==0) n++;
                    desvTrans=Math.sqrt(desvTrans/n);
                    desvFin=Math.sqrt(desvTrans/n);
                    pw.println(" , ");
                    pw.println("Total transmisiones,Total fallidos,Promedio transmision (s),Promedio despliegue (s),Confiabilidad (%),Desviacion estandar transmision (s),Desviacion estandar despliegue (s)");
                    pw.println(j+","+fallidos+","+sumTrans[i]/j+","+sumFin[i]/j+","+(cant-fallidos)/cant+","+desvTrans+","+desvFin);
                    pw.println(" , ");
                    pw.println("Total despliegues,Total fallidos,Confiabilidad (%)");
                    pw.println(j+","+fallidos+","+(cant-fallidos)/cant);

                }
                catch(Exception e)
                {
                    System.out.println("There was an error during the HTTP Procedure in hostname "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    e.printStackTrace();
                }
                pw.println("");
            }
        }

        pw.close();
    }

    public static void main(String[] args) throws Exception {


        UnaCloudConnection uc = new UnaCloudConnection("E72EOKECIA79DZO89ME7M5NWLZAF5MXI","http://localhost:8080/UnaCloudWeb");
        ExecutionReportGenerator executionReportGenerator=new ExecutionReportGenerator(uc);
        //First method for making deployment time testing
        executionReportGenerator.generateExecutionReportbyProtocol("./reporte.csv","hola","","_",1,new int[]{2});
        //Second method for making deployment time testing with post-cache processing UNCOMENT NEXT LINE TÇO USE
        //deploymentTimeTesting.deploymentTimeTestingWithPostCacheCleaning(1,new int[]{1},"UnaCloudConnectionTest");

    }




}
