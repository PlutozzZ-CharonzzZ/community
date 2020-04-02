package life.majiang.commnity.controller;

import life.majiang.commnity.Provider.GithubProvider;
import life.majiang.commnity.dto.AccessTokenDTO;
import life.majiang.commnity.dto.GithubUser;
import life.majiang.commnity.mapper.UserMapper;
import life.majiang.commnity.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller//把当前类作为路由API的承载着
public class AuthorizeController {

    @Autowired//@Component已经把provider放到spring容器里，通过@Autowired自动的把spring容器里写好的实例化的实例加载到当前使用的上下文
    private GithubProvider githubProvider;

    @Value("${github.client.id}")//@Value自动去配置文件中读取值
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;


    @Autowired
    private UserMapper userMapper;



    @GetMapping("/callback")
    //接收code和state参数
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                          HttpServletResponse response){//注入response，cookie

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        /**
         * 如果登录成功，就把token、name、accountId、create、modified写入数据库中
         */
        if (githubUser != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            //登录成功，写cookie和session   cookie好比银行卡，session好比银行账户

            return "redirect:/";//如果不加redirect，那当前地址不会变，只是把页面渲染成index，加了redirect，就会重定向到index
        }else{
            //登陆失败，重新登录
            return "redirect:/";//redirect返回的是路径 所以不能用index，因为index只是一个域名
        }
    }
}
