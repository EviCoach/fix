package com.columnhack.fix;

import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class ServicesLab {
    private static ServicesLab sServicesLab;

    private List<Service> services = new ArrayList<>();
    private List<Service> nearbyServices = new ArrayList<>();

    public static ServicesLab getInstance(Context context){
        if(sServicesLab == null)
            sServicesLab = new ServicesLab();

        return sServicesLab;
    }

    public List<Service> getServices(){
        // Generate placeholder services
        // TODO: replace services list with real ones from server
        for (int index = 0; index < 100; index++) {
           Service service = new Service();
           service.setTitle("Service Title for " + index);
           service.setDescription("services " +
                   index + " Description");
           service.setServiceImageUrls(new String[]{"url1", "url2", "url3"});
           service.setContactAddress("Ajah, km4. Ikoyi, Lagos");
           nearbyServices.add(service);
        }
        return nearbyServices;
    }

    public List<Service> getNearbyServices(){
        // Generate placeholder services
        // TODO: replace services list with real ones from server
        for (int index = 0; index < 10; index++) {
            Service service = new Service();
            service.setTitle("Service Title for " + index);
            service.setDescription("services " +
                    index + " Description");
            service.setServiceImageUrls(new String[]{"url1", "url2", "url3"});
            service.setContactAddress("Ajah, km4. Ikoyi, Lagos");
            services.add(service);
        }
        return services;
    }
}
