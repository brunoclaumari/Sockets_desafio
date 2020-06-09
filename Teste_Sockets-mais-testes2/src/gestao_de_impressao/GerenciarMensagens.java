/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestao_de_impressao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BRUNOSILVA
 */
public class GerenciarMensagens {

    //---------Inicio do Padrão Singleton---------------
    private static Object objeto = new Object();

    //instancia que se auto cria no Singleton
    private static GerenciarMensagens _instancia;

    List<ThreadRunnableGestaoMensAuditoria> listaImpressoras;
    //Thread tr;

    //Fila de mensagens a serem enviadas
    private ConcurrentLinkedQueue<String> filaMensAuditoria;

    //construtor privado para instanciar a fila
    private GerenciarMensagens() {
        filaMensAuditoria = new ConcurrentLinkedQueue<>();
    }

    //método estático que 'auto cria' a instancia do Singleton
    public static GerenciarMensagens getInstancia() {
        if (_instancia == null) {
            _instancia = new GerenciarMensagens();
        }

        return _instancia;
    }
    //--------------Fim do Padrão Singleton--------------------

    //-------------Métodos adicionais
    //Enfileira uma mensagem na fila desta classe
    public synchronized void addMensagemAuditoria(String mensAuditoria) {
        filaMensAuditoria.add(mensAuditoria);
    }

    //Desenfileira uma mensagem em uma string e a retorna
    public synchronized String retiraMensagemAuditoria() {
        String mens = filaMensAuditoria.poll();
        return mens;
    }

    public void ativarThread() {
        if (listaImpressoras == null) {
            listaImpressoras = new ArrayList<>();
            for (int n = 0; n < 5; n++) {
                //Esse runnable cria e inicia a Thread
                // dentro dele
                ThreadRunnableGestaoMensAuditoria runny = new ThreadRunnableGestaoMensAuditoria();
                runny.setExecutando(true);
                runny.getTr().setName("Thread " + (n + 1));
                runny.getTr().start();
                listaImpressoras.add(runny);

            }

        }
    }

    public void desativarThread() {
        if (listaImpressoras != null) {
            for (ThreadRunnableGestaoMensAuditoria thr : listaImpressoras) {
                if (thr.getTr() != null) {
                    //passa para o booleano que vai parar a 
                    //execução atribuindo 'false'
                    thr.setExecutando(false);
                    try {
                        thr.getTr().join(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GerenciarMensagens.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (thr.getTr().isAlive()) {
                        thr.getTr().interrupt();
                    }
                }
            }
        }
    }

}
