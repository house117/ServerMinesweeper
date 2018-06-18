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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.Cliente;
import objects.Equipo;
import objects.Jugador;
import objects.Reiniciador;
import objects.Semaforo;
import objectsServerChat.ClienteChat;
import objectsServerChat.ColaMensajes;
import objectsServerChat.Organizador;


/**
 *
 * @author House
 */
public class ServerMinesweeper {

    private static ServerSocket server;
    private static ServerSocket canalNewGame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        server = new ServerSocket(1235);
        canalNewGame = new ServerSocket(1236);
        while (true) {
            
            //COSAS DEL CHAT!!!
            ArrayList<ClienteChat> clientes = new ArrayList<>();
            ColaMensajes cola = new ColaMensajes();
            Organizador organizador = new Organizador(clientes, cola);
            organizador.start();
            //servidor pone el juego porque es bien chido jajaja
            //16, 16, 51

            BuscaminasMp buscaminas = new BuscaminasMp(16, 16, 51);
            Semaforo semaforo = new Semaforo(true, false);
            ObjectOutputStream writerRojo;
            ObjectOutputStream writerAzul;
            ObjectInputStream readerRojo;
            ObjectInputStream readerAzul;
            ObjectOutputStream writerNewGameRojo;
            ObjectOutputStream writerNewGameAzul;
            ObjectInputStream readerNewGameRojo;
            ObjectInputStream readerNewGameAzul;
            Jugador rojo;
            Jugador azul;
            //JUGADOR ROJO
            iniciarUnChat(clientes, cola, organizador, canalNewGame);
            Socket playerRojo = server.accept();
            Socket playerRojoReinicio = server.accept();
            writerNewGameRojo = new ObjectOutputStream(playerRojoReinicio.getOutputStream());
            readerNewGameRojo = new ObjectInputStream(playerRojoReinicio.getInputStream());
            readerRojo = new ObjectInputStream(playerRojo.getInputStream());
            rojo = new Jugador((String) readerRojo.readObject(), Equipo.EquipoRojo);
            writerRojo = new ObjectOutputStream(playerRojo.getOutputStream());
            writerRojo.writeObject(rojo);
            writerRojo.writeObject(true);
            writerRojo.writeObject(buscaminas);

            System.out.println("Se conecto rojo y todo el pedo");
            Thread.sleep(1000);
            //JUGADOR AZUL
            iniciarUnChat(clientes, cola, organizador, canalNewGame);
            Socket playerAzul = server.accept();
            Socket playerAzulReinicio = server.accept();
            writerNewGameAzul = new ObjectOutputStream(playerAzulReinicio.getOutputStream());
            readerNewGameAzul = new ObjectInputStream(playerAzulReinicio.getInputStream());
            readerAzul = new ObjectInputStream(playerAzul.getInputStream());
            azul = new Jugador((String) readerAzul.readObject(), Equipo.EquipoAzul);
            writerAzul = new ObjectOutputStream(playerAzul.getOutputStream());
            writerAzul.writeObject(azul);
            writerAzul.writeObject(false);
            writerAzul.writeObject(buscaminas);
            System.out.println("Se conecto azul y todo el pedo");
            writerRojo.writeObject(azul);
            writerAzul.writeObject(rojo);

            Cliente jugadorRojo = new Cliente(readerRojo, writerAzul, rojo, semaforo);
            Cliente jugadorAzul = new Cliente(readerAzul, writerRojo, azul, semaforo);
            Reiniciador reiniciador = new Reiniciador(writerNewGameRojo, writerNewGameAzul, readerNewGameRojo,
                    readerNewGameAzul, semaforo);
            System.out.println("se crearon los procesos");
            jugadorRojo.start();
            jugadorAzul.start();
            reiniciador.start();
            System.out.println("Iniciaron los procesos, que chingon");
        }
    }
    public static void iniciarUnChat(ArrayList<ClienteChat> clientes, ColaMensajes cola,
            Organizador organizador, ServerSocket server){
        
        try {
            //server = new ServerSocket(1234);
            
            //while(true){
                System.out.println("pasando1");
                Socket socket = server.accept();
                ClienteChat cliente = new ClienteChat(socket, cola);
                clientes.add(cliente);
                cliente.start();
                        System.out.println("pasando2");
            //}
        } catch (IOException ex) {
            Logger.getLogger(ServerMinesweeper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
