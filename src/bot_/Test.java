package bot_;

import com.vk.api.sdk.client.VkApiClient;

import java.util.HashMap;

public class Test {
    static VKAPI vkapi = new VKAPI();

    public static void main(String[] args) {
        HashMap<String,String> hashMap = new HashMap();
        hashMap.put("12","12");
        System.out.println(hashMap.get("32"));
    }
}
