///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  WhatsApp.java
// File:             Message.java
// Semester:         CS 367 Spring 2016
//
// Author:           Morgan O'Leary oleary4@wisc.edu
// CS Login:         o-leary
// Lecturer's Name:  Jim Skrentny
// Lecture Section:  1
//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.Date;

/**
 * This is the message class which captures information of a message Every
 * logical message will result in at least two Message objects. One always goes
 * to the message list of the sender user. The others, depending on whether the
 * message was sent to a single user or a broadcast, will go to the message
 * lists of the target user(s). For a one to one message (non broadcast
 * messages), fromNickname and toNickname assume appropriate values.
 * broadcastNickname is null for such cases. For broadcast messages, the sender
 * Message object has a null for toNickname. Instead the broadcastNickname is
 * populated. However, on the receivers' (people who belong to the broadcast
 * list), the toNickname is their own nicknames. They have no idea it came
 * through a broadcast. Hence, broadcastNickname is null for the Message objects
 * that go to the receivers' message lists. fromNickname always assumes the
 * sender's nickname. read is true for the sender's Message object. It's always
 * false in the beginning, for the receivers' Message object until they read the
 * message.
 * 
 * There are no known bugs within this file
 *
 * @author jmishra, morgan o'leary
 */
public class Message
{
	//stores the nickname of the person who sent the message
	private String fromNickname;
	//stores the nickname of the person who the message is from	
	private String toNickname;
	//stores the broadcast nickname the message was sent to	
	private String broadcastNickname;
	//stores the time it was sent	
	private Date sentTime;
	//stores the physical message	
	private String message;
	//stores whether the message has been read or not	
	private boolean read;
    
    /**
     * A constructor to instantiate this class. fromNickname and message cannot
     * be null or empty. toNickname must be null, if broadcastNockname is
     * provided. Alternatively, broadcastNickname must be null if toNickname is
     * provided. sentTime cannot be null. For any of the above mentioned
     * validation issues, you must throw a WhatsAppRuntimeException with the
     * CANT_BE_EMPTY_OR_NULL message
     *
     * @param fromNickname the nickname of the user sending the message
     * @param toNickname the nickname of the user to whom the message is being
     * sent. This must be null if the message is being broadcast to a broadcast
     * list
     * @param broadcastNickname the nickname of the broadcast list to which this
     * message is being sent. This must be null if the message is being sent to
     * a single user
     * @param sentTime the instant of time at which the message was sent
     * @param message the message string that is being communicated from the
     * sender to the receiver(s)
     * @param read whether the receiver has read this message
     * @throws WhatsAppRuntimeException read the description of this constructor
     * for the conditions failing which you may throw this exception
     */

    public Message(String fromNickname, String toNickname, 
    		String broadcastNickname,Date sentTime, String message, 
    		boolean read) throws WhatsAppRuntimeException
    {
    	//if some of the arguments provided are null, throw an exception        
    	if(fromNickname == null || message == null || sentTime == null){
        	throw new WhatsAppRuntimeException(Config.CANT_BE_EMPTY_OR_NULL);
        }
    	/*if the toNickname parameter is not null, assign it to the 
    	 * private field and set the broadcastNickname field to null  	
    	 */
        if(toNickname != null){
        	this.toNickname = toNickname;
        	this.broadcastNickname = null;
        } 
        /*if the broadcastNickname parameter is not null, assign it to the 
    	 * private field and set the toNickname field to null  	
    	 */
        if(broadcastNickname != null){
        	this.broadcastNickname = broadcastNickname;
        	this.toNickname = null;
        }
        //set other fields to the information passed in through the constructor        
        this.fromNickname = fromNickname;
        this.sentTime = sentTime;
        this.message = message;
        this.read = read;
        
    }

    /**
     * A getter for the fromNickname
     *
     * @return the fromNickname of this message
     */
    public String getFromNickname()
    {
        return fromNickname;
    }

    /**
     * A setter for the fromNickname
     *
     * @param fromNickname the fromNickname of this message
     */
    public void setFromNickname(String fromNickname)
    {
        this.fromNickname = fromNickname;
    }

    /**
     * A getter for the toNickname
     *
     * @return the toNickname of this message
     */
    public String getToNickname()
    {
        return toNickname;
    }

    /**
     * A setter for the toNickname
     *
     * @param toNickname the toNickname of this message
     */
    public void setToNickname(String toNickname)
    {
        this.toNickname = toNickname;
    }

    /**
     * A getter for the broadcastNickname
     *
     * @return the broadcastNickname of this message
     */
    public String getBroadcastNickname()
    {
       
        return broadcastNickname;
    }

    /**
     * A setter for the broadcastNickname
     *
     * @param broadcastNickname the broadcastNickname of this message
     */
    public void setBroadcastNickname(String broadcastNickname)
    {
        this.broadcastNickname = broadcastNickname;
    }

    /**
     * A getter for the time at which this message was sent
     *
     * @return the Date object which captures the time at which this message was
     * sent
     */
    public Date getSentTime()
    {
        return sentTime;
    }

    /**
     * A setter for the time at which this message was sent
     *
     * @param sentTime the Date object which captures the time at which this
     * message was sent
     */
    public void setSentTime(Date sentTime)
    {
        this.sentTime = sentTime;
    }

    /**
     * A getter for the message string
     *
     * @return the message string
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * A setter for the message string
     *
     * @param message the message string
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * A getter for the read flag which indicates whether the message has been
     * read
     *
     * @return the read flag
     */
    public boolean isRead()
    {
        return read;
    }

    /**
     * A setter for the read flag which indicates whether the message has been
     * read
     *
     * @param read the read flag
     */
    public void setRead(boolean read)
    {
        this.read = read;
    }

}
