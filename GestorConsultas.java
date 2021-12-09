package com.p2tienda.p2tienda.model;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


// imports

public class GestorConsultas {

    // atributos
    private RandomAccessFile stream;

    private void creaFichero(){

        String nombreFichero = "/home/tomcat/ficheroDiscos";
        try {
            File f = new File(nombreFichero);
            if(f.exists()){
                stream = new RandomAccessFile(nombreFichero, "rw");
            }
            else{
                stream = new RandomAccessFile(nombreFichero, "rw");
                Disco disco1 = new Disco(1, "Que voy a hacer", "Los Planetas", 20.0, 5);
                disco1.escribeEnFichero(stream);
                Disco disco2 = new Disco(2, "La voz del presidente", "Viva Suecia", 35.0, 1);
                disco2.escribeEnFichero(stream);
                Disco disco3 = new Disco(3, "La revolución sexual", "La casa azul", 20.0, 10);
                disco3.escribeEnFichero(stream);
                Disco disco4 = new Disco(4, "Finisterre", "Vetusta Morla", 40.0, 5);
                disco4.escribeEnFichero(stream);
                Disco disco5 = new Disco(5, "Paradise","Coldplay", 35.0, 2);
                disco5.escribeEnFichero(stream);
                stream.seek(0);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("RUTA INACCESIBLE.");
        }
        catch (IOException e) {
            System.out.println("ERROR CREANDO EL FICHERO.");
        }
    }

    /**
     * Constructor del gestor de consultas de la tienda.
     * Abre el fichero con los datos de prueba.
     * Si no existe, lo crea.
     */
    public GestorConsultas() {
        this.creaFichero();
    }


    public void cierraGestor(){
        try{
            stream.close();
        }
        catch (IOException e) {
            throw new RuntimeException("ERROR CERRANDO EL FICHERO.");
        }
    }

    private long buscaCodigo(int codigoBuscado){
        Disco disco = new Disco();
        try{
            while(this.stream.getFilePointer() < this.stream.length()){
                long puntero = this.stream.getFilePointer();
                disco.leeDeFichero(this.stream);
                if (disco.getCodigo()==codigoBuscado){
                    stream.seek(0);
                    return puntero;
                }
            }
            stream.seek(0);
            return -1;
        }
        catch (EOFException e) {
            throw new RuntimeException("ERROR LOCALIZANDO EL FICHERO.");
        }
        catch (IOException e) {
            throw new RuntimeException("ERROR CREANDO EL FICHERO.");
        }
    }


    public String[] listaAutores() {
        Disco disco = new Disco();
        Set<String> conjuntoAutores = new HashSet<String>();
        try{
            while(this.stream.getFilePointer() < this.stream.length()){
                disco.leeDeFichero(this.stream);
                conjuntoAutores.add(disco.getAutor());
            }
            stream.seek(0);
            return conjuntoAutores.toArray(new String[conjuntoAutores.size()]);
        }
        catch (EOFException e) {
            throw new RuntimeException("ERROR LOCALIZANDO EL FICHERO.");
        }
        catch (IOException e) {
            throw new RuntimeException("ERROR CREANDO EL FICHERO.");
        }
    }



    public String[] buscaAutor(String autorBuscado) {
        Disco disco = new Disco();
        ArrayList<String> comicsAutor = new ArrayList<String>();
        try{

            while(this.stream.getFilePointer() < this.stream.length()){
                disco.leeDeFichero(this.stream);
                if(disco.getAutor().equals(autorBuscado))
                    comicsAutor.add(disco.toString());
            }
            stream.seek(0);
            return comicsAutor.toArray(new String[comicsAutor.size()]);
        }
        catch (EOFException e) {
            throw new RuntimeException("ERROR LOCALIZANDO EL FICHERO.");
        }
        catch (IOException e) {
            throw new RuntimeException("ERROR CREANDO EL FICHERO.");
        }
    }



    public String altaDisco(int codigoBuscado) {
        Disco disco = new Disco();
        long puntero = this.buscaCodigo(codigoBuscado);

        try{
            if (puntero==-1){
                stream.seek(0);
                return "";
            }
            stream.seek(puntero);
            disco.leeDeFichero(stream);
            disco.setCantidad(disco.getCantidad()+1);
            stream.seek(puntero);
            disco.escribeEnFichero(stream);
            stream.seek(0);
            return disco.toString();
        }
        catch (IOException e){
            throw new RuntimeException("ERROR UBICANDO EL PUNTERO");
        }
    }


    public String bajaDisco(int codigoBuscado) {
        Disco disco = new Disco();
        long puntero = this.buscaCodigo(codigoBuscado);
        if (puntero==-1) return "";
        try{
            stream.seek(puntero);
            disco.leeDeFichero(stream);
            if (disco.getCantidad() == 0){
                stream.seek(0);
                return "Disculpe. No quedan ejemplares de este disco.\n" + disco.toString();
            }
            disco.setCantidad(disco.getCantidad()-1);
            String mensaje=disco.toString();
            if(disco.getCantidad()==0)
                mensaje += "\nRecuerde que esta era la ultima copia en stock.";

            stream.seek(puntero);
            disco.escribeEnFichero(stream);
            stream.seek(0);
            return mensaje;
        }
        catch (IOException e){
            throw new RuntimeException("ERROR UBICANDO EL PUNTERO");
        }
    }

}
