package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public String id;

    public String name;
    public String uuid;


    @Override
    public String toString(){
        return uuid + "\t" + name;
    }
}
