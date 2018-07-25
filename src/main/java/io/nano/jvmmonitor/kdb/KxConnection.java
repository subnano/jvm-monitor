package io.nano.jvmmonitor.kdb;

import java.io.IOException;

public class KxConnection {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String userPassword;

    private c c;

    public KxConnection(String host, int port) {
        this(host, port, null, null);
    }

    public KxConnection(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.userPassword = userPassword();
    }

    public void connect() {
        try {
            this.c = new c(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KxTableWriterBuilder newTableWriterBuilder() {
        return new KxTableWriterBuilder(this);
    }

    public void update(String table, io.nano.jvmmonitor.kdb.c.Flip flip) {
        try {
            // send asynchronously
            Object result = c.k(".u.upd", table, flip);
            System.out.println("Sent records to kdb server");
        } catch (IOException e) {
            System.err.println("error sending feed to server.");
        } catch (io.nano.jvmmonitor.kdb.c.KException e) {
            System.err.println("error sending feed to server.");
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        c.close();
    }

    private String userPassword() {
        if (user == null || user.length() == 0)
            return null;
        if (password == null || password.length() == 0)
            return user;
        return user + ":" + password;
    }
}
