
public class Parameters {
    protected static double CONSUMPTION_RATE_MEAN   = 1.0; //normal
    protected static double CONSUMPTION_RATE_SD     = 0.2;
    
    protected static double INIT_RESOURCE_MEAN      = 10.0;//normal
    protected static double INIT_RESOURCE_SD        = 0.5;
    
    protected static double BACT_INTER_MOVE         = 1.0;//exp
    protected static double BACT_INTER_DIVIDE       = 5.0;//exp
    
    protected static double MACRO_INTER_MOVE        = 3.0;//exp
    protected static double MACRO_INTER_DIVIDE      = 10.0;
    
    protected static int    MIN_BACT_TO_DIVIDE      = 3; //mac only divides if
    //it has 5+ neighboring bacteria
    
    protected static double REGROWTH_RATE_MEAN      = 1.0;
    protected static double REGROWTH_RATE_SD        = 0.1;
            
    protected static double MAX_RESOURCE_MEAN       = 1.0;
    protected static double MAX_RESOURCE_SD         = 0.25;
}
