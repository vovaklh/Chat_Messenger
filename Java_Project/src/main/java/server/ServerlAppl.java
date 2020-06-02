package server;

import domain.Message;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class ServerlAppl extends JFrame {
    private String selectedUser = "test<-->test";
    private JPanel panel;
    private JList usersSpeech;
    private JTextPane pane;
    private JScrollPane scrollPane, scrollList;
    private Timer timer;
    private Long lastMessageId;
    private DefaultListModel list;
    private Set<Message> messages;
    private String serverIpAddress = "127.0.0.1";
    public static final int FRAME_WIDTH = 700;
    public static final int FRAME_HEIGHT = 600;
    public static final short DELAY = 100;
    public static final short PERIOD = 1000;
    private static String messagesTextPaneColor = "#E5F0FF";
    private static String usersTextPaneColor = "#01142F";

    public ServerlAppl(){
        super();
        Initialize();
    }


    public void Initialize(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setTitle("Server Interface");

        setMessages(new TreeSet<Message>() {
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder("<html><body id = 'body'>");
                Iterator<Message> i = iterator();
                while (i.hasNext()){
                    result.append(i.next().toString()).append("\n");
                }
                return result.append("</body></html>").toString();
            }

        });
        lastMessageId = 0L;


        getUsersSpeech();
        updateMessagesAndUserList();
        getScrollList();
        getPane();
        getScrollPane();
        getPanel().add(getScrollPane(), BorderLayout.CENTER);
        getPanel().add(getScrollList(), BorderLayout.EAST);
        this.setContentPane(getPanel());
    }

    public void setSelectedUser(String selectedUser){
        this.selectedUser = selectedUser;
    }

    public String getSelectedUser(){
        return selectedUser;
    }

    public JPanel getPanel() {
        if(panel == null){
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
        }
        return panel;
    }

    public DefaultListModel getList() {
        if (list == null){
            list = new DefaultListModel();
        }
        return list;
    }

    public JList getUsersSpeech() {
        if(usersSpeech == null){
            usersSpeech = new JList(getList());
            usersSpeech.setFont(new Font("Arial", Font.BOLD, 16));
            usersSpeech.setBackground(Color.decode(usersTextPaneColor));
            usersSpeech.setForeground(Color.decode("#FFFFFF"));
            usersSpeech.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    JList list = (JList)evt.getSource();
                    if (evt.getClickCount() == 1) {
                        setSelectedUser(usersSpeech.getSelectedValue().toString());
                        showMessages(getSelectedUser());
                    }
                }
            });
            usersSpeech.setFixedCellHeight(30);
        }
        return usersSpeech;
    }

    public JScrollPane getScrollList() {
        if(scrollList == null){
            scrollList = new JScrollPane(getUsersSpeech());
            scrollList.setSize(getMaximumSize());
            scrollList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return scrollList;
    }

    public JScrollPane getScrollPane() {
        if(scrollPane == null){
            scrollPane = new JScrollPane(getPane());
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return scrollPane;
    }


    public void updateMessagesAndUserList(){
        ServerUtility.messagesUpdate(this);
        this.setTimer(new Timer());
        this.getTimer().scheduleAtFixedRate(new ServerUpdateMessages(this), DELAY, PERIOD);
    }

    public void addSpeech(){
        for(Message message: messages){
            if(!(list.contains(message.getUserNameFrom() + "<-->" + message.getUserNameTo()))
            && !(list.contains(message.getUserNameTo() + "<-->" + message.getUserNameFrom())))
            {
                list.addElement(message.getUserNameTo() + "<-->" + message.getUserNameFrom());
            }
        }
    }

    public String messagesToStringAndFilter(String value) {
        String[] result = value.split("<-->");
        return buildString(messages.stream().filter(p -> p.getUserNameFrom().equals(result[0]) &&
                p.getUserNameTo().equals(result[1])
                || p.getUserNameFrom().equals(result[1]) &&
                p.getUserNameTo().equals(result[0])).collect(Collectors.toSet()));
    }

    public void showMessages(String value){
        clearFields();
        pane.setText(messagesToStringAndFilter(value));
    }
    public String buildString(Set<Message> messages){
        List<Message> messagesSorted = messages.stream().collect(Collectors.toList());
        Collections.sort(messagesSorted, (x, y) -> x.getMoment().compareTo(y.getMoment()));
        StringBuilder result = new StringBuilder("<html><body id = 'body'>");
        for(Message message: messagesSorted){
            result.append(message.toString()).append("\n");
        }
        return result.append("</body></html>").toString();
    }


    public void clearFields() {
        getPane().setText("");
    }


    public void adddMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        addSpeech();
    }

    public JTextPane getPane() {
        if(pane == null){
            pane = new JTextPane();
            pane.setContentType("text/html");
            pane.setEditable(false);
            pane.setSize(getMaximumSize());
            pane.setBackground(Color.decode(messagesTextPaneColor));
            pane.setFont(new Font("Arial", Font.BOLD, 14));
            ((DefaultCaret)pane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        return pane;
    }


    public String getServerIpAddress()
    {
        return serverIpAddress;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }
    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    public Timer getTimer() {
        return timer;
    }

    public static void main(String[] args) {
        JFrame frame = new ServerlAppl();
        frame.setVisible(true);
        frame.repaint();
    }
}
