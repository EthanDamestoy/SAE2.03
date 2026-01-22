import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Joueur
{
    // Couleurs ANSI
    public static final String RESET        = "\u001B[0m";
    public static final String VERT         = "\u001B[32m";
    public static final String CYAN         = "\u001B[36m";
    public static final String JAUNE        = "\u001B[33m";
    public static final String ROUGE        = "\u001B[31m";
    public static final String MAGENTA      = "\u001B[35m";
    public static final String BLANC        = "\u001B[37m";
    public static final String GRAS         = "\u001B[1m";
    public static final String SOULIGNE     = "\u001B[4m";

    private String          nom;
    private Robot           robot;
    private Controleur      ctrl;

    private Socket          socket;
    private PrintWriter     writer;
    private BufferedReader  reader;

    public Joueur(String nom, Controleur ctrl, Socket socket, PrintWriter writer, BufferedReader reader)
    {
        this.nom    = nom;
        this.ctrl   = ctrl;
        this.socket = socket;
        this.writer = writer;
        this.reader = reader;

        this.robot  = null;
    }

    public String toString()
    {
        return VERT + SOULIGNE + "Joueur :" + RESET + "\n" +
               " " + CYAN + "- Nom: " + RESET + ROUGE + GRAS + this.nom + RESET + "\n" + 
               this.robot;
    }

    /* ---------------------- */
    /*         Getteurs        */
    /* ---------------------- */

    public Robot getRobotJoueur    () { return this.robot ; }
    public String getNom           () { return this.nom   ; }

    public Socket getSocket        () { return this.socket; }

    public PrintWriter getWriter   () { return this.writer; }

    public BufferedReader getReader() { return this.reader; }

    /* ---------------------- */
    /*  Choix Robot           */
    /* ---------------------- */
    public void choixRobot(String input)
    {

        int index = ctrl.getEnsRobot().stream()
                        .map(Robot::getNom)
                        .map(String::toLowerCase)
                        .toList()
                        .indexOf(input.toLowerCase());

        if (index != -1)
        {
            this.robot = ctrl.getRobot(index);
            System.out.println(CYAN + "Vous avez choisi le robot : " + RESET + this.robot.getNom());
        }
        else
        {
            System.out.println(ROUGE + "Robot introuvable. Un robot par défaut sera attribué." + RESET);
            if (!ctrl.getEnsRobot().isEmpty())
                this.robot = ctrl.getRobot(0);
        }
    }
}
