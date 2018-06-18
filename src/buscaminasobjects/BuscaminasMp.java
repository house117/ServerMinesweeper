/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscaminasobjects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import objects.Celda;
import objects.CeldaEstado;
import objects.Coordenada;
import objects.Equipo;
import objects.GameEst;
import objects.Jugador;

/**
 *
 * @author House
 */
//hacer clase que lea y escriba los archivos objeto escribidor de archivos
/*
si tengo archivo lo pongo
si no simplemente lo creo sin preguntarle
al usuario
buscar como abrir un archivo de dialogo para indicar donde se
debe de guardar en la version con GUI
*/

public class BuscaminasMp implements Serializable{
    private Celda tablero[][];
    private Integer dimx;
    private Integer dimy;
    private Integer minas;
    private GameEst juego;
    private boolean juegoOn;
    private ObjectOutputStream writer;
    
    
    
    public BuscaminasMp(Integer x,Integer y,Integer m){
        tablero=new Celda[x][y];
        dimx=x;
        dimy=y;
        minas=m;
        juego=GameEst.JUGANDO;
        this.juegoOn=true;
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                tablero[i][j]=new Celda(i,j);
            }
        }
        colocarMinas();
       
        
    }
    public Integer getBlueFlagCount(){
        int minas = 0;
        for (int i = 0; i < dimx; i++) {
            for (int j = 0; j < dimy; j++) {
                if (tablero[i][j].getEstado() == CeldaEstado.BLUEFLAG) {
                     minas++;
                }
            }
        }
        return minas;
    }
    public Integer getRedFlagCount(){
        int minas = 0;
        for (int i = 0; i < dimx; i++) {
            for (int j = 0; j < dimy; j++) {
                if (tablero[i][j].getEstado() == CeldaEstado.REDFLAG) {
                     minas++;
                }
            }
        }
        return minas;
    }
    
    private void colocarMinas(){
        Random random = new Random();
        int contador = 0;
        while(contador<minas){
            int xx = random.nextInt(this.dimx);
            int yy = random.nextInt(this.dimy);
            if (!(tablero[xx][yy]).isMina()) {
                tablero[xx][yy].setMina(true);
                contador++;
            }
        }
    }
    
    public Integer getDimx() {
        return dimx;
    }

    public void setDimx(Integer dimx) {
        this.dimx = dimx;
    }

    public Integer getDimy() {
        return dimy;
    }

    public void setDimy(Integer dimy) {
        this.dimy = dimy;
    }
    public Celda[][] getTablero(){
        return tablero;
    }
    
    public void abrirCelda(Integer x, Integer y, Jugador player){
        if(celdaOK(x,y)){
            if((!tablero[x][y].isMina())){
                if(tablero[x][y].getEstado()==CeldaEstado.CERRADO){
                    tablero[x][y].setEstado(CeldaEstado.ABIERTO);
                }
                int mina = cantMinas(x,y);
                tablero[x][y].setNumero(mina);
                if(mina == 0){
                    for (int i = x-1; i <=x+1; i++) {
                        for (int j = y-1; j <= y+1; j++) {
                            if(celdaOK(i,j) && tablero[i][j].getEstado()== CeldaEstado.CERRADO){
                                abrirCelda(i,j,player);                           
                            }
                        }
                    }
                }
            }else if(tablero[x][y].getEstado() == CeldaEstado.CERRADO){
                
                if(player.getEquipo() == Equipo.EquipoAzul){
                    tablero[x][y].setEstado(CeldaEstado.BLUEFLAG);
                }else{
                    tablero[x][y].setEstado(CeldaEstado.REDFLAG);
                }
            }
            
        }
    }
    public Boolean abrirCelda(Integer x, Integer y, Jugador player, ObjectOutputStream writer) throws IOException{
        
        
        if ((!tablero[x][y].isMina())) {
            writer.writeObject(false);
            if (tablero[x][y].getEstado() == CeldaEstado.CERRADO) {
                tablero[x][y].setEstado(CeldaEstado.ABIERTO);
                writer.writeObject(new Coordenada(x, y, player));
            }
            int mina = cantMinas(x, y);
            tablero[x][y].setNumero(mina);
            if (mina == 0) {
                for (int i = x - 1; i <= x + 1; i++) {
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (celdaOK(i, j) && tablero[i][j].getEstado() == CeldaEstado.CERRADO) {
                            writer.writeObject(true);
                            writer.writeObject(new Coordenada(i, j, player));
                            abrirCelda(i, j, player);
                        }
                    }
                }
            }
            return false;
        } else {
            player.addPoint();
            writer.writeObject(true);
            writer.writeObject(new Coordenada(x, y, player));
            if (tablero[x][y].getEstado() == CeldaEstado.CERRADO) {

                if (player.getEquipo() == Equipo.EquipoAzul) {
                    tablero[x][y].setEstado(CeldaEstado.BLUEFLAG);
                } else {
                    tablero[x][y].setEstado(CeldaEstado.REDFLAG);
                }

            }
            return true;
        }

    }
    /*public boolean winner(){
        boolean resultado = true;
        for (int i = 0; i < dimy; i++) {
            for (int j = 0; j < dimx; j++) {
                if ( (tablero[j][i].getEstado() != Estado.ABIERTO) &&(!tablero[j][i].isMina() ) ){
                    return false;
                }
            }
        }      
        if (resultado == true){
            juegoOn = false;
            juego = GameEst.Win;  
            System.out.println("Ganaste");
        }
        return resultado;
    }
    public void looser(Integer x, Integer y){
        juegoOn=false;
        tablero[x][y].setEstado(Estado.BOOM);
        System.out.println("Perdiste");
        juego= GameEst.Lose;
        for(int i=0; i<dimx; i++){
            for(int j=0; j<dimy; j++){
                if(tablero[i][j].isMina() && tablero[i][j].getEstado()==Estado.CERRADO){
                    tablero[i][j].setEstado(Estado.MINA);
                }
                if(tablero[i][j].getEstado()==Estado.BANDERA && !tablero[x][y].isMina()){
                    tablero[i][j].setEstado(Estado.BANDERAMALA);
                }  
            }
        }
    }*/
    public void resultados(){
         for (int i = 0; i < dimy; i++) {
            for (int j = 0; j < dimx; j++) {
                if ( tablero[j][i].getEstado()!= CeldaEstado.ABIERTO){
                    if ((tablero[j][i].isMina())&&(tablero[j][i].getEstado()== CeldaEstado.CERRADO)){
                        tablero[j][i].setEstado(CeldaEstado.MINA);
                    }else if ((!tablero[j][i].isMina())&&(tablero[j][i].getEstado()== CeldaEstado.BANDERA)){
                        tablero[j][i].setEstado(CeldaEstado.BANDERAMALA);
                    }
                }
                //System.out.println(tablero[j][i]);
            }
        }   
    }
    public void marcarCelda(Integer x, Integer y){
        tablero[x][y].nextEdo();
        
    }
    private boolean celdaOK(int i, int j) {
        return (((i >= 0) && (i <= dimx)) && ((j >= 0) && (j <= dimy))) && ((i != dimx) && (j != dimy));
    }
    public Integer cantMinas(Integer x , Integer y){
        Integer numMinas=0;
        for (int i = x-1; i <= x+1; i++) {
            for (int j =y-1; j <= y+1; j++) {
                if(celdaOK(i,j)){
                    if(tablero[i][j].isMina()){
                        numMinas++; 
                    }
                }
            }            
        } 
        return numMinas;
    }
    public Integer minasTotales(){
        int minas = 0;
        for (int i = 0; i < dimx; i++) {
            for (int j = 0; j < dimy; j++) {
                if (tablero[i][j].isMina() && tablero[i][j].getEstado() == CeldaEstado.CERRADO) {
                     minas++;
                }
            }
        }
        return minas;
    }
    
    public GameEst getEstado(){
        return juego;
    }
    public boolean isGameOn(){
        return juegoOn;
    }

    public Integer getMinas() {
        return minas;
    }

    public void setMinas(Integer minas) {
        this.minas = minas;
    }

    public GameEst getJuego() {
        return juego;
    }

    public void setJuego(GameEst juego) {
        this.juego = juego;
    }

    public boolean isJuegoOn() {
        return juegoOn;
    }

    public void setJuegoOn(boolean juegoOn) {
        this.juegoOn = juegoOn;
    }

    public ObjectOutputStream getWriter() {
        return writer;
    }

    public void setWriter(ObjectOutputStream writer) {
        this.writer = writer;
    }
    
}
//uva