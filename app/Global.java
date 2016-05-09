/**
 * Created by carlodidomenico on 19/02/16.
 */
import play.*;
import java.util.*;
import java.lang.reflect.*;

public class Global extends GlobalSettings {
    private String path = "lib/";
    @Override
    public void onStart(Application app) {
        /*
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("OS is = " + os);
        if(os.contains("mac os x") || os.contains("mac")) {
            // OSX/Linux:
            path += "OSX_Linux/";
            //path = "";
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263.jnilib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263mpitransport.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263mpiworker.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263processtransport.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263processworker.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263remote.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263remotejni.jnilib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263tcpiptransport.dylib").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"libcplex1263tcpipworker.dylib").getPath());

        }else if (os.contains("windows")){
	    //WINDOWS:
            path += "WIN/";

            PlayNativeLibLoader.load(app.getFile(path +"cplex1260.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260processtransport.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260processworker.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260remote.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260remotejni.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260tcpiptransport.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"cplex1260tcpipworker.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"ILOG.Concert.dll").getPath());
            PlayNativeLibLoader.load(app.getFile(path +"ILOG.CPLEX.dll").getPath());

        }else {
            System.out.println("Something went wrong loadinc CPLEX libs");

                }*/
    }
    @Override
    public void onStop(Application app) {
        // NOTE: Unload library in here
        try {
            Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            Vector libs = (Vector)field.get(app.classloader());
            for (Object lib : libs) {
                Method finalize = lib.getClass().getDeclaredMethod("finalize", new Class[0]);
                finalize.setAccessible(true);
                finalize.invoke(lib, new Object[0]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}