import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.io.*;
import java.util.*;

public class publish {

  private static final String EXCHANGE_NAME = "direct_logs";

  public static void main(String[] argv) throws Exception {

    Long time = System.currentTimeMillis();

    /*Uncomment this line when runnin the code for the first time when the spammerCount.log file is not created*/
    //    Map<String, List<Long>> spammer = new HashMap<>();

    FileInputStream fileInputStream  = new FileInputStream("spamCounter.log");
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
    Map<String, List<Long>> spammer = (HashMap) objectInputStream.readObject();
    objectInputStream.close();

    //Parsing the flags, -p, -m,-i
    String cName = "", message = "", id = "";
    boolean pFlag = false , mFlag = false, iFlag = false ;
    for ( String para : argv)
    {
      if(pFlag) {cName = para; pFlag = false;mFlag = false;}
      if(mFlag) {message += " " +para; }
      if(iFlag) {id = para; iFlag = false;mFlag = false;}
      message = message.trim();

      if(para.equals("-p"))pFlag = true;
      else if(para.equals("-m"))mFlag = true;
      else if(para.equals("-i"))iFlag = true;
    }


    //Check spamming
    boolean spamFlag = false;
    if(spammer.containsKey(id))
    {
      if(spammer.get(id).size()<3)
      spammer.get(id).add(time);
      else
      {
        if((time -spammer.get(id).get(0)) < 5000)
        {
          System.err.println("You cannot post more than 3 messages in 5 seconds.");
          spamFlag = true;
        }
        else
        {
          spammer.get(id).set(0,spammer.get(id).get(1));
          spammer.get(id).set(1,spammer.get(id).get(2));
          spammer.get(id).set(2, time);
          spamFlag = false;
        }
      }
    }
    else
    {
      spammer.put(id, new ArrayList<Long>());
      spammer.get(id).add(time);
    }

    //send the message.
    if(!spamFlag)
    {
      //check for flags
      if(cName != "" && message !="" && id !=""){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String messageFinal = message+":" +id;
        channel.basicPublish(EXCHANGE_NAME, cName, null, messageFinal.getBytes("UTF-8"));
        System.out.println( message + " has been published to the channel " + cName +" from user id " + id);
        channel.close();
        connection.close();
      }
      else
      System.err.println("Need Channel, Message and UserID to successfully publish a message");
    }

    //write the id details in file.
    FileOutputStream fileOutputStream = new FileOutputStream("spamCounter.log");
    ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(spammer);
    objectOutputStream.close();

  }
}
