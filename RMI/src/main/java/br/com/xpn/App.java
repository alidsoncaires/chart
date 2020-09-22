package br.com.xpn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class App {
    public static void main(final String[] args) {
        final MyCommandOptions options = new MyCommandOptions();
        options.run(args);
        // System.out.println(options.getHost()+":"+options.getPort());
        testConnected(options.getHost(), options.getPort());
        //System.exit(0);
    }

    private static boolean testConnected(final String host, final int port) {
        Registry registry;
        try {
            // System.out.println("\n\nendpoint:["+host+":"+port+"]");
            registry = LocateRegistry.getRegistry(host, port);
            for (final String name : registry.list()) {
                System.out.println(name);
                return true;
            }
        } catch (final RemoteException e) {
            //e.printStackTrace();
            System.out.println(e.getCause().getMessage());
            System.exit(1);
        }
        return false;
    }
}
