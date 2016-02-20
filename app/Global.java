/**
 * Created by carlodidomenico on 19/02/16.
 */
import play.*;
import java.util.*;
import java.lang.reflect.*;

public class Global extends GlobalSettings {
    private final String path = "./lib/";
    @Override
    public void onStart(Application app) {
	// OSX/Linux:
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263.jnilib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263mpitransport.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263mpiworker.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263processtransport.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263processworker.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263remote.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263remotejni.jnilib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263tcpiptransport.dylib").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/libcplex1263tcpipworker.dylib").getPath());
	// WINDOWS:
	PlayNativeLibLoader.load(app.getFile("./lib/cplex1260.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260processtransport.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260processworker.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260remote.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260remotejni.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260tcpiptransport.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/cplex1260tcpipworker.dll").getPath());
        PlayNativeLibLoader.load(app.getFile("./lib/ILOG.Concert.dll").getPath());
	PlayNativeLibLoader.load(app.getFile("./lib/ILOG.CPLEX.dll").getPath());

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