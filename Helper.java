///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  WhatsApp.java
// File:             Helper.java
// Semester:         CS 367 Spring 2016
//
// Author:           Morgan O'Leary oleary4@wisc.edu
// CS Login:         o-leary
// Lecturer's Name:  Jim Skrentny
// Lecture Section:  1
//////////////////////////// 80 columns wide //////////////////////////////////
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * A few helper static methods. This class allows different classes to access
 * objects from a nickname parameter as well as populates data
 * This file has no known bugs
 * @author jmishra, morgan o'leary
 */
public class Helper
{

    /**
     * Given a nickname as a String return the User object from the List of
     * Users
     *
     * @param users the list of users from which a user will be returned
     * @param nickname the nickname of the user to be returned
     * @return the USer object whose nickname is nickname and this object is
     * searched from the users List
     */

    public static User getUserFromNickname(List<User> users, String nickname)
    {
    	//create an iterator to go through the list provided    	
    	Iterator<User> useriterator = users.iterator();
    	
    	//iterate through the list    	
        while(useriterator.hasNext()){
    		User user = useriterator.next();
    		/*if the user in the iteration has the same nickname as 
    		 * the nickname provided in the parameter, return the user  		
    		 */
        	if( user.getNickname().equals(nickname)){
        		return user;
        		
        	}		
        }
        //if there are no users with the parameter nickname return null
        return null;

    }

    /**
     * Given a nickname as a String return the BroadcastList from the List of
     * BroadcastLists
     *
     * @param lists the list of lists from which one list object is to be
     * returned
     * @param nickname the nickname of the broadcast list to be returned
     * @return the BroadcastList object whose nickname is nickname. This object
     * is searched from the lists List
     */

    public static BroadcastList getBroadcastListFromNickname(
            List<BroadcastList> lists, String nickname)
    {
        Iterator <BroadcastList> bcastList = lists.iterator();
        //iterate through the lists        
        while(bcastList.hasNext()){
        	//if a list has the same nickname as the param, return that list 
        	BroadcastList currentList = bcastList.next();
        	if(currentList.getNickname().equals(nickname)){
        		return currentList;
        	}
        }
        //if no lists with the param nickname exist, return null        
        return null;
    }

    /**
     * Given a nickname determine whether a User exists globally with that
     * nickname
     *
     * @param nickname the nickname to check for
     * @return true if a user exists globally with the provided nickname
     */

    public static boolean isExistingGlobalContact(String nickname)
    {
       Iterator <User> allUsers = Config.getInstance().getAllUsers().iterator();
        //iterate through all users        
        while(allUsers.hasNext()){
        	//if a user has the same nickname as the parameter return true        	
        	if(allUsers.next().getNickname().equals(nickname)){
        		return true;
        	}
        }
        //if no user globally has that nickname return false        
        return false;
    }

    /**
     * Populate data from the file with the given path
     *
     * @param filename the path to the input file
     * @throws FileNotFoundException this is thrown if the file is not found
     * @throws IOException this is thrown if some error occurs while reading the
     * input file
     * @throws WhatsAppRuntimeException this is thrown if any invalid data is
     * used to construct any of the WhatsApp objects or a line is encountered
     * that does not begin with any of the four words mentioned in the
     * specification
     * @throws java.text.ParseException this exception is thrown if some issue
     * occurs while parsing the date string
     */
    public static void populateData(String filename) throws 
    FileNotFoundException, IOException, WhatsAppRuntimeException, ParseException
    {
        Scanner scanner = new Scanner(new File(filename));        
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            switch (parts[0])
            {
                case "user":
                    User user = new User(parts[1], parts[2], parts[3], parts[4], 
                    		new ArrayList<>(), new ArrayList<>(),
                    		new ArrayList<>());
                    Config.getInstance().getAllUsers().add(user);
                    break;
                case "flist":
                    user = getUserFromNickname(Config.getInstance().
                            getAllUsers(), parts[1]);
                    for (int i = 2; i < parts.length; ++i)
                    {
                        user.getFriends().add(getUserFromNickname(Config.
                                getInstance().getAllUsers(), parts[i]));
                    }
                    break;
                case "bcast":
                    user = getUserFromNickname(Config.getInstance().
                            getAllUsers(), parts[1]);
                    BroadcastList newList = new BroadcastList(parts[3], 
                    		new ArrayList<>());
                    for (int i = 4; i < parts.length; ++i)
                    {
                        newList.getMembers().add(parts[i]);
                    }
                    user.getBroadcastLists().add(newList);
                    break;
                case "message":
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    if ((getUserFromNickname(Config.
                            getInstance().getAllUsers(), parts[1])).
                            isBroadcastList(parts[2]))
                    {
                        Message sentMessage = new Message(parts[1], null, parts[2], sdf.
                                parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), true);
                        getUserFromNickname(Config.getInstance().getAllUsers(), parts[1]).
                                getMessages().add(sentMessage);
                        Iterator<String> memberIterator = getBroadcastListFromNickname(getUserFromNickname(Config.
                                getInstance().getAllUsers(), parts[1]).
                                getBroadcastLists(), parts[2]).getMembers().
                                iterator();
                        for (int i = 5; i < parts.length; ++i)
                        {
                            String member = memberIterator.next();
                            Message receivedMessage = new Message(parts[1], member, null, sdf.
                                    parse(parts[3]), parts[4].
                                    substring(1, parts[4].length() - 1), parts[i].
                                    equals("read"));
                            getUserFromNickname(Config.getInstance().
                                    getAllUsers(), member).
                                    getMessages().add(receivedMessage);
                        }
                    } else
                    {
                        Message sentMessage = new Message(parts[1], parts[2], null, sdf.
                                parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), true);
                        getUserFromNickname(Config.getInstance().getAllUsers(), parts[1]).
                                getMessages().add(sentMessage);
                        Message receivedMessage = new Message(parts[1], parts[2], null, sdf.
                                parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), parts[5].
                                equals("read"));
                        getUserFromNickname(Config.getInstance().getAllUsers(), parts[2]).
                                getMessages().add(receivedMessage);
                    }
                    break;
                default:
                    throw new WhatsAppRuntimeException();
            }
        }
        scanner.close();
    }

}
