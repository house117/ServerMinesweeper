/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import buscaminasobjects.BuscaminasMp;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author House
 */
public class Reiniciador extends Thread{
    private Socket socket;
    private ObjectOutputStream writerRojo;
    private ObjectOutputStream writerAzul;
    private ObjectInputStream readerRojo;
    private ObjectInputStream readerAzul;
    private Semaforo semaforo;
    public Reiniciador (ObjectOutputStream writerRojo,
    ObjectOutputStream writerAzul,
    ObjectInputStream readerRojo,
    ObjectInputStream readerAzul, Semaforo semaforo){
        this.writerAzul = writerAzul;
        this.writerRojo = writerRojo;
        this.readerRojo = readerRojo;
        this.readerAzul = readerAzul;
        this.semaforo = semaforo;
    }

    @Override
    public void run() {
        while(true){
            try {
                Boolean respuestaAzul = (Boolean) readerAzul.readObject();
                Boolean respuestaRojo = (Boolean) readerRojo.readObject();
                if(respuestaAzul && respuestaRojo){
                    writerAzul.writeObject(true);
                    writerRojo.writeObject(true);
                    BuscaminasMp buscaminas = new BuscaminasMp(16, 16, 51);
                    writerAzul.writeObject(buscaminas);
                    writerRojo.writeObject(buscaminas);
                    writerRojo.writeObject(true);
                    writerAzul.writeObject(false);
                    synchronized (semaforo) {
                        semaforo.setTurnoAzul(false);
                        semaforo.setTurnoRojo(true);
                    }

                    
                }else{
                    writerAzul.writeObject(false);
                    writerRojo.writeObject(false);
                }
            } catch (IOException ex) {
                Logger.getLogger(Reiniciador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Reiniciador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
