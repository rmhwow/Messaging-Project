
import java.util.Date;
import java.util.Iterator;



/**
 * The most important class. This processes all the commands issued by the users
 * There are no known bugs within this file.
 * @author jmishra
 */
public class CommandProcessor
{

	// session added for saving some typing overhead and slight performance benefit
	private static final Config CONFIG = Config.getInstance();

	/**
	 * A method to do login. Should show LOGIN_PROMPT for the nickname,
	 * PASSWORD_PROMPT for the password. Says SUCCESSFULLY_LOGGED_IN is
	 * successfully logs in someone. Must set the logged in user in the Config
	 * instance here
	 *
	 * @throws WhatsAppException if the credentials supplied by the user are
	 * invalid, throw this exception with INVALID_CREDENTIALS as the message
	 */

	public static void doLogin() throws WhatsAppException
	{
		CONFIG.getConsoleOutput().printf(Config.LOGIN_PROMPT);
		String nickname = CONFIG.getConsoleInput().nextLine();
		CONFIG.getConsoleOutput().printf(Config.PASSWORD_PROMPT);
		String password = CONFIG.getConsoleInput().nextLine();

		Iterator<User> userIterator = CONFIG.getAllUsers().iterator();
		while (userIterator.hasNext())
		{
			User user = userIterator.next();
			if (user.getNickname().equals(nickname) && user.getPassword()
					.equals(password))
			{
				CONFIG.setCurrentUser(user);
				CONFIG.getConsoleOutput().
				printf(Config.SUCCESSFULLY_LOGGED_IN);
				return;
			}

		}
		throw new WhatsAppException(String.
				format(Config.INVALID_CREDENTIALS));
	}

	/**
	 * A method to logout the user. Should print SUCCESSFULLY_LOGGED_OUT when
	 * done.
	 */

	public static void doLogout()
	{
		//set the user current user to null    	
		Config.getInstance().setCurrentUser(null);
		//print logged out         
		System.out.print(Config.SUCCESSFULLY_LOGGED_OUT);
	}

	/**
	 * A method to send a message. Handles both one to one and broadcasts
	 * MESSAGE_SENT_SUCCESSFULLY if sent successfully.
	 *
	 * @param nickname - can be a friend or broadcast list nickname
	 * @param message - message to send
	 * @throws WhatsAppRuntimeException simply pass this untouched from the
	 * constructor of the Message class
	 * @throws WhatsAppException throw this with one of CANT_SEND_YOURSELF,
	 * NICKNAME_DOES_NOT_EXIST messages
	 */

