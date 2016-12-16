package models;


public class Device {

    public String name;
    public String uuid;

    public Device() {
    }

    public Device(String uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString(){
        return uuid + "\t" + name;
    }

    @Override
    public boolean equals(Object o){
        return this.uuid.equalsIgnoreCase(((Device) o ).getUuid());
    }
}
