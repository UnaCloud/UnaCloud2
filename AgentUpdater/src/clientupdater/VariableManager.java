/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientupdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Variable manager to be used by client updater. This class comes from CommonShares library, but we separe it to deliver unacloud client updater with no library dependencies.
 * @author Clouder
 */
public class VariableManager {

    private final static Map<String, Object> map = new TreeMap<String, Object>() {
		private static final long serialVersionUID = 7563315934983406041L;
		@Override
        public Object put(String key, Object value) {
            key = key.replace("=", "");
            Object c = super.put(key, value);
            saveChanges();
            return c;
        }
    };
    private static File fileVars;
    
    public static synchronized String getStringValue(String key) {
        return (String) map.get("String." + key);
    }

    public static synchronized void setStringValue(String key, String v) {
        map.put("String." + key, v);
    }

    public static synchronized int getIntValue(String key) {
        return (Integer) map.get("Integer." + key);
    }

    public static synchronized void setIntValue(String key, int v) {
        map.put("Integer." + key, v);
    }

    public static synchronized void mergeValues(Map<String, String> temp) {
        for (Map.Entry<String, String> e : temp.entrySet()) {
            map.put(e.getKey(), e.getValue());
        }
        saveChanges();
    }

    private static void saveChanges() {
        try {
            PrintWriter pw = new PrintWriter(fileVars);
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (!e.getKey().startsWith("Secret")) {
                    pw.println(e.getKey() + "=" + e.getValue());
                }
            }
            pw.close();
        } catch (Exception e) {
        }
    }

    public static void init(String varsPath) {
        fileVars = new File(varsPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileVars));
            for (String h, j[]; (h = br.readLine()) != null;) {
                j = h.split("=");
                if (j[0].startsWith("String.")) {
                    map.put(j[0], j[1]);
                } else if (j[0].startsWith("Integer.")) {
                    map.put(j[0], Integer.parseInt(j[1]));
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
