package life.majiang.commnity.controller;

import life.majiang.commnity.Provider.GithubProvider;
import life.majiang.commnity.dto.AccessTokenDTO;
import life.majiang.commnity.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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



    @GetMapping("/callback")
    //接收code和state参数
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state){

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";//登录成功后返回index页面

    }
}
