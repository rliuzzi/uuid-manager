package controllers;

import models.Device;
import models.DeviceStore;
import org.eclipse.jgit.api.errors.GitAPIException;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static play.libs.Json.toJson;

public class Registration extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    DeviceStore deviceStore;

    public Result index() {
        return ok(index.render());
    }

    public Result addDevice() throws IOException, GitAPIException{
        Device device = formFactory.form(Device.class).bindFromRequest().get();
        deviceStore.save(device);
        return redirect(routes.Registration.index());
    }

    public Result getDevices() throws IOException{
        List<Device> devices = deviceStore.getDevices();
        if(devices != null & !devices.isEmpty()) {
            for(Device device : devices) {
                System.out.println(device.toString());
            }
        }
        System.out.println(toJson(devices).toString());
        return ok(toJson(devices));
    }
}
