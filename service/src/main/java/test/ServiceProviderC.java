package test;

public class ServiceProviderC implements ServiceInterface {
    @Override
    public String whoIAm() {
        return "Provider C declared in META-INF.";
    }
}
