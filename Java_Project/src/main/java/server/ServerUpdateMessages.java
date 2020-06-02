package server;


import java.util.TimerTask;

public class ServerUpdateMessages extends TimerTask {
    ServerlAppl appl;
    public ServerUpdateMessages(ServerlAppl appl) {
        this.appl = appl;
    }

    @Override
    public void run() {
        ServerUtility.messagesUpdate(appl);
        appl.getPane().setText(appl.messagesToStringAndFilter(appl.getSelectedUser()));
    }
}
