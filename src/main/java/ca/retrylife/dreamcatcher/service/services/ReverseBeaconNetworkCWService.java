package ca.retrylife.dreamcatcher.service.services;

public class ReverseBeaconNetworkCWService extends ReverseBeaconNetworkServiceBase {

    public ReverseBeaconNetworkCWService() {
        super(7000);
    }

    @Override
    protected void handleTelnetMessage(String message) {
        
        System.out.println(message);

    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }

}