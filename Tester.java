package final;

public class Tester
{
    public static long SEED = 8675309;
    public static void main(String[] args) throws InterruptedException
    {

        // construct a simulation object w/ appropriate parameters and then run
        int numCells       = 10;
        int guiCellWidth   = 40;
        double initResourceMean = Parameters.INIT_RESOURCE_MEAN;
        double initResourceSD = Parameters.INIT_RESOURCE_SD;
        double regrowthRateMean = Parameters.REGROWTH_RATE_MEAN;
        double regrowthRateSD = Parameters.REGROWTH_RATE_SD;
        double maxResourceMean = Parameters.MAX_RESOURCE_MEAN;
        double maxResourceSD = Parameters.MAX_RESOURCE_SD;
        int numMacrophages = 3;
        double macSpeed    = 2.0;
        int numBacteria    = 8;
        double bacSpeed    = Parameters.BACT_INTER_MOVE;
        double bacDivRate    = Parameters.BACT_INTER_DIVIDE;
        //double bacDivScale = 2.0;
        double consumptionRateMean = Parameters.CONSUMPTION_RATE_MEAN;
        double consumptionRateSD = Parameters.CONSUMPTION_RATE_SD;
        double maxTime     = 20.0;
        double guiDelayInSecs = .1;
/*
        if(args.length > 0){ // if they provided command-line args, use those instead
          try{
            numCells = Integer.parseInt(args[0]);
            guiCellWidth = Integer.parseInt(args[1]);
            numMacrophages = Integer.parseInt(args[2]);
            macSpeed = Double.parseDouble(args[3]);
            numBacteria = Integer.parseInt(args[4]);
            bacSpeed = Double.parseDouble(args[5]);
            bacDivShape = Integer.parseInt(args[6]);
            bacDivScale = Double.parseDouble(args[7]);
            maxTime = Double.parseDouble(args[8]);
            guiDelayInSecs = Double.parseDouble(args[9]);
            SEED = Long.parseLong(args[10]);
          } catch (Exception e) {
            System.out.println("Usage: java Tester <int numCells> <int guiCellWidth> "
                +"<int numMacrophages> <double macSpeed> <int numBacteria> <double bacSpeed> "
                +"<int bacDivShape> <double bacDivScale> <double maxTime> <double "
                +" guiDelayInSecs> <long seed>");
            return;
          }
        }
*/
        Simulation s = new Simulation(numCells, guiCellWidth, initResourceMean,
            initResourceSD, regrowthRateMean, regrowthRateSD, maxResourceMean, 
            maxResourceSD, numMacrophages, macSpeed, numBacteria, bacSpeed,
            bacDivRate, consumptionRateMean, consumptionRateSD,
            SEED, maxTime);
        s.run(guiDelayInSecs);
    }
}
