package com.love.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.love.config.WechatProperties;
import com.love.model.Message;
import com.love.model.TextMessage;
import com.love.service.ReceiverMessageService;
import com.love.service.RedisService;
import com.thoughtworks.xstream.XStream;

@Service("receiverService")
@Transactional
public class ReceiverMessageServiceImpl implements ReceiverMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverMessageServiceImpl.class);
    private static boolean isFirst = true;

    @Resource
    RedisService redisService;

    @Resource
    WechatProperties wechatProp;

    @Override
    public String receiveMessage(HttpServletRequest request) {
        String respXml = null;

        try {
            Map<String, String> map = xmlToMap(request);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");

            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(String.valueOf(System.currentTimeMillis()));
            textMessage.setMsgType(Message.MESSAGE_TEXT);
            if (Message.MESSAGE_TEXT.equals(msgType)) {
                if (isFirst) {
                    // isFirst = false;
                    textMessage.setContent(replyText());
                    respXml = textMessageToXml(textMessage);
                    LOGGER.info(respXml);
                } else {
                    respXml = "";
                }
            } else if (Message.MESSAGE_EVENT.equals(msgType)) {
                String eventType = map.get("Event");
                switch (eventType) {
                    case Message.MESSAGE_SUBSCRIBE:
                        textMessage.setContent(subscribeText());
                        respXml = textMessageToXml(textMessage);
                        LOGGER.info(respXml);
                        break;
                    case Message.MESSAGE_UNSUBSCRIBE:

                        break;
                    case Message.MESSAGE_CLICK:
                        textMessage.setContent(contactText());
                        respXml = textMessageToXml(textMessage);
                        break;
                    case Message.MESSAGE_VIEW:
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.warn("异常信息：" + e);
        } catch (DocumentException e) {
            LOGGER.warn("异常信息：" + e);
        }
        return respXml;
    }



    /**
     * xml转为map集合
     * 
     * @throws IOException
     * @throws DocumentException
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> xmlToMap(HttpServletRequest request)
            throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();

        InputStream insStream = request.getInputStream();
        Document document = reader.read(insStream);

        Element root = document.getRootElement();

        List<Element> list = root.elements();

        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        insStream.close();
        return map;
    }

    /**
     * 将文本消息对象转为xml
     * 
     * @param textMessage
     * @return
     */
    public String textMessageToXml(TextMessage textMessage) {
        XStream xStream = new XStream();
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }

    /**
     * 默认回复内容
     * 
     * @return
     */
    public String replyText() {
        StringBuffer sb = new StringBuffer();
        sb.append("这里是以爱为名,健康出行客服中心,如果您在使用过程中有任何疑问,请拨打客服电话：18073920866");
        return sb.toString();
    }

    /**
     * 关注回复内容
     * 
     * @return
     */
    public String subscribeText() {
        StringBuffer sb = new StringBuffer();
        sb.append("您好，欢迎关注以爱为名。\n\n");
        sb.append("我们的口号是:以爱为名,健康出行!");
        return sb.toString();
    }

    public String contactText() {
        StringBuffer sb = new StringBuffer();
        sb.append("请直接发送消息，我们将在24小时内给您回复，或拨打客服电话：15974012112");
        return sb.toString();
    }

}
