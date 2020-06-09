/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teste_sockets;

import gestao_de_impressao.GerenciarMensagens;

import pacote_de_mensagens.PacoteMensagens;
import gestao_de_impressao.RecepcaoMSGTeste;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static teste_sockets.Runnable_SocketServer.listadeThreads;

/**
 *
 * @author BRUNOSILVA
 */
public class Thread_ConexaoSocket extends Thread {

    private String nomeDaThread;
    private Socket conexao;    
    RecepcaoMSGTeste recebeMensagens = new RecepcaoMSGTeste();

    public synchronized String getNomeDaThread() {
        return nomeDaThread;
    }

    public synchronized void setNomeDaThread(String nomeThread) {
        this.nomeDaThread = nomeThread;
    }

    public Thread_ConexaoSocket(Socket conex) throws IOException {
        this.conexao = conex;

    }

    @Override
    public void run() {
        recebeMensagens.iniciaNovaMensagem();
        long tempoEspera = 0;
        System.out.println("Cliente conectado id: " + nomeDaThread);

        InputStream testandoFechar = null;
        try (InputStream inputStr = conexao.getInputStream()) {
            boolean emUso = true;
            int numeroBytes = 0;
            
            while (emUso) {
                //Essa parte envia sinal para o cliente. 
                //Se não chegar é porque o cliente desconectou
                if (Calendar.getInstance().getTimeInMillis() - tempoEspera > 5000) {
                    DataOutputStream envio = new DataOutputStream(conexao.getOutputStream());
                    envio.writeUTF("?");
                    envio.flush();
                    tempoEspera = Calendar.getInstance().getTimeInMillis();
                }

                if (inputStr.available() != 0) {
                    emUso = true;
                    byte[] dados = (new byte[inputStr.available()]);
                    numeroBytes = inputStr.read(dados);
                    if (numeroBytes > 0) {
                        List<PacoteMensagens> listaMsg = recebeMensagens.agruparMensagem(dados, numeroBytes);
                        trataMsgRecebida(listaMsg);
                    }

                }

                Thread.sleep(10);
            }

            System.out.println(nomeDaThread + " Desativada!");

            if (listadeThreads.isEmpty()) {
                TestaStatus.status = false;
            }
            this.desativarConexao();

        } catch (IOException ex) {
            //Logger.getLogger(Thread_ConexaoSocket.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("inputStream " + nomeDaThread + " interrompido");
            System.out.println(nomeDaThread + " Desativada! " + conexao.hashCode());
            this.desativarConexao();
            //listadeThreads.remove(nomeDaThread);

        } catch (InterruptedException ex) {
            Logger.getLogger(Thread_ConexaoSocket.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            fecharSocket();
        }

    }

    public void mostraConexao() {
        if (!conexao.isClosed() && conexao.isConnected()) {
            System.out.println(conexao.hashCode() + " cliente ainda conectado " + nomeDaThread);
        } else {
            System.out.println(conexao.hashCode() + " cliente já desconectado " + nomeDaThread);
        }

    }

    void fecharSocket() {
        if (conexao != null) {
            if (conexao.isConnected()) {
                try {
                    conexao.close();
                } catch (IOException ex) {
                    //Logger.getLogger(Thread_ConexaoSocket.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Falha ao fechar o socket");
                }
            }
        }

    }

    void fecharInputStream(InputStream ois) throws IOException {
        if (ois.available() != 0) {
            ois.close();
        }
    }

    private void desativarConexao() {
        if (conexao != null) {
            try {
                fecharSocket();
                this.join(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Thread_ConexaoSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (this.isAlive()) {
                this.interrupt();
            }
        }
    }

    private synchronized void trataMsgRecebida(List<PacoteMensagens> listaMsg) {
        for (PacoteMensagens msg : listaMsg) {
            if (msg.getOpCode().equals("I")) {
                GerenciarMensagens.getInstancia().addMensagemAuditoria(msg.getMensagem());
            } else if (msg.getOpCode().equals("S")) {
                System.out.println(conexao.hashCode() + " mensagem de status recebida: " + msg.getMensagem());
            } else {
                System.out.println(conexao.hashCode() + " mensagem não identificada: " + msg.getMensagem());
            }
        }
    }

}

/*
if (Calendar.getInstance().getTimeInMillis() - tempoEspera > 3000) {
                    DataOutputStream envio = new DataOutputStream(sc.getOutputStream());
                    envio.writeUTF(nomeDaThread + " ?");
                    envio.flush();
                    tempoEspera = Calendar.getInstance().getTimeInMillis();
                }

 */