	public static void sendMessage(String nickname, String message)
			throws WhatsAppRuntimeException, WhatsAppException
	{
		boolean notFriends;
		boolean wasSuccessful = false;
		Date time = new Date();
		//Special Cases
		/*if the nickname provided isn't a user or a broadcast list, 
    	the code will throw an Exception */   	
		if(Helper.getUserFromNickname(CONFIG.getAllUsers(),nickname) == null 
				&& Helper.getBroadcastListFromNickname(
						CONFIG.getCurrentUser().getBroadcastLists(),nickname) 
						== null){
			throw new WhatsAppException(Config.NICKNAME_DOES_NOT_EXIST);
		} 
		/*if the nickname is the same as the current user's nickname, throw
		 an exception */
		if(CONFIG.getCurrentUser().getNickname().equals(nickname)){
			throw new WhatsAppException(Config.CANT_SEND_YOURSELF);
		}

		//If the nickname is a user, set boolean equal to false        
		if(Helper.getUserFromNickname(CONFIG.getCurrentUser().getFriends(), 
				nickname) != null){
			notFriends = false;
			//create a User object with the correct user from the nickname        	
			User friend = Helper.getUserFromNickname(
					CONFIG.getCurrentUser().getFriends(), nickname);
			//create a message object with the appropriate parameters  
			/*null is used for the broadCastList nickname parameter because
        	the nickname provided is that of a user. */        	
			Message newMessage = new Message(
					CONFIG.getCurrentUser().getNickname(), friend.getNickname(),
					null,time, message, false);
			/*add the new message to both the current user and the friend's
        	messages*/
			CONFIG.getCurrentUser().getMessages().add(newMessage);

			friend.getMessages().add(newMessage);
			//check to make sure the message was stored in the friend's messages

			Iterator<Message> messageitr = friend.getMessages().iterator();

			while(messageitr.hasNext()){
				/*if the message is found in the friend's messages, tell the 
				 * user the message was sent successfully        		 
				 */
				if(messageitr.next().getMessage().equals(message)){
					System.out.println("Sent successfully");
				}
			}
			// if the nickname is not a friend of the user, set notFriend true       	 
		}else{
			notFriends = true;
		}
		// if the nickname is for a broadcast list
		if(Helper.getBroadcastListFromNickname(
				CONFIG.getCurrentUser().getBroadcastLists(), nickname) != null){

			BroadcastList bcastList = Helper.getBroadcastListFromNickname(
					CONFIG.getCurrentUser().getBroadcastLists(), nickname);

			/*if the current iteration is the same as the desired list, send
			 * the message to the list and pass in null for friend nickname
			 */
			CONFIG.getCurrentUser().getMessages().add(new Message
					(CONFIG.getCurrentUser().getNickname(),
							null,bcastList.getNickname(),time,
							message,false));
			//create an iterator for the users on the broadcast list
			Iterator<String> nicknames = 
					bcastList.getMembers().iterator();
			/*if the iteration is the same as the desired user, 
        			save that user in a new variable and add the message 
        			to their message list 
			 */      			
			while(nicknames.hasNext()){
				User bfriend = Helper.getUserFromNickname(
						CONFIG.getAllUsers(), nicknames.next());
				/*add the message to the individual lists with null 
        				for the broadcast list*/        				
				bfriend.getMessages().add(new Message(
						CONFIG.getCurrentUser().getNickname(),
						bfriend.getNickname(),null,
						time,message,false));
				//set boolean vars        					
				wasSuccessful = true;
				notFriends = false;
			}

			if(wasSuccessful == true){
				System.out.println("Sent successfully");
			}
		}
		//if the current user isn't friends with the person throw exception        
		if(notFriends == true){
			throw new WhatsAppException(String.format(
					Config.NICKNAME_DOES_NOT_EXIST, nickname));
		}
	}

	/**
	 * Displays messages from the message list of the user logged in. Prints the
	 * messages in the format specified by MESSAGE_FORMAT. Says NO_MESSAGES if
	 * no messages can be displayed at the present time
	 *
	 * @param nickname - send a null in this if you want to display messages
	 * related to everyone. This can be a broadcast nickname also.
	 * @param enforceUnread - send true if you want to display only unread
	 * messages.
	 */

