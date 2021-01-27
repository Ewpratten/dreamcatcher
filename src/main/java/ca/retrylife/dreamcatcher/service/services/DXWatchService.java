package ca.retrylife.dreamcatcher.service.services;

import java.util.ArrayList;
import java.util.List;

import ca.retrylife.dreamcatcher.service.Service;
import ca.retrylife.dreamcatcher.service.bases.DXWatchServiceBase;

public class DXWatchService implements Service {
    
    // List of services per skimmer
    private List<DXWatchServiceBase> serviceList = new ArrayList<>();

    public DXWatchService() {
        // Add every sub-service
        this.serviceList.add(new DXWatchServiceBase("dx.iz7auh.net", 8000));
        this.serviceList.add(new DXWatchServiceBase("cluster.i0inu.it", 7300));
        this.serviceList.add(new DXWatchServiceBase("py5jo.no-ip.org", 8000));
        this.serviceList.add(new DXWatchServiceBase("dxcluster.pl", 7300));
        this.serviceList.add(new DXWatchServiceBase("dxc.sv5fri.eu", 7300));
        this.serviceList.add(new DXWatchServiceBase("dx.packet-radio.nl", 7300));
        this.serviceList.add(new DXWatchServiceBase("sv1iw.ignorelist.com", 73));
        this.serviceList.add(new DXWatchServiceBase("ik5pwj-6.dyndns.org", 8000));
    }

    @Override
    public void launch() {
        this.serviceList.forEach((service)->{
            service.launch();
        });
    }

    @Override
    public void kill() {
        this.serviceList.forEach((service)->{
            service.kill();
        });
    }

    @Override
    public String getServiceName() {
        return getClass().getName();
    }
    
}