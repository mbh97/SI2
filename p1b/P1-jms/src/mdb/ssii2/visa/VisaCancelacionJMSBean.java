/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = "update pago set codRespuesta=999 where idAutorizacion=?";
  private static final String UPDATE_SALDO_QRY = "update tarjeta set saldo = saldo + pago.importe from pago where pago.numeroTarjeta = tarjeta.numeroTarjeta and pago.idautorizacion = ?";
   // TODO : Definir UPDATE sobre la tabla pagos para poner
   // codRespuesta a 999 dado un código de autorización


  public VisaCancelacionJMSBean() {
  }

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean ret = false;

        PreparedStatement pstmt = null;

      try {

          // Obtener conexion
            

            

          if (inMessage instanceof TextMessage) {
            msg = (TextMessage) inMessage;
            logger.info("MESSAGE BEAN: Message received: " + msg.getText());
            int id = Integer.parseInt(msg.getText());

            con = getConnection();

            String cancela = UPDATE_CANCELA_QRY;
            pstmt = con.prepareStatement(cancela);
            pstmt.setInt(1, id);
            pstmt.execute();

            String saldo = UPDATE_SALDO_QRY;
            pstmt = con.prepareStatement(saldo);
            pstmt.setInt(1, id);
            pstmt.execute();

          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }
  }


}
