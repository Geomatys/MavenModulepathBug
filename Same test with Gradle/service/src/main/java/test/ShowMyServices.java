package test;

import java.util.ServiceLoader;

public class ShowMyServices {
    public static void run() {
        System.out.println("Start searching for services...");
        for (ServiceInterface s : ServiceLoader.load(ServiceInterface.class)) {
            System.out.println(s.whoIAm());
        }
        System.out.println("Done.");
    }
}
