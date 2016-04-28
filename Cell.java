public class Cell
{
  private Macro macrophage;
  private Bact bacterium;
  private int row; //perhaps redundant
  private int col;
  public double resource;
  private double growthRate;
  private double maxResource;
  public double timeLastDepleted;

  public Cell(int row, int col, double res, double rate, double max)
  {
    this.row = row;
    this.col = col;
    this.macrophage = null;
    this.bacterium = null;
    this.resource = res;
    this.growthRate = rate;
    this.maxResource = max;
  }

  public void occupy(Agent a){ 
      if(a instanceof Macro)
          occupy((Macro)a);
      if(a instanceof Bact)
          occupy((Bact)a);
  }
  public void occupy(Macro m) {macrophage = m; m.setRowCol(row,col); }
  public void occupy(Bact b) { bacterium = b; b.setRowCol(row,col); }

  public Macro removeMacrophage() { macrophage.setRowCol(-1,-1); Macro tmp = macrophage; macrophage = null; return(tmp); }
  public Bact removeBacterium() { bacterium.setRowCol(-1,-1); Bact tmp = bacterium; bacterium = null; return(tmp); }

  public int getRow() { return row; }
  public int getCol() { return col; }
  public Macro getMacrophage() { return(macrophage); }
  public Bact getBacterium() { return(bacterium); }

  public boolean isOccupied() { return(macrophage != null || bacterium  != null); }
  public boolean hasMacrophage() {return(macrophage != null); }
  public boolean hasBacterium() {return(bacterium != null); }

  public double getResource(){ return resource; }
  public void setResource(double res) { resource = res; }

  public double getRate(){ return growthRate; }

  public double getTimeLastDepleted(){ return timeLastDepleted; }
  public void setTimeLastDepleted(double t) { timeLastDepleted = t; }


  public String toString() { return("(" + row + "," + col + ")"); }

}
