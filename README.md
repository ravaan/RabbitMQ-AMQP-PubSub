#Simple PubSub Model

This is a simple Publish Subscribe model using RabbitMQ with AMQP protocol. This also has spam detection, the publish model will allow only 3 messages per 5 seconds. No external library has been used for parsing the attributes.

###File Structure

The file structure contains 3 files - 

	- Publish.java - Contains the code to publish a message and spam filer.
	- Subscribe.java - Contains the code for channel creation.
	- SpamCounter.log - File to log the spam messages for spam filtering.

###Attributes

-Publisher:
 	- -p : channel to publish message to 
 	- -i : user id publishing the message 
 	- -m : the message to be published 

 Note: All three attributes are required for publishing a message.

 -Subscriber:
 	- -s : the channel name
 	- -i : user id creating the channel

 Note: Both the attributes are required for publishing a message.

###Compilation and Use:

Follow the following steps:

- Setup RabbitMQ following instructions [here](https://www.rabbitmq.com/configure.html) and read the documentation [here](https://www.rabbitmq.com/admin-guide.html)

- Clone this repository at a suitable location

- Compile publish.java

```
javac -Xlint -cp amqp-client-4.0.2.jar publish.java  
```

- Compile subscribe.java

```
javac -Xlint -cp amqp-client-4.0.2.jar subscribe.java
```

- Run subscribe.java

```
java -cp .:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar subscribe -s your_channel_here -i user_id_here
```

- Run publish.java

```
java -cp .:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar publish -p your_channel_here -i user_id_here -m message_here
```

NOTE: please keep the spamCounter.log file in the directory else you'll have to make a few changes in the code.



