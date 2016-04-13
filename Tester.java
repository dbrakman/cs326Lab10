public class Tester
{
    public static long SEED = 8675309;
    public static void main(String[] args) throws InterruptedException
    {

        // construct a simulation object w/ appropriate parameters and then run
        int numCells       = 10;
        int guiCellWidth   = 40;
        int numMacrophages = 3;
        int numBacteria    = 8;
        double maxTime     = 20.0;
        double guiDelayInSecs = .5;

        if(args.length > 0){ // if they provided command-line args, use those instead
          try{
            numCells = Integer.parseInt(args[0]);
            guiCellWidth = Integer.parseInt(args[1]);
            numMacrophages = Integer.parseInt(args[2]);
            numBacteria = Integer.parseInt(args[3]);
            maxTime = Double.parseDouble(args[4]);
            guiDelayInSecs = Double.parseDouble(args[5]);
            SEED = Long.parseLong(args[6]);
          } catch (Exception e) {
            System.out.println("Usage: java Tester <int numCells> <int guiCellWidth> "
                +"<int numMacrophages> <int numBacteria> <double maxTime> <double "
                +" guiDelayInSecs> <long seed>");
            return;
          }
        }

        Simulation s = new Simulation(numCells, guiCellWidth,
                                      numMacrophages, numBacteria, SEED, maxTime);
        s.run(guiDelayInSecs);
    }
}
