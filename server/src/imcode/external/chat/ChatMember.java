package imcode.external.chat;

import imcode.server.User;

import java.util.*;

import org.apache.log4j.Logger;

public class ChatMember implements Comparable {

    private int memberId;
    private String _ipNr;
    private User user;
    private ChatMessage _lastChatMsg;
    private int _lastMsgInt;
    private List _msgBuffer;
    private String _name;
    private ChatGroup group;
    private int _maxSize = 200;
    private Chat _parent;
    private int referrerMetaId;
    private int refreshTime;
    private boolean showDateTimesEnabled = false;
    private boolean showPrivateMessagesEnabled = true;
    private boolean autoRefreshEnabled = false;
    private boolean showEnterAndLeaveMessagesEnabled;
    private Date lastRequest = new Date();
    private int fontSize = 2;
    private static final int HOURS__BACK_FOR__HISTORY = 1;
    private static final int MINUTE__BACK_FOR__HISTORY = 0;
    private boolean isTimedOut;
    private boolean isKickedOut;

    private final static Logger log = Logger.getLogger("imcode.external.chat.ChatMember");

    /**
     * construktor
     * <p/>
     * obs måste fixa alla inställningar med chatparametrarna de ligger idag i sessionen
     * ska flyttas in till användaren så att chat medleandena formateras direk innan de levereras
     * om det går vill säga
     */
    // obs måste fixa alla inställningar med chatparametrarna de ligger idag i sessionen
    // ska flyttas in till användaren så att chat medleandena formateras direk innan de levereras
    // om det går vill säga
    protected ChatMember(int memberId, Chat parent, User user, int referrerMetaId) {
        this.referrerMetaId = referrerMetaId;
        _msgBuffer = Collections.synchronizedList(new LinkedList());
        this.memberId = memberId;
        this.user = user;
        _parent = parent;
        this.refreshTime = parent.getRefreshTime();
        this.isTimedOut = false;
        this.isKickedOut = false;

    }

    //*********** methods ************
    public Chat getParent() {
        return _parent;
    }

    /**
     * Sets the referens to the group the user joins by the group
     * when you add a user in to one
     */
    protected void setCurrentGroup(ChatGroup group) {
        _msgBuffer = Collections.synchronizedList(new LinkedList());
        _lastChatMsg = null;
        this.group = group;
    }

    /**
     * Gets the currentGroup
     * 
     * @return The current group
     */

    public ChatGroup getGroup() {
        return group;
    }

    public int getLastMsgNr() {
        return _lastMsgInt;
    }

    public ListIterator getMessages() {
        synchronized (_msgBuffer) {

            _lastChatMsg = (ChatMessage) _msgBuffer.get(0);
            _lastMsgInt = _lastChatMsg.getIdNumber();

            return copyList(_msgBuffer).listIterator();
        }
    }

    private static List copyList(List listToCopy) {
        List _msgBufferCopy = new ArrayList(listToCopy.size());
        _msgBufferCopy.addAll(listToCopy);
        return _msgBufferCopy;
    }

    protected void addNewMsg(ChatMessage msg) {

        synchronized (_msgBuffer) {
            pruneBuffer();
            _msgBuffer.add(0, msg);
        }
    }

    private void pruneBuffer() {
        if (_msgBuffer.size() > _maxSize) {
            _msgBuffer.remove(_msgBuffer.size() - 1);
        }
    }

    public void addMessageHistory() {
        List historyMsg = this.getGroup().get_msgBuffer();
        synchronized (_msgBuffer) {
            this._msgBuffer = Collections.synchronizedList(new LinkedList(historyMsg));
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(Calendar.HOUR, -HOURS__BACK_FOR__HISTORY );
            cal.add(Calendar.MINUTE, -MINUTE__BACK_FOR__HISTORY );

            for (Iterator it = this._msgBuffer.iterator(); it.hasNext();) {
                ChatMessage tempMessage = (ChatMessage) it.next();
                if (tempMessage.getDateTime().before(cal.getTime())) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Sets the name of the member
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the name of the member
     * 
     * @return The name of the member,
     *         if no name has been set an empty string is returned
     */
    public String getName() {
        return (_name == null) ? "" : _name;
    }

    /**
     * Gets the id number of this ChatMember
     * 
     * @return The id number of this user
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     * Sets the ipNumber for this ChatMember
     * 
     * @param ipNr The ip number to bee set
     */
    public void setIpNr(String ipNr) {
        _ipNr = ipNr;
    }

    /**
     * Gets the ipNumber for this ChatMember
     * 
     * @return The ip number fore this ChatMember,
     *         if no number has been set an empty string is returned
     */
    public String getIpNr() {
        return (_ipNr == null) ? "" : _ipNr;
    }

    public String toString() {
        return "Id= " + memberId + " Namn = " + _name;
    }

    public boolean isShowDateTimesEnabled() {
        return showDateTimesEnabled;
    }

    public boolean isShowPrivateMessagesEnabled() {
        return showPrivateMessagesEnabled;
    }

    public boolean isAutoRefreshEnabled() {
        return autoRefreshEnabled;
    }

    public boolean isShowEnterAndLeaveMessagesEnabled() {
        return showEnterAndLeaveMessagesEnabled;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setShowDateTimesEnabled(boolean onOff) {
        this.showDateTimesEnabled = onOff;
    }

    public void setShowPrivateMessagesEnabled(boolean onOff) {
        this.showPrivateMessagesEnabled = onOff;
    }

    public void setShowEnterAndLeaveMessagesEnabled(boolean showEnterAndLeaveMessagesEnabled) {
        this.showEnterAndLeaveMessagesEnabled = showEnterAndLeaveMessagesEnabled;
    }

    public void setAutoRefreshEnabled(boolean onOff) {
        this.autoRefreshEnabled = onOff;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setGroup(ChatGroup group) {
        this.group = group;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMember)) {
            return false;
        }

        final ChatMember chatMember = (ChatMember) o;

        if (memberId != chatMember.memberId) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return memberId;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }

    public User getUser() {
        return user;
    }

    public int compareTo(Object o) {
        ChatMember otherChatMember = ((ChatMember) o);
        return this.getName().compareToIgnoreCase(otherChatMember.getName());
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public int getReferrerMetaId() {
        return referrerMetaId;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public void setTimedOut(boolean timedOut) {
        isTimedOut = timedOut;
    }

    public boolean isKickedOut() {
        return isKickedOut;
    }

    public void setKickedOut(boolean kickedOut) {
        isKickedOut = kickedOut;
    }
}//end class
