/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestao_de_impressao;

import pacote_de_mensagens.PacoteMensagens;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author BRUNOSILVA
 */
public class RecepcaoMSGTeste {

    //private EnumProtocolo status;
    private StringBuilder montaNovaMensagem;
    private PacoteMensagens pacoteDeMsg;
    Charset codificacao = Charset.forName("ISO-8859-1");

    //Este método faz o tratamento dos dados conforme o protocolo definido:
    //[STX(02)][Opcode][Mensagem][ETX(03)] -> STX e ETX estão em hexadecimal
    public List<PacoteMensagens> agruparMensagem(byte[] arrBytes, int numBytes) throws UnsupportedEncodingException {
        List<PacoteMensagens> listaDeMensagens = new LinkedList<>();
        
        for (int i = 0; i < numBytes; i++) {

            if (arrBytes[i] == 0x03) {
                pacoteDeMsg.setMensagem(montaNovaMensagem.toString());
                listaDeMensagens.add(pacoteDeMsg);
                
                iniciaNovaMensagem();

            } else {
                if (arrBytes[i] == 0x02) {
                    i=i+1;
                    pacoteDeMsg.setOpCode(new String(arrBytes, (i), 1));
                } else {
                   ByteBuffer adicionaByte = ByteBuffer.wrap(new byte[]{arrBytes[i]});
                    montaNovaMensagem.append(codificacao.decode(adicionaByte).toString());
                }
            }
        }

        return listaDeMensagens;
    }

    public void iniciaNovaMensagem() {
        montaNovaMensagem = new StringBuilder();
        pacoteDeMsg = new PacoteMensagens();
        //status = EnumProtocolo.AGUARDA_STX;

    }

}