	public static void readMessage(String nickname, boolean enforceUnread)
	{
		boolean read = false;
		//create iterator to go through the user's messages    	
		Iterator<Message> messages = 
				CONFIG.getCurrentUser().getMessages().iterator();
		//if the user wants to read all their messages    	
		if(nickname == null){
			while(messages.hasNext()){
				Message currentMessage = messages.next();
				/*if it doesn't matter if the message has already been read,
				 * iterate through all the messages, print in MESSAGE_FORMAT,
				 * set the messages to read and the boolean read to false 	    			
				 */
				if(enforceUnread == false){
					if(currentMessage.getToNickname() != null){
						System.out.printf(Config.MESSAGE_FORMAT, 
								currentMessage.getFromNickname(),
								currentMessage.getToNickname(),
								currentMessage.getMessage(), 
								currentMessage.getSentTime());

						currentMessage.setRead(true);

						read = false;
					}
					/*if the user explicitly wants to read unread messages 
					 * and doesn't have a specific user in mind   	    			
					 */
					else{
						System.out.printf(Config.MESSAGE_FORMAT, 
								currentMessage.getFromNickname(),
								currentMessage.getBroadcastNickname(),
								currentMessage.getMessage(), 
								currentMessage.getSentTime());
						currentMessage.setRead(true);
						read = false;
					}
				}
				//if the user only wants to read unread messages    	    		
				else{
					//print out no messages    	    			
					if(currentMessage.isRead()){
						read = true;
					}
					/*if there are unread messages and they are to 
					 * a specific user, print out the message in the correct
					 * format, set the read property to true and the boolean
					 * false 	    			
					 */
					else{
						if(currentMessage.getToNickname()!= null){
							System.out.printf(Config.MESSAGE_FORMAT, 
									currentMessage.getFromNickname(),
									currentMessage.getToNickname(),
									currentMessage.getMessage(), 
									currentMessage.getSentTime());

							currentMessage.setRead(true);

							read = false;	
						} 
						/* if there are unread messages and they are to a 
						 * broadcast list, in the toNickname category, print
						 * the broadcastnickname, set variables as needed
						 */
						else{
							System.out.printf(Config.MESSAGE_FORMAT, 
									currentMessage.getFromNickname(),
									currentMessage.getBroadcastNickname(),
									currentMessage.getMessage(), 
									currentMessage.getSentTime());

							currentMessage.setRead(true);
							read = false;
						}
					}
				}
			}
			//end iteration without a specific user in mind    	    	
		}
		//if the user wants to read messages from a specific nickname    	     
		if(nickname != null){
			while(messages.hasNext()){
				Message messageCurrent = messages.next();
				/* if it doesn't matter if the messages are unread,
				 * iterate through the messages until the messages that 
				 * match the nickname provided are found. Display those 
				 * messages to the user and setRead property to true.  	    		 
				 */
				if(enforceUnread == false){
					//if the nickname is a user    	    			 
					if(messageCurrent.getFromNickname().equals(nickname)){
						System.out.printf(Config.MESSAGE_FORMAT, 
								messageCurrent.getFromNickname(),
								messageCurrent.getToNickname(),
								messageCurrent.getMessage(),
								messageCurrent.getSentTime());

						messageCurrent.setRead(true);

						read = false;

					}
					//if the nickname is a broadcast list    	    			 
					else if(messageCurrent.getBroadcastNickname().equals(nickname)){
						System.out.printf(Config.MESSAGE_FORMAT,
								messageCurrent.getFromNickname(),
								messageCurrent.getBroadcastNickname(),
								messageCurrent.getMessage(),
								messageCurrent.getSentTime());

						messageCurrent.setRead(true);

						read = false;
					}
				}
				//if the user only wants to read unread messages    	    		 
				if(enforceUnread == true){
					//check to see if message has already been read    	    			 
					if(messageCurrent.isRead()){
						read = true;
					}
					//if the message hasn't been read, display the message    	    			 
					if(messageCurrent.isRead() == false){
						/*print out the friend nickname in the from section
						 *  if the nickname provided is for a user    	    				 
						 */
						if(messageCurrent.getFromNickname().
								equals(nickname)){
							System.out.printf(Config.MESSAGE_FORMAT,
									messageCurrent.getFromNickname(),
									messageCurrent.getToNickname(),  
									messageCurrent.getMessage(), 
									messageCurrent.getSentTime());

							messageCurrent.setRead(true);
							read = false;
						} 
						/*print out the broadcast nickname in the 
						 * from section if the nickname provided is 
						 * a broadcast list name 	    				 
						 */
						else if(messageCurrent.getBroadcastNickname().
								equals(nickname)){
							System.out.printf(Config.MESSAGE_FORMAT,
									messageCurrent.getFromNickname(),
									messageCurrent.getBroadcastNickname(), 
									messageCurrent.getMessage(), 
									messageCurrent.getSentTime()); 

							messageCurrent.setRead(true);
							read = false;
						} 
					}
				}
			}

		} 
		//if all the messages have been read, print out no messages.     	
		if(read == true){
			System.out.print(Config.NO_MESSAGES);
		}
	}



	/**
	 * Method to do a user search. Does a case insensitive "contains" search on
	 * either first name or last name. Displays user information as specified by
	 * the USER_DISPLAY_FOR_SEARCH format. Says NO_RESULTS_FOUND is nothing
	 * found.
	 *
	 * @param word - word to search for
	 * @param searchByFirstName - true if searching for first name. false for
	 * last name
	 */

