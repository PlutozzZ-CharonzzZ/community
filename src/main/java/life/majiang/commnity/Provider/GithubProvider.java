package life.majiang.commnity.Provider;

/**
 * 整个provider包是希望这个包能提供对第三方支持的能力
 */

import com.alibaba.fastjson.JSON;
import life.majiang.commnity.dto.AccessTokenDTO;
import life.majiang.commnity.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component//把当前的类初始化到spring容器的上下文，泛指各种组件，就是说当我们的类不属于各种归类的时候（不属于@Controller、@Service的时候），我们就可以用@Compoent来标注这个类
//通俗来讲，加了@Component之后，对象就自动实例化放到一个池子里面，当我们去用的时候，很轻松的就能通过这个名字拿出来用
/**
 * 传递 client_id;
 *     client_secret;
 *     code;
 *     redirect_uri;
 *     state;参数，调用https://github.com/login/oauth/access_token地址，获取access_token
 */
public class GithubProvider {
    /**
     * 此方法用来获取accesstoken
     * @param accessTokenDTO
     * @return
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
         MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));//string类型的json
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();//获取到的accesstoken
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法通过accesstoken返回用户信息
     * @param accessToken
     * @return
     */
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();//返回的user信息
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);//把string的json对象去自动转换解析为java的类对象
            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }
}
