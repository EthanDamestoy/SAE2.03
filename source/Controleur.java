import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Controleur 
{   
    // Constante de victoire / défaite
    private static final String CADRE_VICTOIRE = 
        "╔══════════════════════════════════╗\n" +
        "║                                  ║\n" +
        "║           VICTOIRE !!!           ║\n" +
        "║                                  ║\n" +
        "╚══════════════════════════════════╝";

    private static final String CADRE_DEFAITE = 
        "╔══════════════════════════════════╗\n" +
        "║                                  ║\n" +
        "║           DÉFAITE...             ║\n" +
        "║                                  ║\n" +
        "╚══════════════════════════════════╝";

    // Couleur 
    public static final String RESET        = "\u001B[0m" ;
    public static final String ROUGE        = "\u001B[31m";
    public static final String VERT         = "\u001B[32m";
    public static final String JAUNE        = "\u001B[33m";
    public static final String BLEU         = "\u001B[34m";
    public static final String CYAN         = "\u001B[36m";
    public static final String BLEU_CLAIR   = "\u001B[94m";
    public static final String GRAS         = "\u001B[1m" ;  
    public static final String SOULIGNE     = "\u001B[4m" ;  

    // Port du serveur
    private static final int PORT = 9000;

    // Attributs
    private ArrayList<Attaque>  ensAttaque;
    private ArrayList<Robot>    ensRobot;
    private ServerSocket        serverSocket;

    private Joueur              j1;
    private Joueur              j2;

    public Controleur()
    {
        this.ensAttaque    = new ArrayList<>();
        this.ensRobot      = new ArrayList<>();
        this.j1            = null;
        this.j2            = null;

        this.initRobot();
        this.initAttaque();

        startServer();
    }

    /**
     * Démarrage du serveur
     */
    private void startServer() 
    {
        try 
        {
            serverSocket = new ServerSocket(PORT);
            System.out.println(VERT + "Serveur démarré sur le port " + PORT + RESET);

            System.out.println(JAUNE + "En attente de joueurs (2 nécessaires pour commencer)..." + RESET);
            System.out.println(JAUNE + "En attente du premier joueur..." + RESET);
            Socket socketJ1 = serverSocket.accept();
            this.j1         = connecterJoueur(socketJ1, "Joueur 1");

            System.out.println(JAUNE + "Premier joueur connecté! En attente du second joueur..." + RESET);
            Socket socketJ2 = serverSocket.accept();
            this.j2         = connecterJoueur(socketJ2, "Joueur 2");

            System.out.println(VERT + "Deux joueurs connectés! Démarrage de la partie." + RESET);

            this.j1.getRobotJoueur().setPosition(-500);
            this.j2.getRobotJoueur().setPosition(500);

            jouerPartie(this.j1, this.j2);
            finaliserPartie(this.j1, this.j2);

        } 
        catch (IOException e) 
        {
            System.out.println(ROUGE + "Erreur serveur: " + e.getMessage() + RESET);
        } 
        finally 
        {
            try 
            {
                if (serverSocket != null && !serverSocket.isClosed())
                    serverSocket.close();
            } 
            catch (IOException e) 
            {
                System.out.println(ROUGE + "Erreur lors de la fermeture du serveur: " + e.getMessage() + RESET);
            }
        }
    }
    
    /**
     * Connecte un joueur et configure son robot
     */
    private Joueur connecterJoueur(Socket socket, String defaultName) throws IOException 
    {
        PrintWriter    out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("Bienvenue au jeu de combat de robots!");
        out.println("Veuillez entrer votre nom:");

        String nomJoueur = in.readLine();
        if (nomJoueur == null || nomJoueur.trim().isEmpty()) 
            nomJoueur = defaultName;

        Joueur joueur = new Joueur(nomJoueur, this, socket, out, in);
        out.println("Voici les robots disponibles:");
        out.println(getRobotsAvailableAsString());
        out.println("Choisissez un robot (entrez le nom complet sans fautes) :");

        String choixRobot = in.readLine();
        joueur.choixRobot(choixRobot);

        out.println("Vous avez choisi le robot: " + joueur.getRobotJoueur().getNom());
        this.removeRobot(joueur.getRobotJoueur().getNom());
        out.println("En attente d'un adversaire...");

        return joueur;
    }
    
    /**
     * Gère le déroulement d'une partie entre deux joueurs
     */
    private void jouerPartie(Joueur j1, Joueur j2) 
    {
        PrintWriter out1 = j1.getWriter();
        PrintWriter out2 = j2.getWriter();
    
        out1.println("La partie commence " + j2.getNom());
        out2.println("La partie commence " + j1.getNom());
    
        int tours = 1;
        boolean j2Joue = j1.getRobotJoueur().getVit() < j2.getRobotJoueur().getVit();
    
        try 
        {
            while (j1.getRobotJoueur().getPv() > 0 && j2.getRobotJoueur().getPv() > 0) 
            {
                String infoTour = JAUNE + GRAS + "\n********     Tour : " + tours + "     *******\n" + RESET;
                
                out1.println(infoTour +
                             VERT + SOULIGNE + "Votre Robot   :" + RESET + "\n" + j1.getRobotJoueur() + "\n" +
                             VERT + SOULIGNE + "Robot Ennemie :" + RESET + "\n" + j2.getRobotJoueur()         );

                out2.println(infoTour +
                             VERT + SOULIGNE + "Votre Robot   :" + RESET + "\n" + j2.getRobotJoueur() + "\n" +
                             VERT + SOULIGNE + "Robot Ennemie :" + RESET + "\n" + j1.getRobotJoueur()         );
    
                // ➔ Un seul joueur joue par tour
                if (j2Joue) processAttack(j2, j1);
                else        processAttack(j1, j2);
    
                // ➔ Après un coup, on passe la main
                j2Joue = !j2Joue;
    
                // ➔ Tour suivant
                tours++;
            }
    
            if (j1.getRobotJoueur().getPv() <= 0)
            {
                afficherVictoire(j2);
                afficherDefaite(j1);
            }
            else
            {
                afficherVictoire(j1);
                afficherDefaite(j2);
            }
    
        } 
        catch (IOException e) 
        {
            System.out.println(ROUGE + "Erreur lors de la partie: " + e.getMessage() + RESET);
            out1.println("Une erreur est survenue pendant la partie.");
            out2.println("Une erreur est survenue pendant la partie.");
        }
    }
    

    private void afficherVictoire(Joueur joueur) 
    {
        joueur.getWriter().println(VERT + CADRE_VICTOIRE + RESET);
    }

    private void afficherDefaite(Joueur joueur) 
    {
        joueur.getWriter().println(ROUGE + CADRE_DEFAITE + RESET);
    }
    
    private void afficherDegat(Joueur jAtt, Joueur jDef, Attaque attaque, int degatsTotal)
    {
        jAtt.getWriter().println(
            String.format("%-15s", jAtt.getNom()) + " attaque avec : " + BLEU_CLAIR + attaque.getNom() + RESET + "\n" +
            String.format("%-15s", jDef.getNom()) + " subit : " + BLEU_CLAIR + degatsTotal + " dégâts " + RESET + "\n" +
            "PV restants pour " + jDef.getNom() + ": " + BLEU_CLAIR + jDef.getRobotJoueur().getPv() + RESET + "\n"
        );

        jDef.getWriter().println(
            String.format("%-15s", jAtt.getNom()) + " attaque avec : " + BLEU_CLAIR + attaque.getNom() + RESET + "\n" +
            String.format("%-15s", jDef.getNom()) + " subit : " + BLEU_CLAIR + degatsTotal + " dégâts " + RESET + "\n" +
            "PV restants pour " + jDef.getNom() + ": " + BLEU_CLAIR + jDef.getRobotJoueur().getPv() + RESET + "\n"
        );
    }



    private void afficherPositions(Joueur j1, Joueur j2)
    {
        PrintWriter out1 = j1.getWriter();
        PrintWriter out2 = j2.getWriter();

        Robot r1 = j1.getRobotJoueur();
        Robot r2 = j2.getRobotJoueur();

        int min     = -2000;
        int max     = 2000;
        int largeur = 80; // Largeur totale de la barre

        int pos1 = r1.getPosition();
        int pos2 = r2.getPosition();

        // Positions relatives 0 --> largeur-1
        int indexJ1 = (int) ((pos1 - min) * 1.0 / (max - min) * (largeur - 1));
        int indexJ2 = (int) ((pos2 - min) * 1.0 / (max - min) * (largeur - 1));

        // Barre vide
        char[] barre = new char[largeur];
        for (int i = 0; i < largeur; i++) barre[i] = '-';

        // Afficher J1 et J2 ou X si même place
        if (indexJ1 == indexJ2)
            barre[indexJ1] = 'X'; // Collision
        else {
            barre[indexJ1] = '1';
            barre[indexJ2] = '2';
        }

        // Préparer repère fixe
        StringBuilder repere = new StringBuilder();
        for (int i = 0; i < largeur; i++) 
        {
            if (i == 0)
                repere.append("-2000");
            else if (i == largeur / 2 - 7)
                repere.append("  0 ");
            else if (i == largeur - 10)
                repere.append("2000");
            else
                repere.append(" ");
        }

        // Infos sur les positions
        String infosPositions = CYAN +
            "Position de " + j1.getNom() + "(n°1) : " + r1.getPosition() + "\n" +
            "Position de " + j2.getNom() + "(n°2) : " + r2.getPosition() + "\n" +
            "Distance actuelle entre eux : " + calculerDistance(r1, r2) + RESET;

        // Afficher avec identifiant
        out1.println(infosPositions);
        out1.println(new String(barre));

        out2.println(infosPositions);
        out2.println(new String(barre));
    }

    /**
     * Traite une attaque d'un joueur vers un autre
     */
    private void processAttack(Joueur attacker, Joueur defender) throws IOException 
    {
        afficherPositions(attacker, defender);
    
        attacker.getWriter().println("C'est votre tour d'attaquer! Choisissez une attaque (0-" + (attacker.getRobotJoueur().getAttaques().size() - 1) + "):");
    
        try 
        {
            String choixAttaque = attacker.getReader().readLine();
            int indexAttaque = Integer.parseInt(choixAttaque);
    
            Attaque attaque = attacker.getRobotJoueur().getAttaque(indexAttaque);
    
            if (attaque.getNom().equals("Déplacer")) 
            {
                Robot robot = attacker.getRobotJoueur();
                int maxDep = robot.getDeplacement();
    
                attacker.getWriter().println("Choisissez une distance de déplacement (par pas de 25, entre -" + maxDep + " et +" + maxDep + ", sans 0):");
    
                String choixDep = attacker.getReader().readLine();
                int deplacement = Integer.parseInt(choixDep);
    
                if (Math.abs(deplacement) > maxDep || deplacement % 25 != 0 || deplacement == 0) 
                {
                    attacker.getWriter().println("INVALID_MOVE");
                    processAttack(attacker, defender);
                    return;
                }
    
                robot.setPosition(robot.getPosition() + deplacement);
                attacker.getWriter().println(VERT + "Vous vous êtes déplacé de " + deplacement + " unités." + RESET);
    
                // Aucun appel à afficherDegat ici pour un déplacement !
                return;
            }
    
            // Sinon, attaque normale
            int distance = calculerDistance(attacker.getRobotJoueur(), defender.getRobotJoueur());
            attacker.getWriter().println("Distance actuelle entre vous et l'adversaire : " + distance);
    
            int totalDegats = attacker.getRobotJoueur().infligerAttaqueDistance(attaque, defender.getRobotJoueur(), distance);
    
            if (totalDegats > 0) 
            {
                attacker.getWriter().println("L'attaque à été effectué avec succès !");
            } 
            else 
            {
                attacker.getWriter().println("L'attaque à échoué !");
            }
    
            afficherDegat(attacker, defender, attaque, totalDegats);
    
        } 
        catch (NumberFormatException | IndexOutOfBoundsException e) 
        {
            attacker.getWriter().println("INVALID_CHOICE");
            processAttack(attacker, defender);
        }
    }
    

    /**
     * Finalise la partie en fermant les connexions
     */
    private void finaliserPartie(Joueur j1, Joueur j2) 
    {
        try 
        {
            // Fermer les connexions du premier joueur
            j1.getWriter().close();
            j1.getReader().close();
            j1.getSocket().close();
            
            // Fermer les connexions du deuxième joueur
            j2.getWriter().close();
            j2.getReader().close();
            j2.getSocket().close();
            
            System.out.println(JAUNE + "Partie terminée, connexions fermées" + RESET);
        } 
        catch (IOException e)
        {
            System.out.println(ROUGE + "Erreur lors de la fermeture des connexions: " + e.getMessage() + RESET);
        }
    }
    
    /**
     * Renvoie la liste des robots au format String
     */
    public String getRobotsAvailableAsString() 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ensRobot.size(); i++) 
        {
            sb.append(ensRobot.get(i)).append("\n");
        }
        return sb.toString();
    }

    public void removeRobot(String nom) 
    {
        for (int i = 0; i < ensRobot.size(); i++) 
        {
            if (ensRobot.get(i).getNom().equals(nom)) 
            {
                ensRobot.remove(i);
                break;
            }
        }
    }

    /* ---------------------- */
    /*   Init  Robot/Attaque  */
    /* ---------------------- */

    private void initRobot() 
    {
        try (Scanner sc = new Scanner(new FileInputStream("data/robot.data"), "UTF8")) 
        {
            while (sc.hasNextLine()) 
            {
                String ligneRobot = sc.nextLine();
                String nom = ligneRobot.substring(0, 18).trim();
                int pv = Integer.parseInt(ligneRobot.substring(18, 24).trim());
                int vitesse = Integer.parseInt(ligneRobot.substring(24, 28).trim());
                int deplacement = Integer.parseInt(ligneRobot.substring(28).trim());

                this.ensRobot.add(new Robot(nom, pv, vitesse, deplacement));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }


    private void initAttaque() 
    {
        try (Scanner sc = new Scanner(new FileInputStream("data/attaque.data"), "UTF8")) 
        {
            while (sc.hasNextLine()) 
            {
                String ligne = sc.nextLine();
                if (ligne.trim().isEmpty()) continue;

                String[] parts = ligne.trim().split("\\s+"); // découpe par ESPACES multiples

                if (parts.length < 10)
                {
                    System.out.println("Ligne mal formatée: " + ligne);
                    continue;
                }

                // Reconstituer le nom
                StringBuilder nomBuilder = new StringBuilder();
                for (int i = 0; i < parts.length - 9; i++) // tout sauf les 9 derniers chiffres
                {
                    nomBuilder.append(parts[i]);
                    if (i != parts.length - 10) nomBuilder.append(" ");
                }
                String nom = nomBuilder.toString();

                int degatMax     = Integer.parseInt(parts[parts.length-9]);
                int degatMin     = Integer.parseInt(parts[parts.length-8]);
                int portee1      = Integer.parseInt(parts[parts.length-7]);
                int porteeMax    = Integer.parseInt(parts[parts.length-6]);
                int precisionMax = Integer.parseInt(parts[parts.length-5]);
                int precisionMin = Integer.parseInt(parts[parts.length-4]);
                int nbFois       = Integer.parseInt(parts[parts.length-3]);
                int chanceMulti  = Integer.parseInt(parts[parts.length-2]);
                int indiceRobot  = Integer.parseInt(parts[parts.length-1]);

                Attaque attaque = new Attaque(nom, degatMax, degatMin, portee1, porteeMax, precisionMax, precisionMin, nbFois, chanceMulti);

                this.ensAttaque.add(attaque);

                if (indiceRobot >= 0 && indiceRobot < this.ensRobot.size())
                {
                    getRobot(indiceRobot).getAttaques().add(attaque);
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // Ajoute "Déplacer"
        for (Robot r : this.ensRobot)
        {
            r.addAttaque(new Attaque("Déplacer", 0, 0, 0, 0, 100, 100, 1, 0));
        }
    }

    
    /* ---------------------- */
    /*         Getteur        */
    /* ---------------------- */
    public Robot getRobot(int index) { return this.ensRobot.get(index); }
    public ArrayList<Robot> getEnsRobot() { return this.ensRobot; }

    public static int calculerDistance(Robot r1, Robot r2) 
    {
        return Math.abs(r1.getPosition() - r2.getPosition());
    }

    public static void main(String[] args) 
    {
        while (true) new Controleur();
    }
}