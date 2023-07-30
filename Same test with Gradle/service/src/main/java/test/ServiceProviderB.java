package test;

public class ServiceProviderB implements ServiceInterface {
    @Override
    public String whoIAm() {
        return "Provider B declared in META-INF.";
    }
}
