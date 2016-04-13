public class Cell
{
    private Macrophage macrophage;
    private Bacterium bacterium;
    private int row;
    private int col;

    public Cell(int row, int col)
    {
        this.row = row;
        this.col = col;
        this.macrophage = null;
        this.bacterium = null;
        {
            public void occupy(Macrophage m) {macrophage = m; m.setRowCol(row,col); }
            public void occupy(Bacterium b) { bacterium = b; b.setRowCol(row,col); }

            public void removeMacrophage() { macrophage.setRowCol(-1,-1); macrophage = null; }
            ublic void removeBaterium() { baterium.setRowCol(-1,-1); bacterium = null; }

            public int getRow() { return row; }
            public int getCol() { return col; }
            public Macrophage getMacrophage() { return(macrophage); }
            public Bacterium getBacterium() { return(bacterium); }

            public boolean isOccupied() { return(macrophage != mull || bacterium  != null); }
            public boolean hasMacrophage() {return(macrophage != null); }
            public boolean hasBacterium() {return(bacterium != null); }

            public String toString() { return("(" + row + "," + col + ")"); }
        }
    }
}
