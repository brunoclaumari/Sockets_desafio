/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teste_sockets;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author BRUNOSILVA
 */
public class Runnable_SocketServer implements Runnable {

    public static List<Thread_ConexaoSocket> listadeThreads = Collections.synchronizedList(new ArrayList<>());
    //private int min = 1;
    private int max = 50;
    private int portaConexao;
    //private Socket cliente;
    private ServerSocket serverSocket;

    public boolean naoAceitaConexao = false;

    public Runnable_SocketServer(int portaConexao) throws IOException {
        this.portaConexao = portaConexao;

        serverSocket = new ServerSocket(getPortaConexao());
    }

    @Override
    public void run() {

        TestaStatus.status = true;

        try {
            while (TestaStatus.status) {
                System.out.println("Aguardando conexão...");

                Socket cliente = serverSocket.accept();
                Thread_ConexaoSocket thrConexao = new Thread_ConexaoSocket(cliente);
                String nomeThread = thrConexao.getName();
                thrConexao.setNomeDaThread(nomeThread);
                thrConexao.start();
                listadeThreads.add(thrConexao);
            }
            fecharServidor();

        } catch (IOException ex) {
            if (ex.getMessage().toLowerCase().equals("socket closed")) {
                System.out.println("A conexão socket foi fechada");
            } else if (ex.getMessage().toLowerCase().equals("stream closed.")) {
                System.out.println("O stream foi fechado");
            } else {
                Logger.getLogger(Runnable_SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        
    }

    public int getPortaConexao() {
        return portaConexao;
    }
    
    public void fecharServidor() throws IOException {
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void desativarThreadServidor(Thread tred) throws IOException, InterruptedException {
        for (Thread_ConexaoSocket conexao : listadeThreads) {
            conexao.fecharSocket();
        }

        serverSocket.close();

        TestaStatus.status = false;
        tred.join();
    }

    public void listarClientes() {
        for (Thread_ConexaoSocket conexao : listadeThreads) {
            conexao.mostraConexao();
        }
    }
    
  

}


