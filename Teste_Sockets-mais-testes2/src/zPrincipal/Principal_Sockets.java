/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zPrincipal;

import gestao_de_impressao.GerenciarMensagens;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import teste_sockets.Runnable_SocketServer;

/**
 *
 * @author BRUNOSILVA
 */
public class Principal_Sockets {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Runnable_SocketServer server = null;
        Thread tred = null;
        Scanner scan = new Scanner(System.in);
        try {
            server = new Runnable_SocketServer(6001);

            tred = new Thread(server);
            tred.start();
            GerenciarMensagens.getInstancia().ativarThread();

            boolean saindo = false;
            System.out.println("----Bem vindo ao servidor de conexoes TCP/IP!! ");
            int resp;
            do {

                try {
                    System.out.println("Escolha a opção desejada");
                    System.out.println("1 - Listar Conexões");
                    System.out.println("2 - Sair");
                    resp = scan.nextInt();
                    switch (resp) {
                        case 1:
                            server.listarClientes();
                            break;
                        case 2:
                            saindo = true;
                            break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Digite apenas os numeros pedidos no menu!!");
                    saindo = false;
                    //scan.nextLine();
                }

            } while (!saindo);

            System.out.println("Cliente Finalizado");

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {

            try {
                GerenciarMensagens.getInstancia().desativarThread();
                server.desativarThreadServidor(tred);
                
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Principal_Sockets.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
