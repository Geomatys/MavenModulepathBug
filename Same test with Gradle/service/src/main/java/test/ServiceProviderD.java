package test;

public class ServiceProviderD implements ServiceInterface {
    @Override
    public String whoIAm() {
        return "Provider D declared in module-info.";
    }
}
