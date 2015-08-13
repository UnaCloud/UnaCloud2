/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualMachineConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * PrintWriter used to write files that have an \n as line separator and not a \n\r like windows print writers
 * @author ga.sotelo69
 */
public class LinuxPrintWriter extends PrintWriter{

    /**
     * Creates a new linux print writer over the given outpustream
     * @param ous
     */
    public LinuxPrintWriter(OutputStream ous){
        super(ous);
    }

    /**
     * Creates a new linux print writer for the given file
     * @param ous
     * @throws FileNotFoundException
     */
    public LinuxPrintWriter(File ous) throws FileNotFoundException{
        super(ous);
    }
    /**
     * Creates a new linux print writer for the file represented by the given path
     * @param ous
     * @throws FileNotFoundException
     */
    public LinuxPrintWriter(String ous) throws FileNotFoundException{
        super(ous);
    }

    /**
     * Writes a line, using an \n as last character
     * @param h
     */
    @Override
    public void println(String h){
        super.write(h+"\n");
    }

}
