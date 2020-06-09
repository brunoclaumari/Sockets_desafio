/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestao_de_impressao;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BRUNOSILVA
 */
public class ThreadRunnableGestaoMensAuditoria implements Runnable {

    private boolean executando = false;
    private Thread thrd;

    public ThreadRunnableGestaoMensAuditoria() {
        thrd = new Thread(this);        

    }

    public boolean getExecutando() {
        return executando;
    }

    public void setExecutando(boolean teste) {
        this.executando = teste;
    }

    @Override
    public void run() {
        setExecutando(true);
        while (executando) {
            try {
                String mensRecebida = GerenciarMensagens
                        .getInstancia()
                        .retiraMensagemAuditoria();
                if (mensRecebida != null) {
                    imprimeMensagem(mensRecebida);
                }

                Thread.sleep(2);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadRunnableGestaoMensAuditoria.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void imprimeMensagem(String mensagem) throws InterruptedException {
        System.out.printf("%s - Impressão pela %s = %s\n", Instant.now().toString(), getTr().getName(), mensagem);
        Thread.sleep(50);

    }

    public Thread getTr() {
        return thrd;
    }
}
