/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.hypervisorManager;

import co.edu.uniandes.virtualMachineManager.entities.ImageCopy;
import co.edu.uniandes.virtualMachineManager.entities.VirtualMachineExecution;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Juan Pablo Vinchira Salazar
 */
public abstract class Libvirt extends Hypervisor {
    
    public static String HYPERVISOR_ID="";
    private String driver="";
    private Connect connection = null;
    private String testVM = "debian-jessie";

    public Libvirt(String path, String driver) {
        super(path);
        this.driver = driver;
    }
    
    /**
     * Connect with the local libvirt instance
     */
    public void connect() {
        try{
            this.connection = new Connect(this.driver + ":///session");
        }catch(LibvirtException le){
            System.err.println("Error: " + le.toString());
        }
    }
    
    /**
     * Set virtual machine process priority
     * @param image 
     */
    private void setPriority(ImageCopy image){
        
    }
    
    /**
     * Unregister all the Hypervisor Virtual Machines
     */
    public void unregisterAllVms(){
        
    }
    
    /**
     * Get the current connection
     * @return 
     */
    public Connect getConnection() {
        return this.connection;
    }
    
    /**
     * Get the current hypervisor id
     * @return 
     */
    public String getHypervisorId() {
        return this.HYPERVISOR_ID;
    }
    
    /**
     * Replace the current connection
     * @param newConnection 
     */
    public void setConnection(Connect newConnection) {
        this.connection = newConnection;
    }
    
    @Override
    public void startVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        
        try{
            Domain virtualMachine = this.connection.domainLookupByName(testVM);
            virtualMachine.create();
        }catch(LibvirtException le){
            System.err.println("Error starting the Virtual Machine: " + le.toString());
        }
    }

    @Override
    public void configureVirtualMachineHardware(int cores, int ram, ImageCopy image) throws HypervisorOperationException {
        if(cores!=0&&ram!=0){
            
            try{
                // Get Domain configuration
                Domain virtualMachine = this.connection.domainLookupByName(testVM);
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document confXML = docBuilder.parse(new InputSource(new StringReader(virtualMachine.getXMLDesc(0))));
                
                // Set new number of cores
                confXML.getElementsByTagName("vcpu").item(0).setTextContent("" + cores);
                
                // Set new amount of memory
                ram = ram * 1024;
                confXML.getElementsByTagName("memory").item(0).setTextContent("" + ram);
                confXML.getElementsByTagName("currentMemory").item(0).setTextContent("" + ram);
                
                // 
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer trans = transFactory.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                
                // Convert to string the new Domain configuration
                StringWriter newConfXML = new StringWriter();
                trans.transform(new DOMSource(confXML), new StreamResult(newConfXML));
                
                // Update the Domain configuration
                virtualMachine.undefine();
                connection.domainDefineXML(newConfXML.toString());
                
            }catch(LibvirtException le){
                System.err.println("Error setting cpu or ram values: " + le.toString());
            }catch(ParserConfigurationException pce){
                System.err.println("Error creating xml document builder: " + pce.toString());
            }catch(TransformerConfigurationException tce){
                System.err.println("Error creating xml domain transformer: " + tce.toString());
            }catch(SAXException se){
                System.err.println("Error parsing virtual machine configuration file: " + se.toString());
            }catch(IOException ioe){
                System.err.println("Error reading virtual machine configuration file: " + ioe.toString());
            }catch(TransformerException te){
                System.err.println("Error transforming virtual machine configuration file: " + te.toString());
            }
        }
    }

    @Override
    public void stopVirtualMachine(ImageCopy image) {
        
        try{
            Domain virtualMachine = this.connection.domainLookupByName(testVM);
            virtualMachine.destroy();
        }catch(LibvirtException le){
            System.err.println("Error starting the Virtual Machine: " + le.toString());
        }
    }

    @Override
    public void restartVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        try{
            Domain virtualMachine = this.connection.domainLookupByName(testVM);
            virtualMachine.reboot(0);
        }catch(LibvirtException le){
            System.err.println("Error rebooting the Virtual Machine: " + le.toString());
        }
    }

    @Override
    public void executeCommandOnMachine(ImageCopy image, String command, String... args) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void takeVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restoreVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean existsVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyFileOnVirtualMachine(ImageCopy image, String destinationRoute, File sourceFile) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeVirtualMachineMac(ImageCopy image) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported cómoyet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerVirtualMachine(ImageCopy image) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unregisterVirtualMachine(ImageCopy image) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cloneVirtualMachine(ImageCopy source, ImageCopy dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<VirtualMachineExecution> checkExecutions(Collection<VirtualMachineExecution> executions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
