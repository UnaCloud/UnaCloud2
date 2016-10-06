package co.edu.uniandes.hypervisorManager;

import co.edu.uniandes.virtualMachineManager.entities.ImageCopy;
import co.edu.uniandes.virtualMachineManager.entities.VirtualMachineExecution;
import com.losandes.utils.LocalProcessExecutor;
import java.io.BufferedReader;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.DomainSnapshot;
import org.libvirt.LibvirtException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    public Libvirt(String path, String driver) {
        super(path);
        this.driver = driver;
    }
    
    private String genMACAddress(){
        
        String vendorOctets = "52:54:00", octeta = "", octetb = "", octetc = "";
        
        octeta = Integer.toHexString((int)(Math.round(Math.random() * 255)));
        octetb = Integer.toHexString((int)(Math.round(Math.random() * 255)));
        octetc = Integer.toHexString((int)(Math.round(Math.random() * 255)));
        
        return vendorOctets + ":" + octeta + ":" + octetb + ":" + octetc ;
    }
    
    /**
     * Connect with the local libvirt instance
     */
    public void connect() {
        try{
            this.connection = new Connect(this.driver + ":///system");
        }catch(LibvirtException le){
            System.err.println("Error: " + le.toString());
        }
    }
    
    /**
     * Set virtual machine process priority
     * @param image 
     */
    public void setPriority(ImageCopy image){
        
        try {
                String vmPID = "";
                
                // List all processes with complete execution command information
                String[] psResult = LocalProcessExecutor.executeCommandOutput("ps", "-A", "-o", "pid args").split("\n");
                
                // Search the Virtual Machine PID
                for(String process:psResult){
                    if(process.contains(super.getExecutablePath()) && process.contains("-name " + image.getVirtualMachineName())){
                        Pattern pidPattern = Pattern.compile("^[ ]*([0-9]*).*");
                        Matcher pidMatches = pidPattern.matcher(process);
                        pidMatches.find();
                        vmPID = pidMatches.group(1);
                        break;
                    }
                }
    		sleep(1000);
                
                // Set the lowest priority
    		LocalProcessExecutor.executeCommandOutput("renice", "-n", "19", "-p", vmPID);
    		sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Unregister all the Hypervisor Virtual Machines
     */
    public void unregisterAllVms(){
        try{
            this.connect();
            String[] definedDomains = this.connection.listDefinedDomains();
            int[] activeDomains = this.connection.listDomains();
            
            Domain vm = null;
            
            for(String definedDomain:definedDomains){
                vm = this.connection.domainLookupByName(definedDomain);
                vm.undefine();
            }
            
            for(int activeDomain:activeDomains){
                vm = this.connection.domainLookupByID(activeDomain);
                vm.undefine();
            }
            
        }catch(LibvirtException le){
            System.err.println("Error attemping to unregister all virtual machines: " + le.toString());
        }
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
            this.connect();
            Domain virtualMachine = this.connection.domainLookupByName(image.getVirtualMachineName());
            virtualMachine.create();
            
            ServerSocket ssocket = new ServerSocket(1333);
        
            while(true){
                Socket csocket = ssocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
                image.getImage().setNatAddress(in.readLine());
                csocket.close();
                break;
            }
            
        }catch(LibvirtException le){
            System.err.println("Error starting the Virtual Machine: " + le.toString());
        }catch(IOException ioe){
            System.err.println("Communication error with the Virtual Machine: " + ioe.toString());
        }
    }

    @Override
    public void configureVirtualMachineHardware(int cores, int ram, ImageCopy image) throws HypervisorOperationException {
        if(cores!=0&&ram!=0){
            try{
                String domainName = image.getVirtualMachineName();
                LocalProcessExecutor.executeCommandOutput("virsh", "-c", this.connection.getURI(), "setmaxmem", "--size", ram + "M", "--domain", domainName, "--config");
                LocalProcessExecutor.executeCommandOutput("virsh", "-c", this.connection.getURI(), "setmem", "--size",ram + "M", "--domain", domainName, "--config");
                LocalProcessExecutor.executeCommandOutput("virsh", "-c", this.connection.getURI(), "setvcpus", "--count", cores + "", "--domain", domainName, "--config");
                sleep(1000);
            }catch(Exception e){
                System.err.println("Error setting cpu or ram values: " + e.toString());
            }
        }
    }

    @Override
    public void stopVirtualMachine(ImageCopy image) {
        try{
            Domain virtualMachine = this.connection.domainLookupByName(image.getVirtualMachineName());
            virtualMachine.destroy();
        }catch(LibvirtException le){
            System.err.println("Error starting the Virtual Machine: " + le.toString());
        }
    }

    @Override
    public void restartVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        try{
            Domain virtualMachine = this.connection.domainLookupByName(image.getVirtualMachineName());
            virtualMachine.reboot(0);
        }catch(LibvirtException le){
            System.err.println("Error rebooting the Virtual Machine: " + le.toString());
        }
    }

    @Override
    abstract public void executeCommandOnMachine(ImageCopy image, String command, String... args) throws HypervisorOperationException;

    @Override
    public void takeVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        try{
                // Get Domain configuration
                Domain virtualMachine = this.connection.domainLookupByName(image.getVirtualMachineName());
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document snapshotXML = docBuilder.newDocument();
                
                // Create snapshot XML structure
                Element root = snapshotXML.createElement("domainsnapshot");
                snapshotXML.appendChild(root);
                
                root.appendChild(snapshotXML.createElement("name")).setTextContent(snapshotname);
                root.appendChild(snapshotXML.createElement("description")).setTextContent(snapshotname);
                
                // Format the new snapshot file
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer trans = transFactory.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                
                // Convert to string the new Domain configuration
                StringWriter newSnapshotXML = new StringWriter();
                trans.transform(new DOMSource(snapshotXML), new StreamResult(newSnapshotXML));
                
                // Create new snapshot
                virtualMachine.snapshotCreateXML(newSnapshotXML.toString());
                
            }catch(LibvirtException le){
                System.err.println("Error creating the snapshot: " + le.toString());
            }catch(ParserConfigurationException pce){
                System.err.println("Error creating xml document builder: " + pce.toString());
            }catch(TransformerConfigurationException tce){
                System.err.println("Error creating xml domain transformer: " + tce.toString());
            }catch(TransformerException te){
                System.err.println("Error transforming virtual machine configuration file: " + te.toString());
            }
    }

    @Override
    public void deleteVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        try{
            Domain virtualMachine = connection.domainLookupByName(image.getVirtualMachineName());
            DomainSnapshot snapshot = virtualMachine.snapshotLookupByName(snapshotname);
            snapshot.delete(0);
        }catch(LibvirtException le){
            System.err.println("Error deleting the snapshot: " + le.toString());
        }
    }

    @Override
    public void restoreVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        try{
            Domain virtualMachine = connection.domainLookupByName(image.getVirtualMachineName());
            DomainSnapshot snapshot = virtualMachine.snapshotLookupByName(snapshotname);
            virtualMachine.revertToSnapshot(snapshot);
        }catch(LibvirtException le){
            System.err.println("Error reverting to snapshot " + snapshotname + ": " + le.toString());
        }
    }

    @Override
    public boolean existsVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws HypervisorOperationException {
        try{
            boolean domainExists = false;
            
            Domain virtualMachine = connection.domainLookupByName(image.getVirtualMachineName());
            if(virtualMachine.snapshotLookupByName(snapshotname) != null){
                System.out.println("Snapshot " + snapshotname + " Found");
                domainExists = true;
            }
            return domainExists;
        }catch(LibvirtException le){
            if(le.toString().contains("no domain snapshot")){
                System.out.println("Snapshot " + snapshotname + " Not found");
            }else{
                System.err.println("Error trying to access to snapshots list: " + le.toString());
            }
            return false;
        }
    }

    @Override
    abstract public void copyFileOnVirtualMachine(ImageCopy image, String destinationRoute, File sourceFile) throws HypervisorOperationException;

    @Override
    public void changeVirtualMachineMac(ImageCopy image) {
        
        try{
            this.connect();
            
            // Get Domain configuration
            Domain virtualMachine = this.connection.domainLookupByName(image.getVirtualMachineName());
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document confXML = docBuilder.parse(new InputSource(new StringReader(virtualMachine.getXMLDesc(0))));
                
            // Set new mac address
            confXML.getElementsByTagName("mac").item(0).getAttributes().getNamedItem("address").setTextContent(this.genMACAddress());
                
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

    @Override
    public void registerVirtualMachine(ImageCopy image) {
        
        try{
            this.connect();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            String sp = File.separator;
            File confXMLFile = new File(System.getProperty("user.dir") + sp + "vmtemplates" + sp + "vmtemplate.xml");
            Document confXML = docBuilder.parse(confXMLFile);
            String vmUUID = UUID.randomUUID().toString();
            
            // Set new vm name
            confXML.getElementsByTagName("name").item(0).setTextContent(image.getVirtualMachineName());
            
            // Set new vm uuid
            confXML.getElementsByTagName("uuid").item(0).setTextContent(vmUUID);
            
            // Set new vm title
            confXML.getElementsByTagName("title").item(0).setTextContent(image.getVirtualMachineName());
            
            // Set new vm description
            confXML.getElementsByTagName("description").item(0).setTextContent("UnaCloud deployment: vm_" + image.getVirtualMachineName());
            
            // Set new vm emulator path
            confXML.getElementsByTagName("emulator").item(0).setTextContent(super.getExecutablePath());
            
            // Set new machine type
            confXML.getElementsByTagName("type").item(0).getAttributes().getNamedItem("machine").setTextContent("pc-i440fx-2.1");
            
            // Set new mac address
            // confXML.getElementsByTagName("mac").item(0).getAttributes().getNamedItem("address").setTextContent(this.genMACAddress());
            
            // Set disk
            int numNodes = confXML.getElementsByTagName("disk").getLength();
            for(int i = 0; i < numNodes; i++){
                Node node = confXML.getElementsByTagName("disk").item(i);
                if (node.getAttributes().getNamedItem("type").getTextContent().equalsIgnoreCase("file") && node.getAttributes().getNamedItem("device").getTextContent().equalsIgnoreCase("disk")){
                    int childNodesCount = node.getChildNodes().getLength();
                    for(int j = 0; j < childNodesCount; j++){
                        Node childNode = node.getChildNodes().item(j);
                        if(childNode.getNodeName().equals("source")){
                            childNode.getAttributes().getNamedItem("file").setTextContent(image.getMainFile().getPath());
                            break;
                        }
                    }
                    break;
                }
            }
            
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer trans = transFactory.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                
            // Convert to string the new Domain configuration
            StringWriter newConfXML = new StringWriter();
            trans.transform(new DOMSource(confXML), new StreamResult(newConfXML));
                
            // Define the new domain
            connection.domainDefineXML(newConfXML.toString());

        }catch(ParserConfigurationException pce){
            System.err.println("Error creating xml document builder: " + pce.toString());
        }catch(SAXException se){
            System.err.println("Error parsing virtual machine configuration file: " + se.toString());
        }catch(IOException ioe){
            System.err.println("Error reading virtual machine configuration file: " + ioe.toString());
        }catch(TransformerConfigurationException tce){
            System.err.println("Error creating xml domain transformer: " + tce.toString());
        }catch(TransformerException te){
            System.err.println("Error transforming virtual machine configuration file: " + te.toString());
        }catch(LibvirtException le){
            System.err.println("Error trying to define the new virtual machine: " + le.toString());
        }
    }

    @Override
    public void unregisterVirtualMachine(ImageCopy image) {
        try{
            this.connect();
            Domain virtualMachine = connection.domainLookupByName(image.getVirtualMachineName());
            virtualMachine.undefine();
        }catch(LibvirtException le){
            System.err.println("Error trying to undefine the virtual machine: " + le.toString());
        }
    }

    @Override
    public void cloneVirtualMachine(ImageCopy source, ImageCopy dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<VirtualMachineExecution> checkExecutions(Collection<VirtualMachineExecution> executions) {
                this.connect();
		List<VirtualMachineExecution> executionsToDelete = new ArrayList<VirtualMachineExecution>();
		List<String> list = new ArrayList<String>();
                try{
                    int[] activeDomains = connection.listDomains();
                    for(int eachDomain: activeDomains){
                        list.add(connection.domainLookupByID(eachDomain).getName());
                    }
                }catch(LibvirtException le){
                    System.err.println("Error trying to retrieve active virtual machines list: " + le.toString());
                }
                
		for (VirtualMachineExecution execution: executions) {
			boolean isRunning = false;
			for(String exeInHypervisor: list){
				if(exeInHypervisor.contains(execution.getImage().getVirtualMachineName())){
					isRunning = true;
					break;
				}
			}	
			if(!isRunning)executionsToDelete.add(execution);	
		}
		return executionsToDelete;    
    }
    
}