	public static void search(String word, boolean searchByFirstName)
	{
		//make string variable to display later   	
		String isFriend = "no";

		boolean foundUser = false;
		//create iterator object to go through all users    	
		Iterator<User> uiterator = Config.getInstance().getAllUsers().
				iterator();

		if(searchByFirstName == true){
			//iterate through the users and look for the same phrase     	
			while(uiterator.hasNext()){
				User person = uiterator.next();
				/*if the user has the word in their name, check to see 
				 * if they are a friend of the current user
				 */
				if(person.getFirstName().contains(word)){
					/*if they are a friend, the string is assigned "yes" 
					 * and if not then the string is assigned "no"        			
					 */
					if (CONFIG.getCurrentUser().isFriend(person.getNickname())){
						isFriend = "yes";
					}else{
						isFriend = "no";
					}
					//display results       			
					System.out.printf(Config.USER_DISPLAY_FOR_SEARCH, 
							person.getLastName(), person.getFirstName(),
							person.getNickname(),isFriend);	
					//found results for this search        			
					foundUser = true;
				} 
			} 
			//if no users were found at all, print out results found         	
			if(foundUser == false){
				System.out.println(Config.NO_RESULTS_FOUND);
			}	
		} 

		//if it's a search by lastname         
		//iterate through the users and look for the same phrase     	
		while(uiterator.hasNext()){
			User userLastNameSearch = uiterator.next();
			/*if the user has the word in their name, check to see 
			 * if they are a friend of the current user
			 */
			if(userLastNameSearch.getLastName().contains(word)){
				/*if they are a friend, the string is assigned "yes" 
				 * and if not then the string is assigned "no"        			
				 */
				if (CONFIG.getCurrentUser().isFriend(
						userLastNameSearch.getNickname())){
					isFriend = "yes";
				}else{
					isFriend = "no";
				}
				//display results        			
				System.out.printf(Config.USER_DISPLAY_FOR_SEARCH,
						userLastNameSearch.getLastName(), 
						userLastNameSearch.getFirstName(),
						userLastNameSearch.getNickname(),isFriend);
				// users were found        			
				foundUser = true;
			}
		}
		//if no users were found, print out no results found        	
		if(foundUser == false){
			System.out.println(Config.NO_RESULTS_FOUND);
		}
	}

	/**
	 * Adds a new friend. Says SUCCESSFULLY_ADDED if added. Hint: use the
	 * addFriend method of the User class.
	 *
	 * @param nickname - nickname of the user to add as a friend
	 * @throws WhatsAppException simply pass the exception thrown from the
	 * addFriend method of the User class
	 */

	public static void addFriend(String nickname) throws WhatsAppException
	{
		/*get the current user and use the user class addFriend method to 
		 * add the nickname to their friend list    	
		 */
		Config.getInstance().getCurrentUser().addFriend(nickname);
		/*check to see if user was added. If the user was added, 
		 * print message, otherwise throw exception.     
		 */
		if (Config.getInstance().getCurrentUser().isFriend(nickname)){
			System.out.print(Config.SUCCESSFULLY_ADDED);
		} 
		else{
			throw  new WhatsAppException(Config.CANT_LOCATE);
		}
	}

	/**
	 * removes an existing friend. Says NOT_A_FRIEND if not a friend to start
	 * with, SUCCESSFULLY_REMOVED if removed. Additionally removes the friend
	 * from any broadcast list she is a part of
	 *
	 * @param nickname nickname of the user to remove from the friend list
	 * @throws WhatsAppException simply pass the exception from the removeFriend
	 * method of the User class
	 */

	public static void removeFriend(String nickname) throws WhatsAppException
	{
		CONFIG.getCurrentUser().removeFriend(nickname);
		CONFIG.getConsoleOutput().printf(Config.SUCCESSFULLY_REMOVED);
	}

