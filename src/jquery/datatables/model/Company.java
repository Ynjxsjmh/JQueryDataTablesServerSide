package jquery.datatables.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Company {

    static int nextID = 17;

    public Company(String name, String address, String town) {
        id = nextID++;
        this.name = name;
        this.address = address;
        this.town = town;
    }

    private int id;
    private String name;
    private String address;
    private String town;
}
