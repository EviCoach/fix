package com.columnhack.fix;

import android.content.Context;

import com.columnhack.fix.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceLab {
    private static ServiceLab sServiceLab;
    private static Context sContext;

    private List<Service> services = new ArrayList<>();
    private List<Service> nearbyServices = new ArrayList<>();
    private List<Service> myServices = new ArrayList<>();

    public static ServiceLab getInstance(Context context){
        if(sServiceLab == null)
            sServiceLab = new ServiceLab();
        sContext = context;

        return sServiceLab;
    }



    public List<Service> getServices(){
        services.clear();
        // Generate placeholder services
        // TODO: replace services list with real ones from server
        for (int index = 0; index < 50; index++) {
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

    public List<Service> getNearbyServices(){
        nearbyServices.clear();
        // Generate placeholder services
        // TODO: replace services list with real ones from server
        for (int index = 0; index < 10; index++) {
            Service service = new Service();
            service.setTitle("Service Title for " + index);
            service.setDescription("services " +
                    index + " Description");
            service.setTitle("Service Title for " + index);
            service.setServiceImgs(new int[]{
                    R.drawable.laptop_repair, R.drawable.laptop_repair_orig,
                    R.drawable.laptop_repair, R.drawable.laptop_repair_orig
            });
            service.setContactAddress("Ajah, km4. Ikoyi, Lagos");
            nearbyServices.add(service);
        }
        return nearbyServices;
    }

    public List<Service> getMyServices(){
        myServices.clear();
        // Generate placeholder services
        // TODO: replace services list with real ones from server
        for (int index = 0; index < 10; index++) {
            Service service = new Service();
            service.setTitle("Service Title for " + index);
            service.setDescription("services " +
                    index + " Description");
            service.setTitle("Service Title for " + index);
            service.setServiceImgs(new int[]{
                    R.drawable.laptop_repair, R.drawable.laptop_repair_orig,
                    R.drawable.laptop_repair, R.drawable.laptop_repair_orig
            });
            service.setContactAddress("Ajah, km4. Ikoyi, Lagos");
            myServices.add(service);
        }
        return myServices;
    }
}
