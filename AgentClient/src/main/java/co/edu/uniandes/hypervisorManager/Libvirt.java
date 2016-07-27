/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.hypervisorManager;

import co.edu.uniandes.virtualMachineManager.entities.ImageCopy;
import co.edu.uniandes.virtualMachineManager.entities.VirtualMachineExecution;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author cronot99
 */
public class Libvirt extends Hypervisor {
    
    public static String HYPERVISOR_ID="";
    private String driver="";
    private String connection="";

    public Libvirt(String hypervisorId) {
        super(""); // Hypervisor without path
        this.HYPERVISOR_ID=hypervisorId;
    }
    
    public void connect(){
        
    }
    
    private void setPriority(ImageCopy image){
        
    }
    
    public void unregisterAllVms(){
        
    }
    
    public String getConnection() {
        return this.connection;
    }
    
    public String getHypervisorId() {
        return this.HYPERVISOR_ID;
    }
    public void setConnection(String uri) {
    
    }
    public void setHypervisorId(String hypervisorId) {
    
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