	/**
	 * adds a friend to a broadcast list. Says SUCCESSFULLY_ADDED if added
	 *
	 * @param friendNickname the nickname of the friend to add to the list
	 * @param bcastNickname the nickname of the list to add the friend to
	 * @throws WhatsAppException throws a new instance of this exception with
	 * one of NOT_A_FRIEND (if friendNickname is not a friend),
	 * BCAST_LIST_DOES_NOT_EXIST (if the broadcast list does not exist),
	 * ALREADY_PRESENT (if the friend is already a member of the list),
	 * CANT_ADD_YOURSELF_TO_BCAST (if attempting to add the user to one of his
	 * own lists
	 */

	public static void addFriendToBcast(String friendNickname,
			String bcastNickname) throws WhatsAppException
	{
		if (friendNickname.equals(CONFIG.getCurrentUser().getNickname()))
		{
			throw new WhatsAppException(Config.CANT_ADD_YOURSELF_TO_BCAST);
		}

		if (!CONFIG.getCurrentUser().isFriend(friendNickname))
		{
			throw new WhatsAppException(Config.NOT_A_FRIEND);
		}

		if (!CONFIG.getCurrentUser().isBroadcastList(bcastNickname))
		{
			throw new WhatsAppException(String.
					format(Config.BCAST_LIST_DOES_NOT_EXIST, bcastNickname));
		}

		if (CONFIG.getCurrentUser().isMemberOfBroadcastList(
				friendNickname, bcastNickname))
		{
			throw new WhatsAppException(Config.ALREADY_PRESENT);
		}
		Helper.
		getBroadcastListFromNickname(CONFIG.getCurrentUser().
				getBroadcastLists(), bcastNickname).getMembers().
				add(friendNickname);

		CONFIG.getConsoleOutput().printf(Config.SUCCESSFULLY_ADDED);
	}

	/**
	 * removes a friend from a broadcast list. Says SUCCESSFULLY_REMOVED if
	 * removed
	 *
	 * @param friendNickname the friend nickname to remove from the list
	 * @param bcastNickname the nickname of the list from which to remove the
	 * friend
	 * @throws WhatsAppException throw a new instance of this with one of these
	 * messages: NOT_A_FRIEND (if friendNickname is not a friend),
	 * BCAST_LIST_DOES_NOT_EXIST (if the broadcast list does not exist),
	 * NOT_PART_OF_BCAST_LIST (if the friend is not a part of the list)
	 */
	public static void removeFriendFromBcast(String friendNickname,
			String bcastNickname) throws WhatsAppException
	{
		//create bclist var to hold the list with the same nickname as the param    	
		BroadcastList bclist = Helper.getBroadcastListFromNickname(
				CONFIG.getCurrentUser().getBroadcastLists(),
				bcastNickname);
		//if the nickname provided isn't a friend of the user throw an exception
		if(Config.getInstance().getCurrentUser().isFriend(friendNickname) 
				== false){
			throw new WhatsAppException(Config.NOT_A_FRIEND);
		}
		//if the broadcastlist doesn't exist, throw an exception    		  
		if(bclist == null){
			throw new WhatsAppException(Config.BCAST_LIST_DOES_NOT_EXIST);
		}
		/*if the friendNickname isn't a part of the broadcast list, throw
		 * an exception
		 */

		if( !Helper.getBroadcastListFromNickname(CONFIG.getCurrentUser().
				getBroadcastLists(), bcastNickname).getMembers().
				contains(friendNickname)){
			throw new WhatsAppException(Config.NOT_PART_OF_BCAST_LIST);
		}
		/*use the helper method to get the correct broadcastList, then get
		 * the members of that list and remove the user with the friend
		 * nickname provided    		  
		 */
		Helper.getBroadcastListFromNickname(CONFIG.getCurrentUser().
				getBroadcastLists(), bcastNickname).getMembers().
				remove(friendNickname);

		//if the list no longer has the friend as a member, print message    		  
		if(!Helper.getBroadcastListFromNickname(CONFIG.getCurrentUser().
				getBroadcastLists(), bcastNickname).getMembers().
				contains(friendNickname)){

			System.out.print(Config.SUCCESSFULLY_REMOVED);

		}
	}

