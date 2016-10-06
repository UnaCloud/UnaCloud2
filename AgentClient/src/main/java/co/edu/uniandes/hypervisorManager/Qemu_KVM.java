package co.edu.uniandes.hypervisorManager;

import co.edu.uniandes.virtualMachineManager.entities.ImageCopy;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.losandes.utils.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Juan Pablo Vinchira Salazar
 */
public class Qemu_KVM extends Libvirt {

    public Qemu_KVM(String path){
        super(path, Constants.QEMU_KVM_DRV);
        super.HYPERVISOR_ID = Constants.QEMU_KVM;
    }

    @Override
    public void executeCommandOnMachine(ImageCopy image, String command, String... args) throws HypervisorOperationException {
        try {
            JSch jsch = new JSch();
            String host = image.getImage().getNatAddress();
            String user = image.getImage().getUsername();

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(image.getImage().getPassword());

            for(String opt:args){
                command = command + " " + opt;
            }

            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.connect();

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.toString());
        }
    }

    @Override
    public void copyFileOnVirtualMachine(ImageCopy image, String destinationRoute, File sourceFile) throws HypervisorOperationException {

        String command = "scp -t " + destinationRoute;

        try {
            JSch jsch = new JSch();
            String host = image.getImage().getNatAddress();
            String user = image.getImage().getUsername();

            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(image.getImage().getPassword());

            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if(checkAck(in)!=0){
                System.exit(0);
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize=sourceFile.length();
            command="C0644 "+filesize+" "+ sourceFile.getName() + "\n";
            out.write(command.getBytes()); out.flush();
            if(checkAck(in)!=0){
              System.exit(0);
            }

            FileInputStream fis = new FileInputStream(sourceFile);

            byte[] buf=new byte[1024];
            while(true){
              int len=fis.read(buf, 0, buf.length);
              if(len<=0) break;
              out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis=null;
            buf[0]=0; out.write(buf, 0, 1); out.flush();
            if(checkAck(in)!=0){
                System.exit(0);
            }

            out.close();

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.toString());
        }
    }

    private static int checkAck(InputStream in) throws IOException{
        int b=in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if(b==0) return b;
        if(b==-1) return b;

        if(b==1 || b==2){
          StringBuffer sb=new StringBuffer();
          int c;
          do {
            c=in.read();
            sb.append((char)c);
          }
          while(c!='\n');
          if(b==1){ // error
            System.out.print(sb.toString());
          }
          if(b==2){ // fatal error
            System.out.print(sb.toString());
          }
        }
        return b;
    }
}
