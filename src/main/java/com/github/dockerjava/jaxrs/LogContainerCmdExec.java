package com.github.dockerjava.jaxrs;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.FrameStreamProcessor;
import com.github.dockerjava.jaxrs.async.AbstractCallbackNotifier;
import com.github.dockerjava.jaxrs.async.GETCallbackNotifier;

public class LogContainerCmdExec extends AbstrDockerCmdExec<LogContainerCmd, Void> implements LogContainerCmd.Exec {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogContainerCmdExec.class);

    public LogContainerCmdExec(WebTarget baseResource) {
        super(baseResource);
    }

    @Override
    protected Void execute(LogContainerCmd command) {
        WebTarget webTarget = getBaseResource().path("/containers/{id}/logs")
                .resolveTemplate("id", command.getContainerId())
                .queryParam("timestamps", command.hasTimestampsEnabled() ? "1" : "0")
                .queryParam("stdout", command.hasStdoutEnabled() ? "1" : "0")
                .queryParam("stderr", command.hasStderrEnabled() ? "1" : "0")
                .queryParam("follow", command.hasFollowStreamEnabled() ? "1" : "0")
                .queryParam("tail", command.getTail() < 0 ? "all" : "" + command.getTail());

        LOGGER.trace("GET: {}", webTarget);

        GETCallbackNotifier<Frame> callbackNotifier = new GETCallbackNotifier<Frame>(new FrameStreamProcessor(),
                command.getResultCallback(), webTarget);

        AbstractCallbackNotifier.startAsyncProcessing(callbackNotifier);

        return null;
    }
}
