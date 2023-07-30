package test;

public class ServiceProviderA implements ServiceInterface {
    @Override
    public String whoIAm() {
        return "Provider A declared in module-info.";
    }
}
