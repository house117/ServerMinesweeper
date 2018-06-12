/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverminesweeper;

import buscaminasobjects.BuscaminasMp;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import objects.Cliente;
import objects.Equipo;
import objects.Jugador;
import objects.Semaforo;

/**
 *
 * @author House
 */
public class ServerMinesweeper {
    private static ServerSocket server;
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        server = new ServerSocket(1235);
        //servidor pone el juego porque es bien chido jajaja
        BuscaminasMp buscaminas = new BuscaminasMp(16, 16, 51);
        Semaforo semaforo = new Semaforo(true, false);
        ObjectOutputStream writerRojo;
        ObjectOutputStream writerAzul;
        ObjectInputStream readerRojo;
        ObjectInputStream readerAzul;
        Jugador rojo;
        Jugador azul;
        //JUGADOR ROJO
        Socket playerRojo = server.accept();
        readerRojo = new ObjectInputStream(playerRojo.getInputStream());
        rojo = new Jugador((String) readerRojo.readObject(), Equipo.EquipoRojo);
        writerRojo = new ObjectOutputStream(playerRojo.getOutputStream());
        writerRojo.writeObject(rojo);
        writerRojo.writeObject(true);
        writerRojo.writeObject(buscaminas);
        
        System.out.println("Se conecto rojo y todo el pedo");
        Thread.sleep(1000);
        //JUGADOR AZUL
        Socket playerAzul = server.accept();
        readerAzul = new ObjectInputStream(playerAzul.getInputStream());
        azul = new Jugador((String)readerAzul.readObject(), Equipo.EquipoAzul);
        writerAzul = new ObjectOutputStream(playerAzul.getOutputStream());
        writerAzul.writeObject(azul);
        writerAzul.writeObject(false);
        writerAzul.writeObject(buscaminas);
        System.out.println("Se conecto azul y todo el pedo");
        
        
        
        
        Cliente jugadorRojo = new Cliente(readerRojo, writerAzul, rojo, semaforo);
        Cliente jugadorAzul = new Cliente(readerAzul, writerRojo, azul, semaforo);
        System.out.println("se crearon los procesos");
        jugadorRojo.start();
        jugadorAzul.start();
        System.out.println("Iniciaron los procesos, que chingon");
    }
    
}
