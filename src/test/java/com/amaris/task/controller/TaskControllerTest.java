package com.amaris.task.controller;

import com.amaris.task.common.ResponseModel;
import com.amaris.task.entity.Task;
import com.amaris.task.service.CommonServiceOps;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@NoArgsConstructor
@RunWith(SpringRunner.class)
public class TaskControllerTest extends CommonServiceOps {

    public static final String GET_ALL_TASK_END_POINT = "/task/all";
    public static final String TASK_BY_ID_END_POINT = "/task/";
    public static final String ADD_TASK_END_POINT = "/task/assign";
    public static final String EDIT_TASK_END_POINT = "/task/edit";

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void getAllTask() {

        webTestClient.get().uri(GET_ALL_TASK_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ResponseModel.class);
    }

    @Test
    public void getTaskById() {
        webTestClient.get().uri(TASK_BY_ID_END_POINT + "1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class);
    }

    @Test
    public void getTaskByIdException() {
        webTestClient.get().uri(TASK_BY_ID_END_POINT + "8")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .isEqualTo("{\"status\":\"KO\",\"payload\":null,\"errors\":\"No task is found with id equals :8\"}");
    }

    @Test
    public void assignTask() {
        Task task = new Task(4,
                "add new task test",
                "03/03/2023",
                2);

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=4, description=add new task test, dueDate=03/03/2023, assignee=2}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void unassignTask() {
        Task task = new Task(1,
                "task unassigned process test",
                "05/03/2023",
                0);

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=task unassigned process test, dueDate=05/03/2023, assignee=0}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void reassignTask() {
        Task task = new Task(1,
                "task reassigned process test",
                "05/03/2023",
                2);

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=task reassigned process test, dueDate=05/03/2023, assignee=2}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void updateDueDate() {
        Task task = new Task(1,
                "task duedate is changed process test",
                "10/03/2023",
                2);

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=task duedate is changed process test, dueDate=10/03/2023, assignee=2}", res.getPayload().get(0).toString());
                });
    }
}
