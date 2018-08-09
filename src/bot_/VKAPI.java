package bot_;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VKAPI {

    private static TransportClient transportClient = HttpTransportClient.getInstance();
    private static VkApiClient vk = new VkApiClient(transportClient);

    private static final String TOKEN_BOT = "token";
    private static final int GROUP_ID = 0;
    private static DataBase db = new DataBase();
    private static ArrayList<Integer> idsAdmin = new ArrayList<>();

    public static DataBase getDB() {
        return db;
    }

    public static VkApiClient getVk() {
        return vk;
    }

    private static final GroupActor GROUP_ACTOR = new GroupActor(GROUP_ID,TOKEN_BOT);

    public static String getTokenBot() {
        return TOKEN_BOT;
    }

    public static ArrayList<Integer> getIdsAdmin() {
        return idsAdmin;
    }

    public static int getGroupId() {
        return GROUP_ID;
    }

    public static GroupActor getGroupActor() {
        return GROUP_ACTOR;
    }

    public static void main(String[] args) throws ClientException, ApiException {
        Executors.newSingleThreadExecutor().submit(new LongPoll());
        idsAdmin.add(387969138);
        idsAdmin.add(406699503);
    }

}
