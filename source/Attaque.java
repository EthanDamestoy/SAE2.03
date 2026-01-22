public class Attaque
{
    private String nom;

    private int degatMax;
    private int degatMin;
    private int portee;
    private int porteeMax;
    private int precisionMax;
    private int precisionMin;
    private int nbTirs;
    private int chanceMulti;

    // Constructeur
    public Attaque(String nom, int degatMax, int degatMin, int portee, int porteeMax, 
                   int precisionMax, int precisionMin, int nbTirs, int chanceMulti    )
    {
        this.nom           = nom;
        this.degatMax      = degatMax;
        this.degatMin      = degatMin;
        this.portee        = portee;
        this.porteeMax     = porteeMax;
        this.precisionMax  = precisionMax;
        this.precisionMin  = precisionMin;
        this.nbTirs        = nbTirs;
        this.chanceMulti   = chanceMulti;
    }

    /* ---------------------- */
    /*         Getteurs        */
    /* ---------------------- */
    public String getNom      () { return this.nom         ; }
    public int getDegatMax    () { return this.degatMax    ; }
    public int getDegatMin    () { return this.degatMin    ; }
    public int getPortee      () { return this.portee      ; }
    public int getPorteeMax   () { return this.porteeMax   ; }
    public int getPrecisionMax() { return this.precisionMax; }
    public int getPrecisionMin() { return this.precisionMin; }
    public int getNbTirs      () { return nbTirs           ; }
    public int getChanceMulti () { return chanceMulti      ; }

    /* ---------------------- */
    /*    Affichage / Debug    */
    /* ---------------------- */

    public String toString()
    {
        final String RESET      = "\u001B[0m";
        final String TITRE      = "\u001B[1;36m";  // Cyan clair gras
        final String VAL        = "\u001B[1;37m";  // Blanc gras
        final String SYSTEM     = "\u001B[38;5;244m";  // Gris tech custom

        StringBuilder sb = new StringBuilder();

        sb.append(TITRE).append("Attaque: ").append(VAL).append(this.nom).append(RESET).append("\n");

        String ligne1 = SYSTEM + String.format("%-10s", "Portée : ")
                    + VAL + String.format("%-5d", this.portee) + " km"
                    + SYSTEM + " -> "
                    + VAL + String.format("%-4d", this.degatMax) + " dmg"
                    + SYSTEM + " - "
                    + VAL + String.format("%-3d", this.precisionMax) + "% hit chance"
                    + RESET;

        String ligne2 = SYSTEM + String.format("%-10s", "Portée : ")
                    + VAL + String.format("%-5d", this.porteeMax) + " km"
                    + SYSTEM + " -> "
                    + VAL + String.format("%-4d", this.degatMin) + " dmg"
                    + SYSTEM + " - "
                    + VAL + String.format("%-3d", this.precisionMin) + "% hit chance"
                    + RESET;

        sb.append(ligne1).append("\n");

        // N'affiche la 2e ligne que si elle est différente de la 1re
        if (!ligne1.equals(ligne2)) {
            sb.append(ligne2).append("\n");
        }

        if (this.nbTirs > 1)
        {
            sb.append(SYSTEM)
              .append(String.format("%-10s", "Tirs   : "))
              .append(VAL).append(String.format("%-8d", this.nbTirs))
              .append(SYSTEM).append(" -> ")
              .append(String.format("%-15s", "MultiChance : "))
              .append(VAL).append(this.chanceMulti).append("%")
              .append(RESET).append("\n");
        }

        return sb.toString();
    }

    


}
