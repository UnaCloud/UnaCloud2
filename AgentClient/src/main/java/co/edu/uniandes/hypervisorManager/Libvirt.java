/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.hypervisorManager;

import co.edu.uniandes.virtualMachineManager.entities.ImageCopy;
import co.edu.uniandes.virtualMachineManager.entities.VirtualMachineExecution;
import org.libvirt.Connect;
import java.io.File;
import java.util.Collection;
import java.util.List;
import org.libvirt.LibvirtException;

/**
 *
 * @author Juan Pablo Vinchira Salazar
 */
public class Libvirt extends Hypervisor {
    
    public static String HYPERVISOR_ID="";
    private String driver="";
    private Connect connection = null;

    /**
     * Temp main method for development goals
     * @param args
     * @throws LibvirtException 
     */
    public static void main(String[] args) throws LibvirtException {
        
        Libvirt libvirtTest = new Libvirt(com.losandes.utils.Constants.QEMU_KVM, com.losandes.utils.Constants.QEMU_KVM_DRV);
        
        System.out.println("Attempting to connect...");
        libvirtTest.connect();
    }

    public Libvirt(String hypervisorId, String hypervisorDrv) {
        super(""); // Hypervisor without path
        this.HYPERVISOR_ID = hypervisorId;
        this.driver = hypervisorDrv;
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
    
    /**
     * Replace the current hypervisor id
     * @param hypervisorId 
     */
    public void setHypervisorId(String hypervisorId) {
        this.HYPERVISOR_ID = hypervisorId;
    }

    @Override
    public void startVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configureVirtualMachineHardware(int cores, int ram, ImageCopy image) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopVirtualMachine(ImageCopy image) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restartVirtualMachine(ImageCopy image) throws HypervisorOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
