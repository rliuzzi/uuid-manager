package models;

import Util.GitProject;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rominaliuzzi on 15/12/2016.
 */
public class DeviceStore {

    HashMap<String,Device> devicesStore = new HashMap<String,Device>();
    public static final String HEADER = "Device ID\tDevice Name";


    @Inject
    GitProject gitProject;

    String context = "zone";
    public static final String SUFFIX = "-uuids.txt";
    File file;

    public void reload() throws IOException {
        devicesStore = new HashMap<String,Device>();
        List<String> lines = Util.Util.read(file);
        for (String line : lines) {
            if(!HEADER.equalsIgnoreCase(line)) {
                String[] words = line.split("\t");
                if(words.length > 1) {
                    Device device = new Device(words[0], words[1]);
                    devicesStore.put(device.getUuid(), device);
                    System.out.println(file.getName()+ ": " + device.toString());
                }
            }
        }
    }

    public void save(Device device) throws IOException, GitAPIException{
        List<Device> devices = getDevices();
        if(!devices.contains(device)){
            device.setName("Device" + (devices.size()+1));
            devices.add(device);
        }
        List<String> content = transform(devices);
        Util.Util.write(HEADER, content, file);
        gitProject.addAllCommitPush("added UUID:" + device.getUuid() + " to: " + context + SUFFIX);
    }

    public List<Device> getDevices () throws IOException{
        List<Device> devices = new ArrayList<>();
        reload();
        for(Map.Entry<String, Device> entry : devicesStore.entrySet()){
            devices.add(entry.getValue());
        }
        return devices;
    }

    public List<String> transform (List<Device> devices){
        List<String> lines = new ArrayList<>();
        for(Device device : devices){
            lines.add(device.toString());
        }
        return lines;
    }

    public void init(String context) {
        this.context = context;
        System.out.println("CONTEXT: " + context);
        this.file = new File(GitProject.ROOT_DIR + "/" + context + SUFFIX);
        System.out.println("FILE: " + file.getName());
    }


}
