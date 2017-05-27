import com.rabbitmq.client.*;
import java.io.IOException;

public class subscribe {

  private static final String EXCHANGE_NAME = "direct_logs";

  public static void main(String[] argv) throws Exception {


    //parse for -i and -s flags
    String  id = "", subs = "";
    boolean  iFlag = false, sFlag = false;
    for ( String para : argv)
    {
      if(iFlag) {id = para; iFlag = false;}
      if(sFlag) {subs = para; sFlag = false;}

      else if(para.equals("-i"))iFlag = true;
      else if(para.equals("-s"))sFlag = true;
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
    String queueName = channel.queueDeclare().getQueue();

    //check for parameters passed
    if (argv.length < 1){
      System.err.println("No parameters passed ");
      System.exit(1);
    }

    //Confirm subscription
    channel.queueBind(queueName, EXCHANGE_NAME,subs);
    System.out.println("You have been subscribed to the channel " + subs + "\nAwaiting new messages to this channel.");

    //set Qos = 1
    channel.basicQos(1);

    //Receove message and hande delivery
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
      AMQP.BasicProperties properties, byte[] body) throws IOException {
        String text = new String(body, "UTF-8");
        String ID = text.substring(text.indexOf(':') + 1,text.length());
        String message = text.substring(0, text.indexOf(':'));
        System.out.println("UserID " + ID + " published " + message);
      }
    };

    channel.basicConsume(queueName, true, consumer);

  }
}
