public class Cell
{
  private Macro macrophage;
  private Bact bacterium;
  private int row; //perhaps redundant
  private int col;

  public Cell(int row, int col)
  {
    this.row = row;
    this.col = col;
    this.macrophage = null;
    this.bacterium = null;
  }

  public void occupy(Macro m) {macrophage = m; m.setRowCol(row,col); }
  public void occupy(Bact b) { bacterium = b; b.setRowCol(row,col); }

  public Macro removeMacrophage() { macrophage.setRowCol(-1,-1); macrophage = null; }
  public Bact removeBacterium() { bacterium.setRowCol(-1,-1); bacterium = null; }

  public int getRow() { return row; }
  public int getCol() { return col; }
  public Macro getMacrophage() { return(macrophage); }
  public Bact getBacterium() { return(bacterium); }

  public boolean isOccupied() { return(macrophage != null || bacterium  != null); }
  public boolean hasMacrophage() {return(macrophage != null); }
  public boolean hasBacterium() {return(bacterium != null); }

  public String toString() { return("(" + row + "," + col + ")"); }

}
