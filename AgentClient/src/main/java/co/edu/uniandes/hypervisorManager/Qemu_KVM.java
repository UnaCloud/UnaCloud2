/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.hypervisorManager;

import org.libvirt.LibvirtException;
import com.losandes.utils.Constants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author cronot99
 */
public class Qemu_KVM extends Libvirt {
    
    /**
     * Temp main method for development goals
     * @param args
     * @throws LibvirtException 
     */
    public static void main(String[] args) throws LibvirtException, HypervisorOperationException {
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        
        Libvirt libvirtTest = new Qemu_KVM("NoPath");
        
        System.out.println("Attempting to connect...");
        libvirtTest.connect();
        
        // System.out.println("Configuring virtual machine...");
        // libvirtTest.configureVirtualMachineHardware(1, 1024, null);
        
        // System.out.println("Starting virtual machine...");
        // libvirtTest.startVirtualMachine(null);
        
        // System.out.println("Destroying virtual machine...");
        // libvirtTest.stopVirtualMachine(null);
        
        // System.out.println("Rebooting virtual machine...");
        // libvirtTest.restartVirtualMachine(null);
        
        // System.out.println("Creating virtual machine snapshot...");
        // libvirtTest.takeVirtualMachineSnapshot(null, "Test");
        
        System.out.println("Done.");
    }
    
    public Qemu_KVM(String path){
        super(path, Constants.QEMU_KVM_DRV);
        HYPERVISOR_ID = Constants.QEMU_KVM;
    }
}