	/**
	 * A method to remove a broadcast list. Says BCAST_LIST_DOES_NOT_EXIST if
	 * there is no such list to begin with and SUCCESSFULLY_REMOVED if removed.
	 * Hint: use the removeBroadcastList method of the User class
	 *
	 * @param nickname the nickname of the broadcast list which is to be removed
	 * from the currently logged in user
	 * @throws WhatsAppException Simply pass the exception returned from the
	 * removeBroadcastList method of the User class
	 */

	public static void removeBroadcastcast(String nickname) 
			throws WhatsAppException
	{
		//if there's no broadcast list with the nickname throw an exception    	
		if(Helper.getBroadcastListFromNickname(
				CONFIG.getCurrentUser().getBroadcastLists(),
				nickname) == null){
			throw new WhatsAppException(Config.BCAST_LIST_DOES_NOT_EXIST);
		}
		//if the list does exist, remove it from the users lists    	
		else {
			Config.getInstance().getCurrentUser().removeBroadcastList(nickname);
			//check that the list was removed and if so, print message    		
			if(Helper.getBroadcastListFromNickname(
					CONFIG.getCurrentUser().getBroadcastLists(),
					nickname) == null){
				System.out.println(Config.SUCCESSFULLY_REMOVED);
			}
		}

	}

	/**
	 * Processes commands issued by the logged in user. Says INVALID_COMMAND for
	 * anything not conforming to the syntax. This basically uses the rest of
	 * the methods in this class. These methods throw either or both an instance
	 * of WhatsAppException/WhatsAppRuntimeException. You ought to catch such
	 * exceptions here and print the messages in them. Note that this method
	 * does not throw any exceptions. Handle all exceptions by catch them here!
	 *
	 * @param command the command string issued by the user
	 */
	public static void processCommand(String command)
	{
		try
		{
			switch (command.split(":")[0])
			{
			case "logout":
				doLogout();
				break;
			case "send message":
				String nickname = command.
				substring(command.indexOf(":") + 1, command.
						indexOf(",")).trim();
				String message = command.
						substring(command.indexOf("\"") + 1, command.trim().
								length()-1);
				sendMessage(nickname, message);
				break;
			case "read messages unread from":
				nickname = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				readMessage(nickname, true);
				break;
			case "read messages all from":
				nickname = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				readMessage(nickname, false);
				break;
			case "read messages all":
				readMessage(null, false);
				break;
			case "read messages unread":
				readMessage(null, true);
				break;
			case "search fn":
				String word = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				search(word, true);
				break;
			case "search ln":
				word = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				search(word, false);
				break;
			case "add friend":
				nickname = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				addFriend(nickname);
				break;
			case "remove friend":
				nickname = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				removeFriend(nickname);
				break;
			case "add to bcast":
				String nickname0 = command.
				substring(command.indexOf(":") + 1, command.
						indexOf(",")).
						trim();
				String nickname1 = command.
						substring(command.indexOf(",") + 1, command.trim().
								length()).
								trim();
				addFriendToBcast(nickname0, nickname1);
				break;
			case "remove from bcast":
				nickname0 = command.
				substring(command.indexOf(":") + 1, command.
						indexOf(",")).
						trim();
				nickname1 = command.
						substring(command.indexOf(",") + 1, command.trim().
								length()).
								trim();
				removeFriendFromBcast(nickname0, nickname1);
				break;
			case "remove bcast":
				nickname = command.
				substring(command.indexOf(":") + 1, command.trim().
						length()).trim();
				removeBroadcastcast(nickname);
				break;
			default:
				CONFIG.getConsoleOutput().
				printf(Config.INVALID_COMMAND);
			}
		} catch (StringIndexOutOfBoundsException ex)
		{
			CONFIG.getConsoleOutput().
			printf(Config.INVALID_COMMAND);
		} catch (WhatsAppException | WhatsAppRuntimeException ex)
		{
			CONFIG.getConsoleOutput().printf(ex.getMessage());
		}
	}

}
