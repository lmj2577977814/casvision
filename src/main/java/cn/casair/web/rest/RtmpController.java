package cn.casair.web.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/rtmp")
public class RtmpController {
    /**
     * 改方法用来对推流做认证，流媒体服务器会自动推送web请求到该地址，如果推流token合法，正常返回，如果非法，返回一错误码即可
     * @param response
     * @param pushToken
     */
   @RequestMapping("/pushAuth")
    public  void  pushAuth( HttpServletResponse response,@RequestParam("pushToken")String pushToken){
            if ("".equals(pushToken) || pushToken==null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            //TODO 下面增加token的校验逻辑
            if("11".equals(pushToken)){

            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
    }

    @RequestMapping("/playAuth")
    public void  playAuth(HttpServletResponse response,@RequestParam("playToken")String playToken){
        if ("".equals(playToken) || playToken==null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if ("22".equals(playToken)){

        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
