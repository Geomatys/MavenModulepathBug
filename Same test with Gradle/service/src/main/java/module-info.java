module service {
    exports test;

    uses test.ServiceInterface;

    provides test.ServiceInterface with
             test.ServiceProviderA,
             test.ServiceProviderD;
}
