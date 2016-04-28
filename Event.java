package finals; //Kluge; declared in same package as Tester so that Tester can access these fields


public class Event
{
    public Agent owner;
    public double time;

    public enum EventType { MOVE, EAT, DIVIDE, STARVE, UNDEF };

    public EventType type;

    public Event(){
        owner = null;
        time = Double.MAX_VALUE;
        type = EventType.UNDEF;
    }

    public Event(Agent owner, double time, EventType type){
        this.owner = owner;
        this.time = time;
        this.type = type;
    }
}
