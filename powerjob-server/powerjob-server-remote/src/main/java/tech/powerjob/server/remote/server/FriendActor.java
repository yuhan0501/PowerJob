package tech.powerjob.server.remote.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.common.response.AskResponse;
import tech.powerjob.common.serialize.JsonUtils;
import tech.powerjob.remote.framework.actor.Handler;
import tech.powerjob.remote.framework.actor.ProcessType;
import tech.powerjob.server.remote.actoes.ServerActor;
import tech.powerjob.server.remote.server.election.Ping;
import tech.powerjob.server.remote.server.redirector.RemoteProcessReq;
import tech.powerjob.server.remote.server.redirector.RemoteRequestProcessor;

import static tech.powerjob.common.RemoteConstant.*;

/**
 * 处理朋友们的信息（处理服务器与服务器之间的通讯）
 *
 * @author tjq
 * @since 2020/4/9
 */
@Slf4j
@Component
@Handler(path = S4S_PATH)
public class FriendActor implements ServerActor {

    private static final String SK = "dGVuZ2ppcWlAZ21haWwuY29tIA==";

    /**
     * 处理存活检测的请求
     */
    @Handler(path = S4S_HANDLER_PING, processType = ProcessType.NO_BLOCKING)
    public AskResponse onReceivePing(Ping ping) {
        return AskResponse.succeed(SK);
    }

    @Handler(path = S4S_HANDLER_PROCESS, processType = ProcessType.BLOCKING)
    private AskResponse onReceiveRemoteProcessReq(RemoteProcessReq req) {

        AskResponse response = new AskResponse();
        response.setSuccess(true);
        try {
            response.setData(JsonUtils.toBytes(RemoteRequestProcessor.processRemoteRequest(req)));
        } catch (Throwable t) {
            log.error("[FriendActor] process remote request[{}] failed!", req, t);
            response.setSuccess(false);
            response.setMessage(ExceptionUtils.getMessage(t));
        }
        return response;
    }
}
