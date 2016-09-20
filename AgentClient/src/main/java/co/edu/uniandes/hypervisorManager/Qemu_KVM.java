package co.edu.uniandes.hypervisorManager;

import com.losandes.utils.Constants;

/**
 *
 * @author Juan Pablo Vinchira Salazar
 */
public class Qemu_KVM extends Libvirt {
    
    public Qemu_KVM(String path){
        super(path, Constants.QEMU_KVM_DRV);
        super.HYPERVISOR_ID = Constants.QEMU_KVM;
    }
}
