
# 🤖 Robot Battle Game - Docker SAE 2.03

Bienvenue dans **Robot Battle Game**, un jeu Java multijoueur où deux robots s'affrontent en utilisant des attaques stratégiques ! 🕹️

---

## 📚 Description

**Robot Battle Game** est une **simulation de combat** entre deux joueurs, chacun contrôlant un robot. Chaque robot possède les caractéristiques suivantes :

- **Points de vie (PV)** : la résistance du robot
- **Vitesse** : détermine l'ordre des attaques
- **Attaques pré-définies** : chaque robot peut effectuer plusieurs attaques avec des effets différents

### 🎯 Objectif
L'objectif est de réduire les **PV** de l'adversaire à zéro avant que le vôtre n'atteigne ce seuil.

Les **attaques** infligent des dégâts différents en fonction des caractéristiques de chaque robot, et la **vitesse** détermine qui attaque en premier.

### ⚡ Gameplay
1. Le joueur choisit le nom de son robot.
2. Le jeu se déroule en **tour par tour**, où chaque robot attaque à son tour.
3. La partie se termine lorsqu'un robot perd tous ses **points de vie** ou si un match nul est déclaré.

---

## 🛠️ Structure du Projet

### 🗡️ `Attaque`
La classe **Attaque** modélise une attaque spécifique :
- **Nom**
- **Dégâts max / min**
- **Portée min / max**
- **Précision min / max**
- **Nombre de tirs**
- **Chance de multiplicateur**

```java
public Attaque(String nom, int degatsMax, int degatsMin, int portee, int porteeMax,
               int precisionMax, int precisionMin, int nbTirs, int chanceMultiplicateur)
```

### 🤖 `Robot`
La classe **Robot** modélise un robot :
- **Nom**
- **PV**
- **Vitesse**
- **Liste d'attaques**
- **Déplacement** (optionnel)

```java
public Robot(String nom, int pv, int vitesse)
public Robot(String nom, int pv, int vitesse, int deplacement)
```

Méthodes associées :
```java
public ArrayList<Attaque> getAttaques()
public Attaque getAttaque(int index)
public int getPv()
public int getVit()
public String getNom()
public int getPvMax()
public void addAttaque(Attaque attaque)
public boolean infligerAttaque(Attaque attaque, Robot ennemi)
```

### 🧍 `Joueur`
Modélise un joueur avec un nom et un robot associé.

### 🎮 `Controleur`
Classe principale qui gère le déroulement du jeu :
- Création des joueurs
- Tour par tour
- Gestion de la fin du jeu

### 🖧 `Serveur`
- Stocke les deux clients
- Gère la communication réseau (via sockets)
- Hébergé via Docker

---

## 🖧 Architecture Réseau et Déploiement Docker

Le jeu fonctionne selon une **architecture client-serveur** :

- **Serveur** :
  - Centralise la logique de jeu
  - Coordonne les tours et transmet les actions
- **Clients (2 joueurs)** :
  - Envoient les actions (choix, attaques)
  - Reçoivent les mises à jour du jeu

### 🐳 Déploiement avec Docker

Le serveur est hébergé dans un conteneur Docker.

```bash
docker build -t robot .
docker run -p 9000:9000 robot    #Dans le cas ou on utilise le terminal / un serveur distant
```

> Clients connectés via l’IP du serveur Docker (`localhost` ou par exemple)

---

## 🐳 Dockerfile – Serveur Java

```dockerfile
FROM debian:latest

WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean

COPY ./ .

RUN javac -encoding UTF-8 -d class @Compile.list

EXPOSE 9000

ENTRYPOINT ["java", "-cp", "class", "Controleur"]
```

---

## 🚀 Lancer le Jeu

### 🔧 Compilation
```bash
javac *.java
```

### ▶️ Exécution
```bash
java Client
```

---

## 🧑‍💻 Auteurs

- Damestoy Ethan – [GitHub](https://github.com/Ethylaa)
- Leclerc Jonathan – [GitHub](https://github.com/Nailledo)
- Millereux Bienvault William – [GitHub](https://github.com/Falcrom37) 
- Leprevost Lucas – [GitHub](https://github.com/LucasLeprevost)
-   
-- 
*Projet réalisé dans le cadre de la SAE 2.03 à l’IUT du Havre — BUT Informatique 1ᵉ année.*
