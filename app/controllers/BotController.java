package controllers;

import bots.Configuration;
import bots.FacebookBot;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Map;

public class BotController extends Controller {

    private final FacebookBot facebookBot;

    @Inject
    public BotController(FacebookBot facebookBot) {
        this.facebookBot = facebookBot;
    }

    public Result index() {
        return ok("Hello Bots!");
    }

    public Result facebookVerify() {
        Map<String, String[]> queryString = request().queryString();

        try {
            String verifyToken = queryString.get("hub.verify_token")[0];
            String challenge = queryString.get("hub.challenge")[0];
            Logger.warn("Facebook Verified");

            if (verifyToken.equals(Configuration.facebookBotVerification)) {
                return ok(challenge);
            }
            return unauthorized("Error, wrong token");
        } catch (Exception e) {
            Logger.error("Exception verification, parameters are not suitable.", e);
            return unauthorized("Error, missing parameters");
        }
    }

    public Result receiveFacebookMessage() {
        JsonNode json = request().body().asJson();
        boolean isReceived = facebookBot.receive(json);
        if(isReceived) {
            return ok();
        }
        else {
            return internalServerError();
        }
    }

}
