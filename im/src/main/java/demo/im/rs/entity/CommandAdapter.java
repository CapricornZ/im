package demo.im.rs.entity;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class CommandAdapter implements JsonDeserializer<Command> {

	@Override
	public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject jsonObject = json.getAsJsonObject();
		JsonPrimitive prim = (JsonPrimitive) jsonObject.get("category");
	    String category = prim.getAsString();
		if("READY".equals(category))
			return context.deserialize(jsonObject, Ready.class);
		else if("MESSAGE".equals(category))
			return context.deserialize(jsonObject, Message.class);
		else if("CAPTCHA".equals(category))
			return context.deserialize(jsonObject, Captcha.class);
		else if("REPLY".equals(category))
			return context.deserialize(jsonObject, Reply.class);

	    throw new RuntimeException("Oops");
	}

}
