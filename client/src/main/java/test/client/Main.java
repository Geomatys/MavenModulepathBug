package test.client;

import test.ShowMyServices;

public class Main {
    public static void main(String[] args) {
        ShowMyServices.run();
        if (ShowMyServices.class.getModule().isNamed()) {
            System.out.printf("The dependency has been loaded as named module. Great!%n"
                            + "This is what we need for the `module-info` to be used.%n");
        } else {
            System.out.printf("The dependency has been loaded as an unnamed module.%n"
                            + "Consequently its `module-info` file has been ignored,%n"
                            + "and the `META-INF/services` directory is used instead.%n");
        }
    }
}
