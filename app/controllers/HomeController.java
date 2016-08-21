package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Profile;
import models.User;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by lubuntu on 8/21/16.
 */

public class HomeController extends Controller {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    FormFactory formFactory;

 public Result getProfile(Long userId){
     User user=User.find.byId(userId);
     Profile profile=Profile.find.byId(user.profile.id);
     ObjectNode data=objectMapper.createObjectNode();
     data.set("suggestions",null);
     List<Long> connectedUserIds = user.connections.stream().map(x->x.id).collect(Collectors.toList());
     List<Long> connectionRequestsSentUserIds = user.connectionRequestSent.stream()
             .map(x->x.receiver.id).collect(Collectors.toList());
     List<JsonNode> suggestions = User.find.all().stream()
             .filter(x->!connectedUserIds.contains(x.id)&&!connectionRequestsSentUserIds.contains(x.id)
                     && !Objects.equals(x.id, userId))
                     .map(x->{
                         ObjectNode userJson = objectMapper.createObjectNode();
                         userJson.put("email",x.email);
                         userJson.put("id",x.id);
                         return userJson;
                     })
             .collect(Collectors.toList());

     return ok();
 }

}
