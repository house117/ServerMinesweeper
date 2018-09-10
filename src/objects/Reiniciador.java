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
                System.out.println("-------REINICIADOR ESPERA RESPUESTAS DE JUGADORES!!!!---------");
                Boolean respuestaAzul = (Boolean) readerAzul.readObject();
                System.out.println("-------REINICIADOR RECIBE RESPUESTA DE AZUL---------");
                if (respuestaAzul) {
                    writerRojo.writeObject(true);
                    Boolean respuestaRojo = (Boolean) readerRojo.readObject();
                    if (respuestaRojo) {
                        if (respuestaAzul && respuestaRojo) {
                            System.out.println("-------------RESPUESTAS POSITIVAS A INICIAR NUEVO JUEGO------------");
                            writerAzul.writeObject(true);
                            System.out.println("-------------SE ENVIARON RESPUESTAS POSITIVAS A AMBOS JUGADORES------------");
                            BuscaminasMp buscaminas = new BuscaminasMp(16, 16, 51);
                            writerAzul.writeObject(buscaminas);
                            writerRojo.writeObject(buscaminas);
                            /*writerRojo.writeObject(true);
                            writerAzul.writeObject(false);
                            System.out.println("REINICIADOR ENVIÓ EL JUEGO Y LOS TURNOS A LOS JUGADORES!!!");
                            synchronized (semaforo) {
                                semaforo.setTurnoAzul(false);
                                semaforo.setTurnoRojo(true);
                            }*/
                            System.out.println("REINICIADOR NO ENVIO TURNOS A SEMAFORO");
                            System.out.println("REINICIADOR NOTIFICA QUE YA USO A SEMAFORO");

                        } else {
                            writerAzul.writeObject(false);
                            writerRojo.writeObject(false);
                        }
                    }else{
                        System.out.println("CASO EN EL QUE ACEPTÓ AZUL PERO ROJO NO QUIZO.");
                        writerAzul.writeObject(false);
                        writerRojo.writeObject(true);
                    }
                }else{
                    System.out.println("CASO EN EL QUE NO QUIZO EL JUGADOR AZUL MEN :(");
                    writerRojo.writeObject(false);
                    readerRojo.readObject();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Reiniciador.class.getName()).log(Level.SEVERE, null, ex);
            }
            //here
            System.out.println("REINICIADOR TERMINÓ TODO!!!!! DARA VUELTA???");
        }
    }
}
