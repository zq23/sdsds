package bot_;

import com.google.gson.*;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.LongpollParams;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * READ DOCS: https://vk.com/dev/using_longpoll
 */
public class LongPoll implements Runnable{
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private VKAPI vkapi = new VKAPI();
    private GroupActor actor = VKAPI.getGroupActor();

    public void run(){
        LongpollParams lp_params = null;
        try {
            lp_params = vkapi.getVk().messages().getLongPollServer(actor).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

        System.out.println(lp_params);

        HttpUrl.Builder url_builder = HttpUrl.get("https://" + lp_params.getServer()).newBuilder();

        url_builder
                .addQueryParameter("act", "a_check")
                .addQueryParameter("key", lp_params.getKey())
                .addQueryParameter("ts", String.valueOf(lp_params.getTs()))
                .addQueryParameter("mode", "2")
                .addQueryParameter("wait", "60")
                .addQueryParameter("version", "2");


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(70, TimeUnit.SECONDS)
                .build();


        Request.Builder request_builder = new Request.Builder();
        Gson g = new Gson();
        JsonParser parser = new JsonParser();

        do {
            Request request = request_builder.url(url_builder.build()).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    throw new RuntimeException("Wrong HTTP code:" + String.valueOf(response.code()));
                }
                String body = response.body().string();
                System.out.println(body);

                //update "ts" for next request
                JsonObject e = parser.parse(body).getAsJsonObject();
                int ts = e.get("ts").getAsInt();
                url_builder.addQueryParameter("ts", String.valueOf(ts));

                //parse updates
                JsonArray updates = e.get("updates").getAsJsonArray();
                for (int i = 0; i < updates.size(); i++) {
                    JsonArray item = updates.get(i).getAsJsonArray();
                    System.out.println(item);

                    int type = item.get(0).getAsInt();
                    switch (type) {
                        case 4://new message, example: [4,1981041,17,406699503,1533096166,"message body",{"title":" ... "}]
                            int message_id = item.get(1).getAsInt();
                            int message_flags = item.get(2).getAsInt();
                            int message_chat_id = item.get(3).getAsInt();
                            int message_ts = item.get(4).getAsInt();
                            String message_body = item.get(5).getAsString();

                            boolean message_outgoing = (message_flags & 2) != 0;
                            boolean message_in_chat = message_chat_id > 2000000000;
                            if (message_in_chat) {
                                message_chat_id -= 2000000000;
                            }

                            if (message_outgoing) {
                                System.out.println("NEW OUTGOING MESSAGE: " + message_body + message_id);

                            } else {
                                System.out.println("NEW MESSAGE IN " + String.valueOf(message_chat_id) + ":" + message_body);
                                threadPool.submit(new MessageHandlerUser(message_chat_id, message_body));
                            }
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);
    }

}
