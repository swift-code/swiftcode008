package controllers;

import com.avaje.ebean.Expr;
import models.ConnectionRequest;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by lubuntu on 8/21/16.
 */
public class ConnectionController extends Controller {
    public Result SendConnectionRequest(Long senderid, Long toid)
    {
        if(ConnectionRequest.find.where()
                .and(
                        Expr.eq("sender_id", senderid),
                        Expr.eq("receiver_id", toid)
                ).findUnique() == null) {

            ConnectionRequest connectionRequest = new ConnectionRequest();
            connectionRequest.sender = User.find.byId(senderid);
            connectionRequest.receiver = User.find.byId(toid);
            connectionRequest.status = ConnectionRequest.Status.WAITING;

            ConnectionRequest.db().save(connectionRequest);

            User sender = User.find.byId(connectionRequest.sender.id);
            sender.connectionRequestSent.add(connectionRequest);
            User.db().update(sender);

            User receiver = User.find.byId(connectionRequest.receiver.id);
            receiver.connectionRequestReceived.add(connectionRequest);
            User.db().update(receiver);
        }

        return ok();
    }
    public Result acceptConnectionRequest(Long requestId){

        ConnectionRequest connectionRequest = ConnectionRequest.find.byId(requestId);
        connectionRequest.status = ConnectionRequest.Status.ACCEPTED;
        ConnectionRequest.db().update(connectionRequest);

        User sender = User.find.byId(connectionRequest.sender.id);
        sender.connections.add(connectionRequest.receiver);
        User.db().update(sender);

        User receiver = User.find.byId(connectionRequest.receiver.id);
        receiver.connections.add(connectionRequest.sender);
        User.db().update(receiver);

        return ok();
    }
    }
