package controllers;

import models.Device;
import play.data.FormFactory;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import javax.inject.Inject;
import java.util.List;

import static play.libs.Json.toJson;

public class Registration extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index() {
        return ok(index.render());
    }

    @Transactional
    public Result addDevice() {
        Device device = formFactory.form(Device.class).bindFromRequest().get();
        JPA.em().persist(device);
        return redirect(routes.Registration.index());
    }

    @Transactional(readOnly = true)
    public Result getDevices() {
        List<Device> devices = (List<Device>) JPA.em().createQuery("select d from Device d").getResultList();
        if(devices != null & !devices.isEmpty()) {
            for(Device device : devices) {
                System.out.println(device.toString());
            }
        }
        System.out.println(toJson(devices).toString());
        return ok(toJson(devices));
    }
}
