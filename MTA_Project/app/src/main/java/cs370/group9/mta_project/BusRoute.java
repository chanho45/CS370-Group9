package cs370.group9.mta_project;

public class BusRoute {
    private final String id;
    private int color;
    private String name;
    private String routeNumber;
    private String description;

    public BusRoute(String id) {
        this.id = id;
    }

    // Getter & Setter
    // id
    public String getId() { return id; }
    // color
    public void setColor(int color) { this.color = color; }
    public int getColor() { return color; }
    // name
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    // routeName
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public String getRouteNumber() { return routeNumber; }
    // description
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    // MTA Bus Request
}
