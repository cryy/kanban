package com.cryy.kanban.test.integration;

import com.cryy.kanban.entities.Task;
import com.cryy.kanban.mapper.TaskMapper;
import com.cryy.kanban.service.TaskService;
import com.cryy.kanban.service.TestStompSessionHandler;
import com.cryy.kanban.ws.TaskEvent;
import com.cryy.kanban.ws.TaskMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebsocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskService taskService;

    private StompSession stompSession;
    private final BlockingQueue<TaskMessage> blockingQueue = new ArrayBlockingQueue<>(1);

    @BeforeEach
    void setUp() throws Exception {
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        stompSession = stompClient.connectAsync("http://localhost:" + port + "/ws",
                new TestStompSessionHandler()).get(5, TimeUnit.SECONDS);
    }
    @Test
    void whenTaskCreated_ShouldReceiveWebSocketNotification() throws Exception {
        stompSession.subscribe("/topic/tasks", new DefaultStompFrameHandler());

        Task task = new Task("WebSocket Test", "Description");
        taskService.createTask(task);

        TaskMessage message = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getEventType()).isEqualTo(TaskEvent.CREATE);
    }

    private class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return TaskMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            blockingQueue.offer((TaskMessage) payload);
        }
    }
}
