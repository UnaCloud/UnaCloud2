package TestManager;

import Connection.DeploymentManager;
import Connection.ReportManager;
import Connection.UnaCloudConnection;
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
                    for(int k=1;k<4;k++)
                    {
                        System.out.println("REGEX "+k+"-"+hostname+iterationRegex+(i+1)+machineRegex+j);
                        DeploymentExecutionResponse response=reportManager.getRelation(k+"-"+hostname+iterationRegex+(i+1)+machineRegex+j+";");
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
                            numMaquina++;
                            if(numMaquina>j)
                                numMaquina=1;
                        }
                    }

                }
                catch(Exception e)
                {
                    System.out.println("There was an error during the HTTP Procedure in hostname "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    e.printStackTrace();
                    continue;
                }

            }
            pw.println("");
            pw.println("FALLIDOS,"+fallidos);
            pw.println("");
        }
        pw.close();
    }

    public void generateExecutionReportbyProtocol(String averageFileName,String fileName,String hostname, String iterationRegex, String machineRegex, int numIterations, int[]quantities) throws Exception
    {
        DeploymentManager deploymentmanager=new DeploymentManager(uc);
        ReportManager reportManager=new ReportManager(uc);
        PrintWriter pw=new PrintWriter(new File(fileName));
        PrintWriter pw2= new PrintWriter(new File(averageFileName));
        SimpleDateFormat sf= new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        double[] sumTrans=null;
        double[] sumFin=null;
        double fallidos;
        int numMaquina=0;
        pw2.println("Cantidad,Tiempo de transmisión (s),Tiempo de despliegue (s)");
        for(Integer j:quantities)
        {
            double[] tiemposDespliegue=new double[j];
            double[] tiemposTransmision=new double[j];
            double promTrans=0;
            double promFin=0;
            numMaquina=1;
            pw.println("Despliegues: "+j);
            pw.println(" , ");
            pw.println("Prueba,Numero,Name,T-Inicial,T-Transmision,T-Final,T-Transmision(s),T-Desplegado(s)");
            sumTrans=new double[numIterations];
            sumFin=new double[numIterations];
            for(int i=0;i<numIterations;i++)
            {
                try
                {
                    fallidos=0;
                    System.out.println("REGEX "+hostname+iterationRegex+(i+1)+machineRegex+j);
                    DeploymentExecutionResponse response=reportManager.getRelation(hostname+iterationRegex+(i+1)+machineRegex+j+iterationRegex);
                    for(Integer executionId:response.getExecutions())
                    {
                        String ini="";
                        String trans="";
                        String fin="";
                        String tiempoTrans="";
                        String tiempoFin="";
                        try {
                            List<ExecutionStateResponse> list=deploymentmanager.getStates(response.getDeploymentId(),executionId);
                            for(ExecutionStateResponse executionStateResponse:list)
                            {
                                System.out.println("ESTADO "+executionStateResponse.getState().getId()+" "+T_TRANS+" "+(executionStateResponse.getState().getId().equals(T_TRANS))+" "+executionStateResponse.getChangeTime());
                                switch (executionStateResponse.getState().getId())
                                {
                                    case(T_INICIAL):ini=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                    case(T_FINAL):fin=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                    case(T_TRANS):trans=executionStateResponse.getChangeTime().replace("T"," ").replace("Z",""); break;
                                    default: continue;
                                }
                            }
                            try
                            {
                                double tTrans=sf.parse(trans).getTime()-sf.parse(ini).getTime();
                                tiempoTrans=tTrans+"";
                                tiemposTransmision[j-1]=tTrans;
                                sumTrans[i]+=tTrans/1000.0;
                            }
                            catch(Exception e)
                            {
                                tiemposTransmision[j-1]=0;
                                e.printStackTrace();
                            }
                            tiempoFin="";
                            try {
                                double tFin = sf.parse(fin).getTime() - sf.parse(ini).getTime();
                                tiempoFin = tFin + "";
                                tiemposDespliegue[j-1]=tFin;
                                sumFin[i] += tFin / 1000.0;
                            }
                            catch(Exception e)
                            {
                                fallidos++;
                                tiemposDespliegue[j-1]=0;
                                e.printStackTrace();
                            }
                        }
                        catch(Exception e)
                        {
                            ini="";
                            trans="";
                            fin="";
                            tiempoTrans="";
                            tiempoFin="";
                            e.printStackTrace();
                        }
                        System.out.println((i+1)+","+numMaquina+","+hostname+iterationRegex+(i+1)+machineRegex+j+","+ini+","+trans+","+fin+","+tiempoTrans+","+tiempoFin);
                        pw.println((i+1)+","+numMaquina+","+hostname+iterationRegex+(i+1)+machineRegex+j+","+ini+","+trans+","+fin+","+tiempoTrans+","+tiempoFin);
                        numMaquina++;
                        if(numMaquina>j)
                            numMaquina=1;
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
                    desvFin=Math.sqrt(desvFin/n);
                    promTrans+=sumTrans[i]/j;
                    promFin+=sumFin[i]/j;
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
                pw.println(" , ");
            }
            pw2.println(j+","+promTrans/numIterations+","+promFin/numIterations);
            pw.println("Promedio total de transmisión (s),Promedio total de despliegue (s)");
            pw.println(promTrans/numIterations+","+promFin/numIterations);
        }
        pw2.close();
        pw.close();
    }

    public static void main(String[] args) throws Exception {


        UnaCloudConnection uc = new UnaCloudConnection("5ZVAZEP0Q7RQRYK2LXYON05T7LUA9GOI","http://157.253.236.113:8080/UnaCloud");
        ExecutionReportGenerator executionReportGenerator=new ExecutionReportGenerator(uc);
        //First method for making deployment time testing
        //executionReportGenerator.generateExecutionReportForEnergyTesting("./reporteTest.csv","MyNewHostXXX","-","_",1,new int[]{40});
       // String[] reportes=new String[]{"P2PSmall1-10.csv","P2PMedium1-10.csv","P2PLarge1-10.csv","TCPSmall1-10.csv","TCPMedium1-10.csv","TCPLarge1-10.csv","P2PSmall15-50.csv","P2PMedium15-50.csv","P2PLarge15-50.csv","TCPSmall15-50.csv","TCPMedium15-50.csv","TCPLarge15-50.csv"};
       // String[] regex= new String[]{"P2PSmallTestWaira2SeriesApril","P2PMediumTestWaira2SeriesApril","P2PLargeTestWaira2SeriesApril","SecondP2PSmallTestWaira2SeriesApril","SecondP2PMediumTestWaira2SeriesApril","SecondP2PLargeTestWaira2SeriesApril","BiggerP2PSmallTestWaira2SeriesApril",
        //                               "BiggerP2PMediumTestWaira2SeriesApril","BiggerP2PLargeTestWaira2SeriesApril","BiggerTCPSmallTestWaira2SeriesApril","BiggerTCPMediumTestWaira2SeriesApril","BiggerTCPLargeTestWaira2SeriesApril"};
        //CAMBIO PARA PRUEBAS BÁSICAS
        String[] reportes=new String[]{"P2PSmall15-50.csv","P2PMedium15-50.csv","P2PLarge15-50.csv"};
        String[] regex= new String[]{"BiggerP2PSmallTestWaira2SeriesApril","BiggerP2PMediumTestWaira2SeriesApril","BiggerP2PLargeTestWaira2SeriesApril"};

        //for(int i=0;i<3;i++)
        //executionReportGenerator.generateExecutionReportbyProtocol("./data/average/prom"+reportes[i],"./data/reports/reporte"+reportes[i],regex[i],"-","_",5,new int[]{1,2,3,4,5,6,7,8,9,10});
        for(int i=0;i<reportes.length;i++)
            executionReportGenerator.generateExecutionReportbyProtocol("./data/average/prom"+reportes[i],"./data/reports/reporte"+reportes[i],regex[i],"-","_",5,new int[]{15,25,50});

    }

}
