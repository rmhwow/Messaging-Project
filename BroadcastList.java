
import java.util.List;

/**
 * This is the broadcast list class which captures information of a broadcast
 * list
 * there are no known bugs within this file
 */
public class BroadcastList
{
	//stores the nickname of the list object
	private String nickname;
	//stores a list of user nicknames associated with this list object	
	private List<String> members;


    /**
     * Constructs a new instance of this class. nickname cannot be null or
     * empty. members cannot be null.
     *
     * @param nickname the nickname of the broadcast list
     * @param members the list of nicknames of all members of this list
     * @throws WhatsAppRuntimeException throw a new instance of this with
     * CANT_BE_EMPTY_OR_NULL message if the validation of nickname or members
     * fails
     *
     */

    public BroadcastList(String nickname, List<String> members) 
    		throws WhatsAppRuntimeException
    {
    	//if any of the parameters are null throw an exception	
    	if(nickname == null || members == null){
    		throw new WhatsAppRuntimeException(Config.CANT_BE_EMPTY_OR_NULL);
    	}
    	//if they aren't null, assign their values to the private fields    	
        this.nickname = nickname;
        this.members = members;
        
    }

    /**
     * A getter of the nickname
     *
     * @return the nickname of the broadcast list
     */
    public String getNickname()
    {
        return nickname;
    }

    /**
     * A setter of the nickname of this broadcast list
     *
     * @param nickname the nickname of this broadcast list
     */
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    /**
     * A getter of the list of members of this broadcast list
     *
     * @return the list of members of this broadcast list
     */
    public List<String> getMembers()
    {
        return members;
    }

    /**
     * A setter of the list of members of this broadcast list
     *
     * @param members the list of members of this broadcast list
     */
    public void setMembers(List<String> members)
    {
        this.members = members;
    }

}
