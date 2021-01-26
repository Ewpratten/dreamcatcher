package ca.retrylife.dreamcatcher.service;

import java.util.ArrayList;

public class ServiceManager implements Service {

    // Internal instance reference
    private static ServiceManager instance = null;

    /**
     * Get the global instance of ServiceManager
     *
     * @return ServiceManager instance
     */
    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    // All registered services
    private ArrayList<Service> services = new ArrayList<>();

    // Hidden constructor to force singleton usage
    private ServiceManager() {
    }

    public void register(Service service) {
        this.services.add(service);
    }

    @Override
    public void launch() {
        this.services.forEach((Service service) -> {
            service.launch();
        });
    }

    @Override
    public void kill() {
        this.services.forEach((Service service) -> {
            service.kill();
        });
    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }

}